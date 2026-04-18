package dev.kemmlow.inputoptimizer.mixin;

import dev.kemmlow.inputoptimizer.InputFlushManager;
import dev.kemmlow.inputoptimizer.rawinput.RawInputManager;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MixinMouseHandler {
    @Inject(method = "onButton", at = @At("HEAD"), cancellable = true)
    private void interceptDuplicateButton(long window, MouseButtonInfo buttonInfo, int action, CallbackInfo ci) {
        if (!RawInputManager.isActive()) return;
        if (RawInputManager.consumeIfButtonMarked(buttonInfo.button(), action)) ci.cancel();
    }

    @Inject(method = "onButton", at = @At("TAIL"))
    private void onMouseButtonFlush(long window, MouseButtonInfo buttonInfo, int action, CallbackInfo ci) {
        if (action != 1) return;
        InputFlushManager.flush(true);
    }

    @Inject(method = "onScroll", at = @At("TAIL"))
    private void onScrollFlush(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (vertical == 0.0 && horizontal == 0.0) return;
        InputFlushManager.flush(true);
    }
}