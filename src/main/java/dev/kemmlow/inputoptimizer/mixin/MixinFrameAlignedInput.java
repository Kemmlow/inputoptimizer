package dev.kemmlow.inputoptimizer.mixin;

import dev.kemmlow.inputoptimizer.rawinput.RawInputManager;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinFrameAlignedInput {
    @Inject(method = "render", at = @At("HEAD"))
    private void alignInputToFrame(DeltaTracker deltaTracker, boolean advanceGameTime, CallbackInfo ci) {
        if (!RawInputManager.isActive()) return;
        Minecraft client = Minecraft.getInstance();
        if (client.screen != null) return;
        if (client.mouseHandler.isMouseGrabbed()) {
            client.mouseHandler.handleAccumulatedMovement();
        }
    }
}