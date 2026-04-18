package dev.kemmlow.inputoptimizer.mixin;

import dev.kemmlow.inputoptimizer.InputFlushManager;
import dev.kemmlow.inputoptimizer.TickGate;
import dev.kemmlow.inputoptimizer.rawinput.RawInputManager;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "tick", at = @At("HEAD"))
    private void tickHead(CallbackInfo ci) {
        InputFlushManager.callbackFlushedThisTick = false;
        TickGate.reset();
        if (RawInputManager.isActive()) RawInputManager.tick();
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/MouseHandler;handleAccumulatedMovement()V",
            shift = At.Shift.AFTER))
    private void fallbackFlush(boolean advanceGameTime, CallbackInfo ci) {
        InputFlushManager.flush(false);
    }
}