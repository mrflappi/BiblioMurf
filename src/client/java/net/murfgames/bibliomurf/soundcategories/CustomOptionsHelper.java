package net.murfgames.bibliomurf.soundcategories;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.sound.SoundCategory;
import net.murfgames.bibliomurf.mixin.client.GameOptionsAccessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class CustomOptionsHelper {

    private static final Map<SoundCategory, SimpleOption<Double>> customSoundVolumeLevels = new HashMap<>();

    public static void registerCustomOptions() {
        List<SoundCategory> customCategories = CustomSoundCategories.getCategoryInternalNames().stream()
                .map(CustomSoundCategories::get)
                .filter(cat -> cat != null)
                .collect(Collectors.toList());

        for (SoundCategory category : customCategories) {
            if (!customSoundVolumeLevels.containsKey(category)) {
                SimpleOption<Double> option = new SimpleOption<>(
                        "soundCategory.custom." + category.getName(),
                        s -> null,
                        GameOptionsAccessor::getPercentValueOrOffText,
                        SimpleOption.DoubleSliderCallbacks.INSTANCE,
                        1.0,
                        value -> MinecraftClient.getInstance()
                                .getSoundManager()
                                .updateSoundVolume(category, value.floatValue())
                );

                customSoundVolumeLevels.put(category, option);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static SimpleOption<Double> getSoundVolumeOption(SoundCategory category) {
        return (SimpleOption<Double>) Objects.requireNonNull((SimpleOption)customSoundVolumeLevels.get(category));
    }
}
