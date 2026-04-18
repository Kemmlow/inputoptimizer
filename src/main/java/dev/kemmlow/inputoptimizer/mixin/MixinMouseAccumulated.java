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
    @Shadow private double accumulatedDX;
    @Shadow private double accumulatedDY;
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "handleAccumulatedMovement", at = @At("HEAD"))
    private void ensureNonZeroForRawPath(CallbackInfo ci) {
        if (!RawInputManager.isActive()) return;
        if (this.minecraft.screen != null) return;
        if (this.accumulatedDX == 0.0 && this.accumulatedDY == 0.0) {
            this.accumulatedDX = 0.001;
        }
    }
}