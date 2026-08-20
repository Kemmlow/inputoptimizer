package dev.kemmlow.inputoptimizer.config;

import dev.kemmlow.inputoptimizer.Main;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends OptionsSubScreen {
    public ConfigScreen(Screen parent) {
        super(parent, Minecraft.getInstance().options, Component.literal("Input Optimizer"));
    }

    @Override
    protected void addOptions() {
        this.list.addBig(CycleButton.<Boolean>builder(value -> value
                ? Component.literal("ON").withStyle(ChatFormatting.GREEN)
                : Component.literal("OFF").withStyle(ChatFormatting.RED),
                Main.getConfig().isConfigEnabled())
            .withValues(true, false)
            .create(0, 0, 310, 20, Component.literal("Mod Enabled"), (button, value) -> {
                Main.getConfig().setEnabled(value);
                Main.getConfig().save();
            }));
    }
}
