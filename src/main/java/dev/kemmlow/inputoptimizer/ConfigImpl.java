package dev.kemmlow.inputoptimizer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigImpl {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH =
        FabricLoader.getInstance().getConfigDir().resolve(Main.MOD_ID + ".json");

    private boolean enabled = true;
    private boolean rawInputEnabled = true;

    private transient boolean serverOptOut = false;

    public boolean isEnabled() {
        return enabled && !serverOptOut;
    }

    public boolean isRawInputEnabled() {
        return rawInputEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setRawInputEnabled(boolean rawInputEnabled) {
        this.rawInputEnabled = rawInputEnabled;
    }

    public boolean isConfigEnabled() {
        return enabled;
    }

    public boolean isServerOptOut() {
        return serverOptOut;
    }

    public void setServerOptOut(boolean serverOptOut) {
        this.serverOptOut = serverOptOut;
    }

    public void load() {
        if (!Files.exists(PATH)) return;
        try (Reader reader = Files.newBufferedReader(PATH)) {
            ConfigImpl loaded = GSON.fromJson(reader, ConfigImpl.class);
            if (loaded != null) {
                this.enabled = loaded.enabled;
                this.rawInputEnabled = loaded.rawInputEnabled;
            }
        } catch (Exception e) {
            Main.LOGGER.warn("[Input Optimizer] Failed to read config, using defaults.", e);
        }
    }

    public void save() {
        try {
            Files.createDirectories(PATH.getParent());
        } catch (IOException e) {
            Main.LOGGER.warn("[Input Optimizer] Failed to create config directory.", e);
            return;
        }
        try (Writer writer = Files.newBufferedWriter(PATH)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            Main.LOGGER.warn("[Input Optimizer] Failed to save config.", e);
        }
    }
}
