package net.murfgames.bibliomurf.mixin.client;

import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameOptions.class)
public interface GameOptionsAccessor {

    @Invoker("getPercentValueOrOffText")
    static Text getPercentValueOrOffText(Text prefix, double value) {
        throw new AssertionError();
    }
}

