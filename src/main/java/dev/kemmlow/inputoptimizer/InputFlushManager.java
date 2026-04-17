package dev.kemmlow.inputoptimizer;

import dev.kemmlow.inputoptimizer.mixin.MixinMinecraftInvoker;
import net.minecraft.client.Minecraft;

public final class InputFlushManager {
    private InputFlushManager() {}

    public static volatile boolean callbackFlushedThisFrame = false;

    public static void flush(boolean fromCallback) {
        Minecraft client = Minecraft.getInstance();
        if (client == null) return;
        if (!client.isSameThread()) return;
        if (client.screen != null) return;
        if (client.isPaused()) return;
        if (client.level == null || client.player == null) return;
        if (client.gameMode == null) return;
        if (!fromCallback && callbackFlushedThisFrame) return;
        if (fromCallback) {
            callbackFlushedThisFrame = true;
        }
        ((MixinMinecraftInvoker) client).invokeHandleKeybinds();
    }
}