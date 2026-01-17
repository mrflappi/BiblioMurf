package net.murfgames.bibliomurf.soundcategories;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.sound.SoundPreviewer;
import net.minecraft.sound.SoundCategory;
import net.murfgames.bibliomurf.mixin.client.GameOptionsAccessor;

import java.util.*;
import java.util.stream.Collectors;

public class CustomOptions {

    private static final Map<SoundCategory, SimpleOption<Double>> soundVolumeLevels = new HashMap<>();

    public static void registerCustomOptions() {
        List<SoundCategory> customCategories = CustomSoundCategories.getCategoryInternalNames().stream()
                .map(CustomSoundCategories::get)
                .filter(Objects::nonNull)
                .toList();

        for (SoundCategory category : customCategories) {
            if (!soundVolumeLevels.containsKey(category)) {
                SimpleOption<Double> option = new SimpleOption<>(
                        "soundCategory.custom." + category.getName(),
                        s -> null,
                        GameOptionsAccessor::getPercentValueOrOffText,
                        SimpleOption.DoubleSliderCallbacks.INSTANCE,
                        1.0,
                        value -> {
                            MinecraftClient client = MinecraftClient.getInstance();
                            client.getSoundManager().refreshSoundVolumes(category);

                            if (client.world == null) {
                                SoundPreviewer.preview(client.getSoundManager(), category, value.floatValue());
                            }
                        }
                );

                soundVolumeLevels.put(category, option);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static SimpleOption<Double> getSoundVolumeOption(SoundCategory category) {
        return (SimpleOption<Double>) Objects.requireNonNull((SimpleOption) soundVolumeLevels.get(category));
    }

    public static boolean containsSoundVolumeOption(SoundCategory category) {
        return soundVolumeLevels.containsKey(category);
    }
}
