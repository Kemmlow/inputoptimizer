package dev.kemmlow.inputoptimizer.mixin;

import dev.kemmlow.inputoptimizer.TickGate;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MixinAttackGate {
    @Inject(method = "startAttack()Z", at = @At("HEAD"), cancellable = true)
    private void gateStartAttack(CallbackInfoReturnable<Boolean> cir) {
        if (!TickGate.tryAttack()) cir.setReturnValue(false);
    }

    @Inject(method = "continueAttack(Z)V", at = @At("HEAD"), cancellable = true)
    private void gateContinueAttack(boolean leftClick, CallbackInfo ci) {
        if (!leftClick) return;
        if (!TickGate.tryContinueAttack()) ci.cancel();
    }
}