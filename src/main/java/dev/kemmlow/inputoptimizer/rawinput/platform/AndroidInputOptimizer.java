package dev.kemmlow.inputoptimizer.rawinput.platform;

import dev.kemmlow.inputoptimizer.rawinput.RawInputEngine;

public class AndroidInputOptimizer extends RawInputEngine {

    private static final boolean IS_ANDROID = isAndroid();

    private static boolean isAndroid() {
        String vendor = System.getProperty("java.vendor", "").toLowerCase();
        String vmName = System.getProperty("java.vm.name", "").toLowerCase();
        String osName = System.getProperty("os.name", "").toLowerCase();
        String androidData = System.getProperty("android.os.build.version.sdk", null);
        return androidData != null
            || vendor.contains("android")
            || vmName.contains("dalvik")
            || vmName.contains("art")
            || osName.contains("android");
    }

    public static boolean isAndroidPlatform() {
        return IS_ANDROID;
    }

    @Override
    public boolean initialize(long glfwWindow) {
        if (!IS_ANDROID) return false;
        this.running = true;

        Thread thread = new Thread(() -> {
            while (this.running) {
                Thread.onSpinWait();
            }
        }, "Kemmlow-Android-Bridge-Speedup");
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.setDaemon(true);
        thread.start();
        return true;
    }

    @Override
    public void shutdown() {
        this.running = false;
    }

    @Override
    public void centerCursor() {}

    @Override
    public String platformName() {
        return "Android HID Bridge";
    }
}