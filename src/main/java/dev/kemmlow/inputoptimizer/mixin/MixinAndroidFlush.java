package dev.kemmlow.inputoptimizer.mixin;

import dev.kemmlow.inputoptimizer.InputFlushManager;
import dev.kemmlow.inputoptimizer.rawinput.RawInputManager;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinAndroidFlush {
    @Inject(method = "runTick", at = @At("TAIL"))
    private void androidPostFrameFlush(boolean advanceGameTime, CallbackInfo ci) {
        if (!RawInputManager.isAndroid()) return;
        Minecraft client = Minecraft.getInstance();
        if (client.screen != null) return;
        InputFlushManager.flush(true);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void androidTickSync(CallbackInfo ci) {
        if (!RawInputManager.isAndroid()) return;
        Minecraft client = Minecraft.getInstance();
        if (client.screen != null) return;
        RawInputManager.setFocused(true);
        RawInputManager.setGameFocused(true);
    }
}