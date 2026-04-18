package dev.kemmlow.inputoptimizer.mixin;

import dev.kemmlow.inputoptimizer.InputFlushManager;
import dev.kemmlow.inputoptimizer.rawinput.RawInputManager;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class MixinKeyboardHandler {
    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
    private void interceptDuplicate(long window, int action, KeyEvent input, CallbackInfo ci) {
        if (!RawInputManager.isActive()) return;
        if (RawInputManager.consumeIfKeyMarked(input.key())) ci.cancel();
    }

    @Inject(method = "keyPress", at = @At("TAIL"))
    private void flushOnKey(long window, int action, KeyEvent input, CallbackInfo ci) {
        if (action == 0) return;
        InputFlushManager.flush(true);
    }
}