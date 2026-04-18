package dev.kemmlow.inputoptimizer.mixin;

import dev.kemmlow.inputoptimizer.InputFlushManager;
import dev.kemmlow.inputoptimizer.rawinput.RawButtonEvent;
import dev.kemmlow.inputoptimizer.rawinput.RawInputManager;
import dev.kemmlow.inputoptimizer.rawinput.RawKeyEvent;
import dev.kemmlow.inputoptimizer.rawinput.RawScrollEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinInputDrain {
    @Inject(method = "runTick", at = @At("HEAD"))
    private void ultraDrain(boolean advanceGameTime, CallbackInfo ci) {
        if (!RawInputManager.isActive()) return;
        Minecraft client = Minecraft.getInstance();
        boolean workDone = false;

        RawKeyEvent kEvent;
        while ((kEvent = RawInputManager.getEngine().pollKey()) != null) {
            ((MixinKeyboardHandlerInvoker) client.keyboardHandler).invokeKeyPress(
                client.getWindow().handle(),
                kEvent.action,
                new KeyEvent(kEvent.key, kEvent.scancode, kEvent.modifiers)
            );
            RawInputManager.markKeyConsumed(kEvent.key);
            if (kEvent.action != 0) workDone = true;
        }

        RawButtonEvent bEvent;
        while ((bEvent = RawInputManager.getEngine().pollButton()) != null) {
            ((MixinMouseHandlerInvoker) client.mouseHandler).invokeOnButton(
                client.getWindow().handle(),
                new MouseButtonInfo(bEvent.button, bEvent.modifiers),
                bEvent.action
            );
            RawInputManager.markButtonConsumed(bEvent.button, bEvent.action);
            if (bEvent.action != 0) workDone = true;
        }

        RawScrollEvent sEvent;
        while ((sEvent = RawInputManager.getEngine().pollScroll()) != null) {
            ((MixinMouseHandlerInvoker) client.mouseHandler).invokeOnScroll(
                client.getWindow().handle(),
                sEvent.horizontal,
                sEvent.vertical
            );
            workDone = true;
        }

        if (workDone) InputFlushManager.flush(true);
    }
}