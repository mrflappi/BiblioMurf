package net.murfgames.bibliomurf.mixin.client;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.options.SoundOptionsScreen;
import net.murfgames.bibliomurf.soundcategories.CustomOptions;
import net.murfgames.bibliomurf.soundcategories.CustomSoundCategories;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.stream.Stream;

@Mixin(SoundOptionsScreen.class)
public abstract class SoundOptionsScreenMixin {

    @Inject(method = "addOptions", at = @At("HEAD"))
    private void registerCustomOptions(CallbackInfo ci) {
        CustomOptions.registerCustomOptions();
    }

    @Inject(method = "getAllSoundOptionsExceptMaster", at = @At("RETURN"), cancellable = true)
    private void injectVolumeOptions(CallbackInfoReturnable<OptionInstance<?>[]> cir) {
        OptionInstance<?>[] original = cir.getReturnValue();

        OptionInstance<?>[] customOptions = CustomSoundCategories.getCategoryInternalNames().stream()
                .map(CustomSoundCategories::get)
                .filter(cat -> cat != null && cat != net.minecraft.sounds.SoundSource.MASTER)
                .map(CustomOptions::getSoundVolumeOption)
                .toArray(OptionInstance[]::new);

        OptionInstance<?>[] combined = Stream.concat(Arrays.stream(original), Arrays.stream(customOptions))
                .toArray(OptionInstance[]::new);

        cir.setReturnValue(combined);
    }
}

