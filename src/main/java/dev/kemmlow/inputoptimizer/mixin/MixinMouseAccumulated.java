package dev.kemmlow.inputoptimizer.mixin;

import dev.kemmlow.inputoptimizer.Main;
import dev.kemmlow.inputoptimizer.rawinput.RawInputManager;
import dev.kemmlow.inputoptimizer.util.ScreenHelper;
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
    private void fixMacDrift(CallbackInfo ci) {
        if (!Main.getConfig().isEnabled()) return;
        if (!Main.getConfig().isEnabled() || !RawInputManager.isActive()) return;
        if (ScreenHelper.getScreen(this.minecraft) != null) return;
        if (this.accumulatedDX == 0.0 && this.accumulatedDY == 0.0) {
            double[] raw = RawInputManager.pollBothDeltas();
            if (raw[0] == 0.0 && raw[1] == 0.0) {
                this.accumulatedDX = 0.0;
                this.accumulatedDY = 0.0;
            }
        }
    }
}