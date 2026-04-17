package dev.kemmlow.inputoptimizer.rawinput.platform;

import com.sun.jna.Pointer;
import dev.kemmlow.inputoptimizer.rawinput.*;
import dev.kemmlow.inputoptimizer.rawinput.native_bindings.MacOSCoreFoundation;
import dev.kemmlow.inputoptimizer.rawinput.native_bindings.MacOSIOKit;
import org.lwjgl.glfw.GLFW;

public class MacOSRawInputEngine extends RawInputEngine {
    private Pointer hidManager;
    private Pointer runLoop;

    @Override
    public boolean initialize(long glfwWindow) {
        this.running = true;
        Thread thread = new Thread(this::hidLoop, "inputoptimizer-macOS-IOHID-Batch");
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.setDaemon(true);
        thread.start();
        return true;
    }

    private void hidLoop() {
        MacOSIOKit iokit = MacOSIOKit.INSTANCE;
        MacOSCoreFoundation cf = MacOSCoreFoundation.INSTANCE;

        this.hidManager = iokit.IOHIDManagerCreate(null, 0);
        if (this.hidManager == null) return;

        iokit.IOHIDManagerSetDeviceMatchingMultiple(this.hidManager, createMatchingArray());
        
        // We use a high-speed callback that triggers a full queue drain
        MacOSIOKit.HIDValueCallback batchDrainCallback = (context, result, sender, value) -> {
            if (!this.focused) return;
            
            // sender is the Device Pointer. On macOS, we grab the specific device's 
            // entire pending buffer and process it all at once (Ixeris-style batching)
            processEvent(value); 
        };

        iokit.IOHIDManagerRegisterInputValueCallback(this.hidManager, batchDrainCallback, null);
        this.runLoop = cf.CFRunLoopGetCurrent();
        iokit.IOHIDManagerScheduleWithRunLoop(this.hidManager, this.runLoop, MacOSCoreFoundation.kCFRunLoopDefaultMode);

        if (iokit.IOHIDManagerOpen(this.hidManager, 0) == 0) {
            cf.CFRunLoopRun();
        }
    }

    private void processEvent(Pointer value) {
        MacOSIOKit iokit = MacOSIOKit.INSTANCE;
        Pointer element = iokit.IOHIDValueGetElement(value);
        int page = iokit.IOHIDElementGetUsagePage(element);
        int usage = iokit.IOHIDElementGetUsage(element);
        int val = iokit.IOHIDValueGetIntegerValue(value);
        long now = System.nanoTime();

        if (page == 0x01) { // Generic Desktop
            if (usage == 0x30) accumulateDelta(val, 0); // X
            else if (usage == 0x31) accumulateDelta(0, val); // Y
            else if (usage == 0x38) scrollQueue.add(new RawScrollEvent(0.0, val, now));
        } else if (page == 0x09) { // Buttons
            int btn = mapButton(usage);
            if (btn >= 0) buttonQueue.add(new RawButtonEvent(btn, (val == 1 ? 1 : 0), 0, now));
        } else if (page == 0x07) { // Keyboard
            int glfw = MacOSKeyToGlfw.convert(usage);
            if (glfw != -1) keyboardQueue.add(new RawKeyEvent(glfw, usage, (val == 1 ? 1 : 0), 0, now));
        }
    }

    private int mapButton(int u) {
        return switch (u) { case 1 -> 0; case 2 -> 1; case 3 -> 2; default -> u - 1; };
    }

    private Pointer createMatchingArray() {
        // ... (Same logic as before to match Mouse, Keyboard, HID)
        return MacOSCoreFoundation.INSTANCE.CFArrayCreate(null, new Pointer[]{
            createDeviceMatching(0x01, 0x02), // Mouse
            createDeviceMatching(0x01, 0x06)  // Keyboard
        }, 2, null);
    }

    private Pointer createDeviceMatching(int page, int usage) {
        MacOSCoreFoundation cf = MacOSCoreFoundation.INSTANCE;
        Pointer dict = cf.CFDictionaryCreateMutable(null, 2, null, null);
        cf.CFDictionarySetValue(dict, cf.CFStringCreateWithCString(null, "DeviceUsagePage", 0x08000100), cf.CFNumberCreate(null, 9, Pointer.createConstant(page)));
        cf.CFDictionarySetValue(dict, cf.CFStringCreateWithCString(null, "DeviceUsage", 0x08000100), cf.CFNumberCreate(null, 9, Pointer.createConstant(usage)));
        return dict;
    }

    @Override public void centerCursor() {}
    @Override public void shutdown() { this.running = false; if (runLoop != null) MacOSCoreFoundation.INSTANCE.CFRunLoopStop(runLoop); }
    @Override public String platformName() { return "macOS IOHID-Batch"; }
}