package dev.kemmlow.inputoptimizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ClientModInitializer;

public class Main implements ClientModInitializer {
    public static final String MOD_ID = "inputoptimizer";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static boolean externalRawInputPresent = false;

    @Override
    public void onInitializeClient() {
        LOGGER.info("[Input Optimizer] Loaded.");
    }
}