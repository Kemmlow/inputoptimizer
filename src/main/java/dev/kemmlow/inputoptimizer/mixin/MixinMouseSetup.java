package dev.kemmlow.inputoptimizer.mixin;

import dev.kemmlow.inputoptimizer.Main;
import dev.kemmlow.inputoptimizer.rawinput.RawInputManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MixinMouseSetup {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void initializeRawInput(Minecraft minecraft, CallbackInfo ci) {
        if (Main.externalRawInputPresent) return;
        if (minecraft.getWindow() == null) return;
        RawInputManager.initialize(minecraft.getWindow().handle());
    }
}
