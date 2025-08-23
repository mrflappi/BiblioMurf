package net.murfgames.bibliomurf.customasset;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.murfgames.bibliomurf.BiblioMurf;
import net.murfgames.bibliomurf.mixin.client.FontManagerAccessor;
import net.murfgames.bibliomurf.mixin.client.MinecraftClientAccessor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Loads fonts referenced in fonts.json in the root minecraft fonts folder and creates text renderers to display them. <br><br>
 * Modified from davidsaltacc's <a href="https://gist.github.com/davidsaltacc/35d6a606b59dfeecfac1662bec4ce446">solution</a>
 **/
public class CustomFontLoader extends CustomAssetLoader {

    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final String FONTS_JSON = "fonts.json";

    /** A unique TextRenderer for each font loaded */
    private final Map<String, TextRenderer> _textRenderers = new HashMap<>();

    @Override
    protected void _onReload(ResourceManager manager) {
        try {
            // Load FontMap from fonts.json file
            LoadedFontMap[] loadedFontMaps = _loadAllJSONResourcesWithId(manager, LoadedFontMap.class, FONTS_JSON);

            // Initialise text renderers
            for (LoadedFontMap loadedFontMap : loadedFontMaps) {
                _initialiseTextRenderers(loadedFontMap.fonts);
            }
        } catch (Exception e) {
            BiblioMurf.LOGGER.error("Failed to load custom fonts", e);
        }
    }

    /** Creates and stores a TextRenderer for each font given in a map */
    private void _initialiseTextRenderers(String[] fonts) {
        _textRenderers.clear();

        for (String font: fonts) {
            TextRenderer renderer = client.textRenderer;
            try {
                Pair<TextRenderer, Boolean> textRendererAndDefault = _createTextRenderer(font);
                renderer = textRendererAndDefault.getLeft();
                if (textRendererAndDefault.getRight()) {
                    BiblioMurf.LOGGER.error("Error initializing TTF renderer, defaulting to minecraft font");
                }
            } catch (Exception e) {
                BiblioMurf.LOGGER.error("Could not initialise text renderer with id {}", font, e);
            }
            _textRenderers.put(font, renderer);
        }
    }

    /** Creates a TextRenderer from the fontPath by accessing FontStorage. Also returns if the returned TextRenderer is the default one in case the given fontPath could not be loaded */
    private static Pair<TextRenderer, Boolean> _createTextRenderer(String fontPath) {
        FontManagerAccessor fontManager = ((FontManagerAccessor) ((MinecraftClientAccessor) MinecraftClient.getInstance()).getFontManager());
        AtomicBoolean isDefault = new AtomicBoolean(false);
        TextRenderer textRenderer = new TextRenderer(id -> {
            FontStorage storage = fontManager.getFontStorages().getOrDefault(Identifier.of(fontPath), fontManager.getFontStorages().getOrDefault(Identifier.of("default"), fontManager.getMissingStorage()));
            if (storage == fontManager.getFontStorages().get(Identifier.of("default"))) {
                isDefault.set(true);
            }
            return storage;
        }, true);
        return new Pair<>(textRenderer, isDefault.get());
    }

    public TextRenderer getTextRenderer(String fontId) {
        return _textRenderers.getOrDefault(fontId, client.textRenderer);
    }

    @Override
    protected String _getFabricId() {
        return "font_assets";
    }

    private static class LoadedFontMap {
        public String[] fonts;
    }
}
