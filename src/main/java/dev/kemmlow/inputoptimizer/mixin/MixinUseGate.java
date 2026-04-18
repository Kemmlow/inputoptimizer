package dev.kemmlow.inputoptimizer.mixin;

import dev.kemmlow.inputoptimizer.TickGate;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinUseGate {
    @Inject(method = "startUseItem()V", at = @At("HEAD"), cancellable = true)
    private void gateStartUseItem(CallbackInfo ci) {
        if (!TickGate.tryUse()) ci.cancel();
    }
}