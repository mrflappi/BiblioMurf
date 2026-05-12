package net.murfgames.bibliomurf.mixin.client;

import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Options.class)
public interface GameOptionsAccessor {

    @Invoker("percentValueOrOffLabel")
    static Component getPercentValueOrOffText(Component prefix, double value) {
        throw new AssertionError();
    }
}

