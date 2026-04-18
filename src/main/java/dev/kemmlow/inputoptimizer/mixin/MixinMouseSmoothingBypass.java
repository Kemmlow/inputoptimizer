package dev.kemmlow.inputoptimizer.mixin;

import dev.kemmlow.inputoptimizer.rawinput.RawInputManager;
import net.minecraft.client.MouseHandler;
import net.minecraft.util.SmoothDouble;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MixinMouseSmoothingBypass {
    @Shadow @Final private SmoothDouble smoothTurnX;
    @Shadow @Final private SmoothDouble smoothTurnY;

    @Unique private double cachedRawDX = 0.0;
    @Unique private double cachedRawDY = 0.0;

    @Inject(method = "turnPlayer", at = @At("HEAD"))
    private void captureRawDeltas(double timeDelta, CallbackInfo ci) {
        if (!RawInputManager.isActive()) return;
        double[] d = RawInputManager.pollBothDeltas();
        this.cachedRawDX = d[0];
        this.cachedRawDY = d[1];
    }

    @Redirect(method = "turnPlayer", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/util/SmoothDouble;getNewDeltaValue(DD)D", ordinal = 0))
    private double optimiseSmoothX(SmoothDouble instance, double value, double smoothing) {
        if (!RawInputManager.isActive()) return instance.getNewDeltaValue(value, smoothing);
        double effective = Math.abs(this.cachedRawDX) > 0.0 ? this.cachedRawDX : value;
        return instance.getNewDeltaValue(effective, smoothing);
    }

    @Redirect(method = "turnPlayer", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/util/SmoothDouble;getNewDeltaValue(DD)D", ordinal = 1))
    private double optimiseSmoothY(SmoothDouble instance, double value, double smoothing) {
        if (!RawInputManager.isActive()) return instance.getNewDeltaValue(value, smoothing);
        double effective = Math.abs(this.cachedRawDY) > 0.0 ? this.cachedRawDY : value;
        return instance.getNewDeltaValue(effective, smoothing);
    }

    @Inject(method = "releaseMouse", at = @At("TAIL"))
    private void resetSmoothingOnRelease(CallbackInfo ci) {
        this.smoothTurnX.reset();
        this.smoothTurnY.reset();
    }
}