package dev.kemmlow.inputoptimizer.mixin;

import dev.kemmlow.inputoptimizer.Main;
import dev.kemmlow.inputoptimizer.rawinput.RawInputManager;
import dev.kemmlow.inputoptimizer.util.ScreenHelper;
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
        if (!Main.getConfig().isEnabled() || !RawInputManager.isActive()) return;
        Minecraft client = Minecraft.getInstance();
        if (ScreenHelper.getScreen(client) != null) return;
        if (!client.mouseHandler.isMouseGrabbed()) return;
        client.mouseHandler.handleAccumulatedMovement();
    }
}