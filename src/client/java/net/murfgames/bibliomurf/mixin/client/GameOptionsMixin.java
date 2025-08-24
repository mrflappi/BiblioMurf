package net.murfgames.bibliomurf.mixin.client;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.sound.SoundCategory;
import net.murfgames.bibliomurf.soundcategories.CustomOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Map;

@Mixin(GameOptions.class)
public class GameOptionsMixin {

    @Shadow
    private Map<SoundCategory, SimpleOption<Double>> soundVolumeLevels;

    @Inject(method = "getSoundVolumeOption(Lnet/minecraft/sound/SoundCategory;)Lnet/minecraft/client/option/SimpleOption;", at = @At("HEAD"), cancellable = true)
    private void inject_getSoundVolumeOption(SoundCategory category, CallbackInfoReturnable<SimpleOption<Double>> info) {
        // Check our custom options map first
        if (CustomOptions.containsSoundVolumeOption(category)) {
            info.setReturnValue(CustomOptions.getSoundVolumeOption(category));
            info.cancel();
            return;
        }

        // Only touch vanilla map if it's a real vanilla category
        if (category.ordinal() < SoundCategory.values().length) {
            info.setReturnValue(soundVolumeLevels.get(category));
            info.cancel();
            return;
        }

        // Fallback: MASTER
        info.setReturnValue(soundVolumeLevels.get(SoundCategory.MASTER));
        info.cancel();
    }
}
