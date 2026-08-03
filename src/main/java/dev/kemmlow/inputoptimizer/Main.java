package dev.kemmlow.inputoptimizer;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ClientModInitializer {
    public static final String MOD_ID = "inputoptimizer";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static boolean externalRawInputPresent = false;
    private static final ConfigImpl config = new ConfigImpl();

    @Override
    public void onInitializeClient() {
        LOGGER.info("[Input Optimizer] Loaded.");
    }

    public static ConfigImpl getConfig() {
        return config;
    }
}
