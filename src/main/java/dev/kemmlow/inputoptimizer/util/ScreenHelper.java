package dev.kemmlow.inputoptimizer.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class ScreenHelper {
    public static Screen getScreen(Minecraft client) {
        return client != null ? client.screen : null;
    }
}