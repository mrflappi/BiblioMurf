package net.murfgames.bibliomurf.mixin.client;

import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Pair;
import net.murfgames.bibliomurf.BiblioMurf;
import net.murfgames.bibliomurf.soundcategories.CustomSoundCategories;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundCategory.class)
@Unique
public abstract class SoundCategoryMixin {

    @Invoker("<init>")
    public static SoundCategory create(String enumName, int ordinal, String name) {
        throw new AssertionError();
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void onInit(CallbackInfo ci) {
        int ordinal = SoundCategory.values().length;

        for (Pair<String, String> categoryName : CustomSoundCategories.getCategoryNames()) {
            SoundCategory customCategory = create(categoryName.getLeft(), ordinal, categoryName.getRight());
            CustomSoundCategories.addSoundCategory(customCategory);
            ordinal++;
        }

        if (ordinal > SoundCategory.values().length)
            BiblioMurf.LOGGER.info("Loaded custom sound categories: {}", CustomSoundCategories.getCategoryInternalNames());
        else
            BiblioMurf.LOGGER.info("No custom sound categories were loaded.");
    }
}
