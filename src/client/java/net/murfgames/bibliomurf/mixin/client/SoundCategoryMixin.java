package net.murfgames.bibliomurf.mixin.client;

import com.mojang.datafixers.util.Pair;
import net.minecraft.sounds.SoundSource;
import net.murfgames.bibliomurf.BiblioMurf;
import net.murfgames.bibliomurf.soundcategories.CustomSoundCategories;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundSource.class)
@Unique
public abstract class SoundCategoryMixin {

    @Invoker("<init>")
    public static SoundSource create(String enumName, int ordinal, String name) {
        throw new AssertionError();
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void onInit(CallbackInfo ci) {
        int ordinal = SoundSource.values().length;

        for (Pair<String, String> categoryName : CustomSoundCategories.getCategoryNames()) {
            SoundSource customCategory = create(categoryName.getFirst(), ordinal, categoryName.getSecond());
            CustomSoundCategories.addSoundCategory(customCategory);
            ordinal++;
        }

        CustomSoundCategories.onSoundsInitialized();
        if (ordinal > SoundSource.values().length)
            BiblioMurf.LOGGER.info("Loaded custom sound categories: {}", CustomSoundCategories.getCategoryInternalNames());
        else
            BiblioMurf.LOGGER.info("No custom sound categories were loaded.");
    }
}
