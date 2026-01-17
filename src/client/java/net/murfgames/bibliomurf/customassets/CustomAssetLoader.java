package net.murfgames.bibliomurf.customassets;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.murfgames.bibliomurf.BiblioMurf;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;

/**
 * Template for loading custom assets from resources on client reload
 */
public abstract class CustomAssetLoader {

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
     * @param codec The codec used to parse the JSON object into a resource
     * @param path The path in the assets folder to load from, includes all namespaces
     * @param pathLoadAllowed A predicate which acts as a filter for which resource paths will be returned
     * @return Map of loaded resources. Keys are identifiers generated from the resource's path under the given <b>path</b>
     * @param <T> The type of object that should be returned: must be the same class or be inherited by the <b>codec</b>
     */
    protected <T> Map<Identifier, T> _loadJSONResourcesFromPath(@NotNull ResourceManager manager, @NotNull Codec<T> codec, String path, Predicate<Identifier> pathLoadAllowed) {
        // Store successfully parsed resources
        Map<Identifier, T> resourceMap = new HashMap<>();

        for(Identifier id : manager.findResources(path, pathLoadAllowed).keySet()) {
            // Attempt to load each resource found in the given path
            try {
                // Load the resource
                T loadedResource = _loadJSONResourceFromId(manager, codec, id);

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
     * @param codec The codec used to parse the JSON object into a resource
     * @param id The specific identifier of the resources to be loaded
     * @return Array of loaded and parsed objects
     * @param <T> The type of object that should be returned: must be the same class or be inherited by the <b>codec</b>
     */
    @SuppressWarnings("unchecked")
    protected <T> Codec<T>[] _loadAllJSONResourcesWithId(@NotNull ResourceManager manager, @NotNull Codec<T> codec, String id) {
        ArrayList<T> loaded = new ArrayList<>();

        for (String namespace: manager.getAllNamespaces()) {
            List<Resource> resources = manager.getAllResources(Identifier.of(namespace, id));

            for (Resource resource : resources) {
                try {
                    T loadedResource = _parseJSONResource(resource, codec);
                    loaded.add(loadedResource);
                    resource.getInputStream().close();
                } catch (Exception e) {
                    BiblioMurf.LOGGER.error("Failed to load JSON resource with id {}", id, e);
                }
            }
        }

        return (Codec<T>[]) loaded.toArray();
    }

    /**
     * Loads a specific JSON resource from the specified id and parses it into a given type
     * @param manager The ResourceManager which loads resources from the assets folder
     * @param codec The codec used to parse the JSON object into a resource
     * @param id The specific identifier of the resource to be loaded
     * @return Loaded and parsed object of the given type
     * @param <T> The type of object that should be returned: must be the same class or be inherited by the <b>codec</b>
     * @throws IOException
     */
    protected <T> T _loadJSONResourceFromId(@NotNull ResourceManager manager, @NotNull Codec<T> codec, Identifier id) throws IOException {
        // Attempt to load resource found in the given path
        Optional<Resource> resource = manager.getResource(id);
        if (resource.isEmpty()) throw new RuntimeException("Resource could not be found");

        // Convert InputStream to the given type
        T parsedResource = _parseJSONResource(resource.get(), codec);
        resource.get().getInputStream().close();
        return parsedResource;
    }

    /**
     * Directly parses a resource formated as a JSON file into an object of a given type
     * @param resource The resource to be parsed
     * @param codec The codec used to parse the JSON object into a resource
     * @return Parsed object of the given type
     * @param <T> The type of object that should be returned: must be the same class or be inherited by the <b>codec</b>
     * @throws IOException
     */
    protected <T> T _parseJSONResource(@NotNull Resource resource, @NotNull Codec<T> codec) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            JsonElement jsonElement = JsonParser.parseReader(reader);
            DynamicOps<JsonElement> ops = JsonOps.INSTANCE;
            DataResult<T> result = codec.parse(ops, jsonElement);

            return result.getOrThrow();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON resource", e);
        }
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
