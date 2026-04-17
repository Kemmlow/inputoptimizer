package dev.kemmlow.inputoptimizer.mixin;

import com.mojang.blaze3d.platform.Window;
import dev.kemmlow.inputoptimizer.Main;
import dev.kemmlow.inputoptimizer.rawinput.RawInputManager;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MixinMouseSetup {
    @Inject(method = "setup", at = @At("TAIL"))
    private void initializeRawInput(Window window, CallbackInfo ci) {
        if (Main.externalRawInputPresent) return;
        RawInputManager.initialize(window.handle());
    }
}