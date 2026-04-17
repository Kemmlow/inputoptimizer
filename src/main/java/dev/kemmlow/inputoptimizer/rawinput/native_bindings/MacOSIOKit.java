package dev.kemmlow.inputoptimizer.rawinput.native_bindings;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public interface MacOSIOKit extends Library {
    MacOSIOKit INSTANCE = Native.load("IOKit", MacOSIOKit.class);

    Pointer IOHIDManagerCreate(Pointer allocator, int options);
    void    IOHIDManagerSetDeviceMatchingMultiple(Pointer manager, Pointer matching);
    int     IOHIDManagerOpen(Pointer manager, int options);
    void    IOHIDManagerClose(Pointer manager, int options);
    void    IOHIDManagerScheduleWithRunLoop(Pointer manager, Pointer runLoop, Pointer mode);

    // Queue API
    Pointer IOHIDQueueCreate(Pointer allocator, Pointer device, int depth, int options);
    void    IOHIDQueueStart(Pointer queue);
    void    IOHIDQueueStop(Pointer queue);
    Pointer IOHIDQueueCopyNextValue(Pointer queue);

    // Value accessors
    int     IOHIDValueGetIntegerValue(Pointer value);
    Pointer IOHIDValueGetElement(Pointer value);
    Pointer IOHIDElementGetDevice(Pointer element);
    int     IOHIDElementGetUsagePage(Pointer element);
    int     IOHIDElementGetUsage(Pointer element);

    // Callback registration
    void IOHIDManagerRegisterInputValueCallback(Pointer manager, HIDValueCallback callback, Pointer context);

    interface HIDValueCallback extends Callback {
        void callback(Pointer context, int result, Pointer sender, Pointer value);
    }
}