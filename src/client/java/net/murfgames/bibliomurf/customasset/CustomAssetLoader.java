package net.murfgames.bibliomurf.customasset;

import com.google.gson.Gson;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import net.murfgames.bibliomurf.BiblioMurf;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;

/**
 * Template for loading custom assets from resources on client reload
 */
public abstract class CustomAssetLoader {

    private static final JSONParser _jsonParser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
    private static final Gson _gson = new Gson();

    public void onInitialise() {

        // Register this asset loader as a reload listener so assets are loaded at the correct time
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {

            @Override
            public Identifier getFabricId() {
                return Identifier.of(BiblioMurf.MOD_ID, _getFabricId());
            }

            @Override
            public void reload(ResourceManager manager) {
                _onReload(manager);
            }
        });
    }

    /**
     * Loads JSON resources found under the given assets path and parses them into a given type
     * @param manager The ResourceManager which loads resources from the assets folder
     * @param loadingClass The class of the object that the loaded resources should be parsed into
     * @param path The path in the assets folder to load from, includes all namespaces
     * @param pathLoadAllowed A predicate which acts as a filter for which resource paths will be returned
     * @return Map of loaded resources. Keys are identifiers generated from the resource's path under the given <b>path</b>
     * @param <T> The type of object that should be returned: must be the same class or be inherited by the <b>loadingClass</b>
     */
    protected <T> Map<Identifier, T> _loadJSONResourcesFromPath(@NotNull ResourceManager manager, @NotNull Class<T> loadingClass, String path, Predicate<Identifier> pathLoadAllowed) {
        // Store successfully parsed resources
        Map<Identifier, T> resourceMap = new HashMap<>();

        for(Identifier id : manager.findResources(path, pathLoadAllowed).keySet()) {
            // Attempt to load each resource found in the given path
            try {
                // Load the resource
                T loadedResource = _loadJSONResourceFromId(manager, loadingClass, id);

                // Create asset ID
                Identifier newId = _generateId(id, path);

                // Add to map
                resourceMap.put(newId, loadedResource);
            } catch(Exception e) {
                BiblioMurf.LOGGER.error("Error occurred while loading resource json {}", id.toString(), e);
            }
        }

        // Return converted Array
        return resourceMap;
    }

    /**
     * Loads all JSON resources found with the specified id and parses them into a given type. Checks all present namespaces
     * @param manager The ResourceManager which loads resources from the assets folder
     * @param loadingClass The class of the object that the loaded resources should be parsed into
     * @param id The specific identifier of the resources to be loaded
     * @return Array of loaded and parsed objects
     * @param <T> The type of object that should be returned: must be the same class or be inherited by the <b>loadingClass</b>
     */
    @SuppressWarnings("unchecked")
    protected <T> T[] _loadAllJSONResourcesWithId(@NotNull ResourceManager manager, @NotNull Class<T> loadingClass, String id) {
        ArrayList<T> loaded = new ArrayList<>();

        for (String namespace: manager.getAllNamespaces()) {
            List<Resource> resources = manager.getAllResources(Identifier.of(namespace, id));

            for (Resource resource : resources) {
                try {
                    T loadedResource = _parseJSONResource(resource, loadingClass);
                    loaded.add(loadedResource);
                    resource.getInputStream().close();
                } catch (Exception e) {
                    BiblioMurf.LOGGER.error("Failed to load JSON resource with id {}", id, e);
                }
            }
        }

        return loaded.toArray((T[]) Array.newInstance(loadingClass, loaded.size()));
    }

    /**
     * Loads a specific JSON resource from the specified id and parses it into a given type
     * @param manager The ResourceManager which loads resources from the assets folder
     * @param loadingClass The class of the object that the loaded resource should be parsed into
     * @param id The specific identifier of the resource to be loaded
     * @return Loaded and parsed object of the given type
     * @param <T> The type of object that should be returned: must be the same class or be inherited by the <b>loadingClass</b>
     * @throws IOException
     * @throws ParseException
     */
    protected <T> T _loadJSONResourceFromId(@NotNull ResourceManager manager, @NotNull Class<T> loadingClass, Identifier id) throws IOException, ParseException {
        // Attempt to load resource found in the given path
        Optional<Resource> resource = manager.getResource(id);
        if (resource.isEmpty()) throw new RuntimeException("Resource could not be found");

        // Convert InputStream to the given type
        T parsedResource = _parseJSONResource(resource.get(), loadingClass);
        resource.get().getInputStream().close();
        return parsedResource;
    }

    /**
     * Directly parses a resource formated as a JSON file into an object of a given type
     * @param resource The resource to be parsed, should be in a valid JSON format
     * @param loadingClass The class of the object that the loaded resource should be parsed into
     * @return Parsed object of the given type
     * @param <T> The type of object that should be returned: must be the same class or be inherited by the <b>loadingClass</b>
     * @throws IOException
     * @throws ParseException
     */
    protected <T> T _parseJSONResource(@NotNull Resource resource, @NotNull Class<T> loadingClass) throws IOException, ParseException {
        InputStream stream = resource.getInputStream();

        // Convert InputStream to JSONObject
        JSONObject jsonObject = (JSONObject)_jsonParser.parse(
                new InputStreamReader(stream, StandardCharsets.UTF_8)
        );

        // Parse into object of given class and type
        T loadedResource = _gson.fromJson(jsonObject.toString(), loadingClass);
        if (loadedResource == null) throw new RuntimeException("Parsed resource returned null");

        return loadedResource;
    }

    private @NotNull Identifier _generateId(@NotNull Identifier resourceId, @NotNull String rootPath) {
        String newPath = resourceId.getPath().substring(rootPath.length() + 1);
        newPath = newPath.replace(".json", "");

        return Identifier.of(resourceId.getNamespace(), newPath);
    }

    /**
     * Called when assets are reloaded (e.g. resource packs changed)
     * @param manager The ResourceManager which loads resources from the assets folder
     */
    protected abstract void _onReload(ResourceManager manager);

    /**
     * @return The fabricId registered in the reload listener
     */
    protected abstract String _getFabricId();

}
