package dev.kemmlow.inputoptimizer.mixin;

import dev.kemmlow.inputoptimizer.InputFlushManager;
import dev.kemmlow.inputoptimizer.rawinput.RawInputManager;
import dev.kemmlow.inputoptimizer.rawinput.RawKeyEvent;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinKeyboardFlushFromRaw {
    @Inject(method = "tick", at = @At("TAIL"))
    private void drainRawKeyQueue(CallbackInfo ci) {
        if (!RawInputManager.isActive()) return;
        RawKeyEvent event;
        while ((event = RawInputManager.getEngine().pollKey()) != null) {
            if (event.action != 0) {
                InputFlushManager.flush(true);
            }
        }
    }
}