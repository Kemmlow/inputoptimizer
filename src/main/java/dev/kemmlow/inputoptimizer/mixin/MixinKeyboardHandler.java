package dev.kemmlow.inputoptimizer.mixin;

import dev.kemmlow.inputoptimizer.InputFlushManager;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class MixinKeyboardHandler {
    @Inject(method = "keyPress", at = @At("TAIL"))
    private void onKeyFlush(long window, int action, KeyEvent input, CallbackInfo ci) {
        if (action == 0) return;
        InputFlushManager.flush(true);
    }
}