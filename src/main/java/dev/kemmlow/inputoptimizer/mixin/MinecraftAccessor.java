package dev.kemmlow.inputoptimizer.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public abstract class MinecraftAccessor {
    @Accessor("gui")
    public abstract Gui getGui();
}