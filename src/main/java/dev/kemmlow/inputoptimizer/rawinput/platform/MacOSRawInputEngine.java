package dev.kemmlow.inputoptimizer.rawinput.platform;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import dev.kemmlow.inputoptimizer.rawinput.RawButtonEvent;
import dev.kemmlow.inputoptimizer.rawinput.RawInputEngine;
import dev.kemmlow.inputoptimizer.rawinput.RawKeyEvent;
import dev.kemmlow.inputoptimizer.rawinput.RawScrollEvent;
import dev.kemmlow.inputoptimizer.rawinput.native_bindings.MacOSCoreFoundation;
import dev.kemmlow.inputoptimizer.rawinput.native_bindings.MacOSIOKit;

public class MacOSRawInputEngine extends RawInputEngine {
    private volatile Pointer hidManager;
    private volatile Pointer runLoop;

    @Override
    public boolean initialize(long glfwWindow) {
        this.running = true;
        Thread thread = new Thread(this::hidLoop, "inputoptimizer-macOS-IOHID");
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.setDaemon(true);
        thread.start();
        return true;
    }

    private void hidLoop() {
        MacOSIOKit iokit = MacOSIOKit.INSTANCE;
        MacOSCoreFoundation cf = MacOSCoreFoundation.INSTANCE;

        this.hidManager = iokit.IOHIDManagerCreate(null, 0);
        if (this.hidManager == null) {
            this.running = false;
            return;
        }

        Pointer matchingArray = createMatchingArray();
        if (matchingArray == null) {
            cf.CFRelease(this.hidManager);
            this.hidManager = null;
            this.running = false;
            return;
        }

        iokit.IOHIDManagerSetDeviceMatchingMultiple(this.hidManager, matchingArray);
        cf.CFRelease(matchingArray);

        MacOSIOKit.HIDValueCallback callback = (context, result, sender, value) -> {
            if (!this.focused) return;
            processEvent(value);
        };

        iokit.IOHIDManagerRegisterInputValueCallback(this.hidManager, callback, null);

        this.runLoop = cf.CFRunLoopGetCurrent();

        iokit.IOHIDManagerScheduleWithRunLoop(
            this.hidManager,
            this.runLoop,
            MacOSCoreFoundation.kCFRunLoopDefaultMode
        );

        int ret = -1;
        for (int attempt = 0; attempt < 5 && this.running; attempt++) {
            ret = iokit.IOHIDManagerOpen(this.hidManager, 0);
            if (ret == 0) break;
            try { Thread.sleep(200L * (attempt + 1)); } catch (InterruptedException ignored) {}
        }

        if (ret != 0) {
            System.err.printf("[InputOptimizer] IOHIDManagerOpen failed after retries: 0x%08X%n", ret);
            cf.CFRelease(this.hidManager);
            this.hidManager = null;
            this.running = false;
            return;
        }

        cf.CFRunLoopRun();

        iokit.IOHIDManagerClose(this.hidManager, 0);
        cf.CFRelease(this.hidManager);
        this.hidManager = null;
    }

    private void processEvent(Pointer value) {
        MacOSIOKit iokit = MacOSIOKit.INSTANCE;
        Pointer element = iokit.IOHIDValueGetElement(value);
        if (element == null) return;

        int page  = iokit.IOHIDElementGetUsagePage(element);
        int usage = iokit.IOHIDElementGetUsage(element);
        int val   = iokit.IOHIDValueGetIntegerValue(value);
        long now  = System.nanoTime();

        if (page == 0x01) {
            if      (usage == 0x30) accumulateDelta(val, 0);
            else if (usage == 0x31) accumulateDelta(0, val);
            else if (usage == 0x38) scrollQueue.add(new RawScrollEvent(0.0, val, now));
        } else if (page == 0x09) {
            int btn = mapButton(usage);
            if (btn >= 0) buttonQueue.add(new RawButtonEvent(btn, val == 1 ? 1 : 0, 0, now));
        } else if (page == 0x07) {
            if (val <= 1) {
                int glfw = MacOSKeyToGlfw.convert(usage);
                if (glfw != -1) keyboardQueue.add(new RawKeyEvent(glfw, usage, val, 0, now));
            }
        }
    }

    private int mapButton(int usage) {
        return switch (usage) {
            case 1 -> 0;
            case 2 -> 1;
            case 3 -> 2;
            default -> usage - 1;
        };
    }

    private Pointer createMatchingArray() {
        MacOSCoreFoundation cf = MacOSCoreFoundation.INSTANCE;

        Pointer mouse    = createDeviceMatching(0x01, 0x02);
        Pointer keyboard = createDeviceMatching(0x01, 0x06);

        if (mouse == null || keyboard == null) {
            if (mouse    != null) cf.CFRelease(mouse);
            if (keyboard != null) cf.CFRelease(keyboard);
            return null;
        }

        Pointer array = cf.CFArrayCreate(
            null,
            new Pointer[]{mouse, keyboard},
            2,
            MacOSCoreFoundation.kCFTypeArrayCallBacks
        );

        cf.CFRelease(mouse);
        cf.CFRelease(keyboard);
        return array;
    }

    private Pointer createDeviceMatching(int page, int usage) {
        MacOSCoreFoundation cf = MacOSCoreFoundation.INSTANCE;

        Memory pageMem  = new Memory(4);
        Memory usageMem = new Memory(4);
        pageMem.setInt(0, page);
        usageMem.setInt(0, usage);

        Pointer pageNum  = cf.CFNumberCreate(null, MacOSCoreFoundation.kCFNumberSInt32Type, pageMem);
        Pointer usageNum = cf.CFNumberCreate(null, MacOSCoreFoundation.kCFNumberSInt32Type, usageMem);

        if (pageNum == null || usageNum == null) {
            if (pageNum  != null) cf.CFRelease(pageNum);
            if (usageNum != null) cf.CFRelease(usageNum);
            return null;
        }

        Pointer pageKey  = cf.CFStringCreateWithCString(null, "DeviceUsagePage", MacOSCoreFoundation.kCFStringEncodingUTF8);
        Pointer usageKey = cf.CFStringCreateWithCString(null, "DeviceUsage",     MacOSCoreFoundation.kCFStringEncodingUTF8);

        if (pageKey == null || usageKey == null) {
            cf.CFRelease(pageNum);
            cf.CFRelease(usageNum);
            if (pageKey  != null) cf.CFRelease(pageKey);
            if (usageKey != null) cf.CFRelease(usageKey);
            return null;
        }

        Pointer dict = cf.CFDictionaryCreateMutable(
            null, 0,
            MacOSCoreFoundation.kCFTypeDictionaryKeyCallBacks,
            MacOSCoreFoundation.kCFTypeDictionaryValueCallBacks
        );

        if (dict == null) {
            cf.CFRelease(pageNum);
            cf.CFRelease(usageNum);
            cf.CFRelease(pageKey);
            cf.CFRelease(usageKey);
            return null;
        }

        cf.CFDictionarySetValue(dict, pageKey,  pageNum);
        cf.CFDictionarySetValue(dict, usageKey, usageNum);

        cf.CFRelease(pageNum);
        cf.CFRelease(usageNum);
        cf.CFRelease(pageKey);
        cf.CFRelease(usageKey);
        return dict;
    }

    @Override public void centerCursor() {}

    @Override
    public void shutdown() {
        this.running = false;
        Pointer rl = this.runLoop;
        if (rl != null) MacOSCoreFoundation.INSTANCE.CFRunLoopStop(rl);
    }

    @Override
    public String platformName() {
        return "macOS IOHID";
    }
}