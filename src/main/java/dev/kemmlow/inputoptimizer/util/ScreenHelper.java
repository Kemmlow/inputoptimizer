package dev.kemmlow.inputoptimizer.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import java.lang.reflect.Method;

public class ScreenHelper {
    private static Method getGuiMethod;

    static {
        try {
            getGuiMethod = Minecraft.class.getMethod("getGui");
            getGuiMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static Screen getScreen(Minecraft client) {
        if (client == null) return null;
        try {
            Gui gui = (Gui) getGuiMethod.invoke(client);
            if (gui != null) {
                return gui.screen();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}