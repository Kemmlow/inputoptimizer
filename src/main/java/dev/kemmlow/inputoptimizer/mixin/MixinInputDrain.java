package dev.kemmlow.inputoptimizer.mixin;

import dev.kemmlow.inputoptimizer.Main;
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
    @Inject(method = "tick", at = @At("HEAD"))
    private void drainRawInputBeforeTick(CallbackInfo ci) {
        if (!Main.getConfig().isEnabled() || !RawInputManager.isActive()) return;
        Minecraft client = Minecraft.getInstance();
        if (client.getWindow() == null) return;

        RawKeyEvent kEvent;
        while ((kEvent = RawInputManager.getEngine().pollKey()) != null) {
            ((MixinKeyboardHandlerInvoker) client.keyboardHandler).invokeKeyPress(
                client.getWindow().handle(),
                kEvent.action,
                new KeyEvent(kEvent.key, kEvent.scancode, kEvent.modifiers)
            );
            RawInputManager.markKeyConsumed(kEvent.key);
        }

        RawButtonEvent bEvent;
        while ((bEvent = RawInputManager.getEngine().pollButton()) != null) {
            ((MixinMouseHandlerInvoker) client.mouseHandler).invokeOnButton(
                client.getWindow().handle(),
                new MouseButtonInfo(bEvent.button, bEvent.modifiers),
                bEvent.action
            );
            RawInputManager.markButtonConsumed(bEvent.button, bEvent.action);
        }

        RawScrollEvent sEvent;
        while ((sEvent = RawInputManager.getEngine().pollScroll()) != null) {
            ((MixinMouseHandlerInvoker) client.mouseHandler).invokeOnScroll(
                client.getWindow().handle(),
                sEvent.horizontal,
                sEvent.vertical
            );
        }

        if (client.mouseHandler.isMouseGrabbed()) {
            client.mouseHandler.handleAccumulatedMovement();
        }
    }
}
