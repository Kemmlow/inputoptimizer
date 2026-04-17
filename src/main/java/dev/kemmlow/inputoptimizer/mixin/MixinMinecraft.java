package dev.kemmlow.inputoptimizer.mixin;

import dev.kemmlow.inputoptimizer.InputFlushManager;
import dev.kemmlow.inputoptimizer.rawinput.RawInputManager;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "runTick", at = @At("HEAD"))
    private void resetFrameFlag(boolean advanceGameTime, CallbackInfo ci) {
        InputFlushManager.callbackFlushedThisFrame = false;
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/MouseHandler;handleAccumulatedMovement()V",
            shift = At.Shift.AFTER))
    private void fallbackFlush(boolean advanceGameTime, CallbackInfo ci) {
        InputFlushManager.flush(false);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickRawInput(CallbackInfo ci) {
        if (RawInputManager.isActive()) {
            RawInputManager.tick();
        }
    }
}