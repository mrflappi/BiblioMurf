package net.murfgames.bibliomurf.soundcategories;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Pair;
import net.murfgames.bibliomurf.BiblioMurf;
import org.jetbrains.annotations.NotNull;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class CustomSoundCategories {
    private static final Map<String, SoundCategory> CUSTOM_SOUND_CATEGORIES = new HashMap<>();
    private static final Map<String, String> CATEGORY_NAMES = new HashMap<>();

    // Load category names from JSON
    static {
        loadFromMods();
    }

    // I can't lie, I used ChatGPT for this, I am not smart enough to figure ts out.
    // Please don't hunt me I'm just a boy who wants to add a new sound category to minecraft ;-;
    private static void loadFromMods() {
        Gson gson = new Gson();
        Set<String> seenNames = new HashSet<>();

        try {
            Enumeration<URL> resources = CustomSoundCategories.class.getClassLoader().getResources("sound_categories.json");

            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                try (InputStreamReader reader = new InputStreamReader(url.openStream())) {
                    Map<String, Map<String, String>> map = gson.fromJson(reader, new TypeToken<Map<String, Map<String, String>>>() {}.getType());
                    Map<String, String> categories = map.get("categories");

                    if (categories != null) {
                        for (String enumName : categories.keySet()) {
                            if (seenNames.contains(enumName)) {
                                BiblioMurf.LOGGER.warn("[Warning] Duplicate SoundCategory detected: {} (skipping duplicate)", enumName);
                            } else {
                                CATEGORY_NAMES.put(enumName, categories.get(enumName));
                                seenNames.add(enumName);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            BiblioMurf.LOGGER.error("Failed to load custom sound categories", e);
        }
    }

    public static List<String> getCategoryInternalNames() {
        return CATEGORY_NAMES.keySet().stream().toList();
    }

    public static @NotNull List<Pair<String, String>> getCategoryNames() {
        List<Pair<String, String>> contents = new ArrayList<>();
        for (String internalName: getCategoryInternalNames()) {
            contents.add(new Pair<>(internalName, CATEGORY_NAMES.get(internalName)));
        }
        return contents;
    }

    public static void addSoundCategory(SoundCategory category) {
        CUSTOM_SOUND_CATEGORIES.put(category.name(), category);
    }

    public static SoundCategory get(String name) {
        return CUSTOM_SOUND_CATEGORIES.get(name);
    }

    public static Optional<SoundCategory> getSafe(String name) {
        if (CUSTOM_SOUND_CATEGORIES.containsKey(name))
            return Optional.of(get(name));
        else return Optional.empty();
    }
}
