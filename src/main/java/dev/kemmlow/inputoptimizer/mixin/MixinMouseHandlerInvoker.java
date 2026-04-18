package dev.kemmlow.inputoptimizer.mixin;

import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MouseHandler.class)
public interface MixinMouseHandlerInvoker {
    @Invoker("onButton")
    void invokeOnButton(long window, MouseButtonInfo buttonInfo, int action);

    @Invoker("onScroll")
    void invokeOnScroll(long window, double horizontal, double vertical);
}