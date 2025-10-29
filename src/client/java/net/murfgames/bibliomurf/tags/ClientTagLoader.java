package net.murfgames.bibliomurf.tags;

import net.minecraft.registry.tag.TagFile;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.murfgames.bibliomurf.BiblioMurf;
import net.murfgames.bibliomurf.customassets.CustomAssetLoader;

import java.util.*;

public class ClientTagLoader extends CustomAssetLoader {
    private static final String TAGS_PATH = "tags";

    private final Map<Identifier, TagFile> _tags = new HashMap<>();
    private final Map<String, List<Identifier>> _cacheTagQueries = new HashMap<>();

    @Override
    protected void _onReload(ResourceManager manager) {
        _tags.clear();
        _cacheTagQueries.clear();

        try {
            Map<Identifier, TagFile> map = _loadJSONResourcesFromPath(manager, TagFile.CODEC, TAGS_PATH, path -> path.toString().endsWith(".json"));
            _tags.putAll(map);
        } catch (Exception e) {
            BiblioMurf.LOGGER.error("Failed to load client-side tags", e);
        }

        for (Identifier id: _tags.keySet()) {
            BiblioMurf.LOGGER.info(id + ", " + _tags.get(id));
        }
    }

    @Override
    protected String _getFabricId() {
        return "client_tags";
    }

    public boolean hasTag(String id, Identifier tag) {
        return _tags.get(tag).entries().contains(id);
    }

    public List<Identifier> getTagsWithEntry(String id) {
        if (_cacheTagQueries.containsKey(id))
            return _cacheTagQueries.get(id);

        List<Identifier> tags = new ArrayList<>();
        for (Identifier tag: _tags.keySet()) {
            if (hasTag(id, tag))
                tags.add(tag);
        }

        _cacheTagQueries.put(id, tags);
        return tags;
    }
}
