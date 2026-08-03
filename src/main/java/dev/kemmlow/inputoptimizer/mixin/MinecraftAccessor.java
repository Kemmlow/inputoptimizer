package dev.kemmlow.inputoptimizer.mixin;

import dev.kemmlow.inputoptimizer.api.IGuiAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;

@Mixin(Minecraft.class)
@Implements(@Interface(iface = IGuiAccessor.class, prefix = "get"))
public abstract class MinecraftAccessor implements IGuiAccessor {

    @Accessor("gui")
    public abstract Gui getGui();

    public Screen getScreen() {
        return getGui().screen();
    }
}