package dev.kemmlow.inputoptimizer.rawinput.native_bindings;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;

public interface MacOSCoreFoundation extends Library {
    MacOSCoreFoundation INSTANCE = Native.load("CoreFoundation", MacOSCoreFoundation.class);

    Pointer CFRunLoopGetCurrent();
    void CFRunLoopRun();
    void CFRunLoopStop(Pointer runLoop);

    Pointer CFDictionaryCreateMutable(Pointer allocator, int capacity, Pointer keyCallBacks, Pointer valueCallBacks);
    void CFDictionarySetValue(Pointer dict, Pointer key, Pointer value);
    Pointer CFNumberCreate(Pointer allocator, int theType, Pointer valuePtr);
    Pointer CFStringCreateWithCString(Pointer allocator, String string, int encoding);
    Pointer CFArrayCreate(Pointer allocator, Pointer[] values, int numValues, Pointer callBacks);

    Pointer kCFAllocatorDefault = null;
    Pointer kCFRunLoopDefaultMode = NativeLibrary.getInstance("CoreFoundation").getGlobalVariableAddress("kCFRunLoopDefaultMode").getPointer(0);
    int kCFNumberIntType = 9;
    int kCFStringEncodingUTF8 = 0x08000100;
}