package net.murfgames.bibliomurf.soundcategories;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.sounds.SoundPreviewHandler;
import net.minecraft.sounds.SoundSource;
import net.murfgames.bibliomurf.mixin.client.GameOptionsAccessor;

import java.util.*;
import java.util.stream.Collectors;

public class CustomOptions {

    private static final Map<SoundSource, OptionInstance<Double>> soundVolumeLevels = new HashMap<>();

    public static void registerCustomOptions() {
        List<SoundSource> customCategories = CustomSoundCategories.getCategoryInternalNames().stream()
                .map(CustomSoundCategories::get)
                .filter(Objects::nonNull)
                .toList();

        for (SoundSource category : customCategories) {
            if (!soundVolumeLevels.containsKey(category)) {
                OptionInstance<Double> option = new OptionInstance<>(
                        "soundCategory.custom." + category.getName(),
                        s -> null,
                        GameOptionsAccessor::getPercentValueOrOffText,
                        OptionInstance.UnitDouble.INSTANCE,
                        1.0,
                        value -> {
                            Minecraft client = Minecraft.getInstance();
                            client.getSoundManager().refreshCategoryVolume(category);

                            if (client.level == null) {
                                SoundPreviewHandler.preview(client.getSoundManager(), category, value.floatValue());
                            }
                        }
                );

                soundVolumeLevels.put(category, option);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static OptionInstance<Double> getSoundVolumeOption(SoundSource category) {
        return (OptionInstance<Double>) Objects.requireNonNull((OptionInstance) soundVolumeLevels.get(category));
    }

    public static boolean containsSoundVolumeOption(SoundSource category) {
        return soundVolumeLevels.containsKey(category);
    }
}
