package net.murfgames.bibliomurf.mixin.client;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.sounds.SoundSource;
import net.murfgames.bibliomurf.soundcategories.CustomOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Map;

@Mixin(Options.class)
public class GameOptionsMixin {

    @Shadow
    private Map<SoundSource, OptionInstance<Double>> soundSourceVolumes;

    @Inject(method = "getSoundSourceOptionInstance(Lnet/minecraft/sounds/SoundSource;)Lnet/minecraft/client/OptionInstance;", at = @At("HEAD"), cancellable = true)
    private void inject_getSoundVolumeOption(SoundSource category, CallbackInfoReturnable<OptionInstance<Double>> info) {
        // Check our custom options map first
        if (CustomOptions.containsSoundVolumeOption(category)) {
            info.setReturnValue(CustomOptions.getSoundVolumeOption(category));
            info.cancel();
            return;
        }

        // Only touch vanilla map if it's a real vanilla category
        if (category.ordinal() < SoundSource.values().length) {
            info.setReturnValue(soundSourceVolumes.get(category));
            info.cancel();
            return;
        }

        // Fallback: MASTER
        info.setReturnValue(soundSourceVolumes.get(SoundSource.MASTER));
        info.cancel();
    }
}
