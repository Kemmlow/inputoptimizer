package dev.kemmlow.inputoptimizer.mixin;

import dev.kemmlow.inputoptimizer.rawinput.RawInputManager;
import net.minecraft.client.MouseHandler;
import net.minecraft.util.SmoothDouble;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MixinMouseSmoothingBypass {
    @Shadow
    @Final
    private SmoothDouble smoothTurnX;

    @Shadow
    @Final
    private SmoothDouble smoothTurnY;

    @Shadow
    private double accumulatedDX;

    @Shadow
    private double accumulatedDY;

    @Redirect(method = "turnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/SmoothDouble;getNewDeltaValue(DD)D", ordinal = 0))
    private double optimiseSmoothX(SmoothDouble instance, double value, double smoothing) {
        if (!RawInputManager.isActive()) {
            return instance.getNewDeltaValue(value, smoothing);
        }
        double rawDelta = RawInputManager.pollDeltaX();
        double effective = Math.abs(rawDelta) > 0.0 ? rawDelta : value;
        return instance.getNewDeltaValue(effective, smoothing);
    }

    @Redirect(method = "turnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/SmoothDouble;getNewDeltaValue(DD)D", ordinal = 1))
    private double optimiseSmoothY(SmoothDouble instance, double value, double smoothing) {
        if (!RawInputManager.isActive()) {
            return instance.getNewDeltaValue(value, smoothing);
        }
        double rawDelta = RawInputManager.pollDeltaY();
        double effective = Math.abs(rawDelta) > 0.0 ? rawDelta : value;
        return instance.getNewDeltaValue(effective, smoothing);
    }

    @Inject(method = "releaseMouse", at = @At("TAIL"))
    private void resetSmoothingOnRelease(CallbackInfo ci) {
        this.smoothTurnX.reset();
        this.smoothTurnY.reset();
    }
}