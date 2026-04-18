package dev.kemmlow.inputoptimizer.rawinput;

import com.mojang.blaze3d.platform.Window;
import dev.kemmlow.inputoptimizer.Main;
import dev.kemmlow.inputoptimizer.rawinput.platform.LinuxRawInputEngine;
import dev.kemmlow.inputoptimizer.rawinput.platform.MacOSRawInputEngine;
import dev.kemmlow.inputoptimizer.rawinput.platform.WindowsRawInputEngine;
import net.minecraft.client.Minecraft;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class RawInputManager {
    private static RawInputEngine engine;
    private static boolean initialized = false;

    private static final Set<Integer> consumedKeys =
        Collections.newSetFromMap(new ConcurrentHashMap<>());

    private static final Set<Integer> consumedButtons =
        Collections.newSetFromMap(new ConcurrentHashMap<>());

    private RawInputManager() {}

    public static boolean initialize(long windowHandle) {
        if (initialized) return engine != null && engine.isRunning();
        initialized = true;

        String os = System.getProperty("os.name", "").toLowerCase();

        if (os.contains("win")) {
            engine = new WindowsRawInputEngine();
        } else if (os.contains("linux")) {
            engine = new LinuxRawInputEngine();
        } else if (os.contains("mac")) {
            engine = new MacOSRawInputEngine();
        } else {
            Main.LOGGER.warn("[Input Optimizer] Unsupported platform: {}", os);
            return false;
        }

        boolean success = engine.initialize(windowHandle);
        if (success) {
            Main.LOGGER.info("[Input Optimizer] Raw input initialized: {}", engine.platformName());
        } else {
            Main.LOGGER.warn("[Input Optimizer] Failed to initialize raw input: {}", engine.platformName());
            engine = null;
        }
        return success;
    }

    public static boolean isActive() {
        return engine != null && engine.isRunning();
    }

    public static RawInputEngine getEngine() {
        return engine;
    }

    public static void markKeyConsumed(int glfwKey) {
        consumedKeys.add(glfwKey);
    }

    public static boolean consumeIfKeyMarked(int glfwKey) {
        return consumedKeys.remove(glfwKey);
    }

    public static void markButtonConsumed(int button, int action) {
        consumedButtons.add(packButtonAction(button, action));
    }

    public static boolean consumeIfButtonMarked(int button, int action) {
        return consumedButtons.remove(packButtonAction(button, action));
    }

    private static int packButtonAction(int button, int action) {
        return ((button & 0xFFFF) << 16) | (action & 0xFFFF);
    }

    public static double[] pollBothDeltas() {
        return engine != null ? engine.pollBothDeltas() : new double[]{0.0, 0.0};
    }

    public static void setFocused(boolean focused) {
        if (engine != null) engine.setFocused(focused);
        if (!focused) {
            consumedKeys.clear();
            consumedButtons.clear();
        }
    }

    public static void setGameFocused(boolean gameFocused) {
        if (engine != null) engine.setGameFocused(gameFocused);
    }

    public static void tick() {
        if (engine == null) return;
        Minecraft client = Minecraft.getInstance();
        setFocused(client.isWindowActive());
        engine.setGameFocused(client.screen == null);

        if (engine instanceof WindowsRawInputEngine winEngine) {
            Window window = client.getWindow();
            if (window != null) {
                winEngine.updateCenter(
                    window.getX() + (window.getScreenWidth() / 2),
                    window.getY() + (window.getScreenHeight() / 2)
                );
            }
        }
    }

    public static void shutdown() {
        if (engine != null) {
            engine.shutdown();
            engine = null;
        }
        consumedKeys.clear();
        consumedButtons.clear();
    }
}