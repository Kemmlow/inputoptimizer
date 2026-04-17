package dev.kemmlow.inputoptimizer.mixin;

import dev.kemmlow.inputoptimizer.Main;
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
public abstract class MixinMouseImmediate {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    private void turnPlayer(double timeDelta) {}

    @Inject(method = "onMove", at = @At("TAIL"))
    private void immediateMouseProcess(long window, double x, double y, CallbackInfo ci) {
        if (Main.externalRawInputPresent) return;
        if (RawInputManager.isActive() && !RawInputManager.isAndroid()) return;
        if (this.minecraft.screen != null) return;
        if (!this.minecraft.mouseHandler.isMouseGrabbed()) return;
        this.turnPlayer(0.0);
    }
}