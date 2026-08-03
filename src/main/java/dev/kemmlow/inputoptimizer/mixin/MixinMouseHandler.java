package dev.kemmlow.inputoptimizer.mixin;

import dev.kemmlow.inputoptimizer.Main;
import dev.kemmlow.inputoptimizer.rawinput.RawInputManager;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MixinMouseHandler {
    @Inject(method = "onButton", at = @At("HEAD"), cancellable = true)
    private void interceptDuplicateButton(long window, MouseButtonInfo buttonInfo, int action, CallbackInfo ci) {
        if (!Main.getConfig().isEnabled() || !RawInputManager.isActive()) return;
        if (RawInputManager.consumeIfButtonMarked(buttonInfo.button(), action)) ci.cancel();
    }
}
