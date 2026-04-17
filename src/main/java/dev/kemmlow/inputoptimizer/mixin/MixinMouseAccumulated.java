package dev.kemmlow.inputoptimizer.mixin;

import dev.kemmlow.inputoptimizer.rawinput.RawInputManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MixinMouseAccumulated {
    @Shadow
    private double accumulatedDX;

    @Shadow
    private double accumulatedDY;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "handleAccumulatedMovement", at = @At("HEAD"))
    private void injectRawDeltas(CallbackInfo ci) {
        if (!RawInputManager.isActive()) return;
        if (this.minecraft.screen != null) return;
        double rawDX = RawInputManager.pollDeltaX();
        double rawDY = RawInputManager.pollDeltaY();
        if (Math.abs(rawDX) > 0.0) this.accumulatedDX = rawDX;
        if (Math.abs(rawDY) > 0.0) this.accumulatedDY = rawDY;
    }
}