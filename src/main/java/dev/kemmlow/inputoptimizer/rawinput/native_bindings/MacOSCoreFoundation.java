package dev.kemmlow.inputoptimizer.rawinput.native_bindings;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;

public interface MacOSCoreFoundation extends Library {
    NativeLibrary CF_LIB = NativeLibrary.getInstance("CoreFoundation");

    MacOSCoreFoundation INSTANCE = Native.load("CoreFoundation", MacOSCoreFoundation.class);

    // RunLoop
    Pointer CFRunLoopGetCurrent();
    void    CFRunLoopRun();
    void    CFRunLoopStop(Pointer runLoop);

    // Dictionary
    Pointer CFDictionaryCreateMutable(Pointer allocator, int capacity,
                                      Pointer keyCallBacks, Pointer valueCallBacks);
    void    CFDictionarySetValue(Pointer dict, Pointer key, Pointer value);

    // Number
    Pointer CFNumberCreate(Pointer allocator, int theType, Pointer valuePtr);

    // String
    Pointer CFStringCreateWithCString(Pointer allocator, String cStr, int encoding);

    // Array
    Pointer CFArrayCreate(Pointer allocator, Pointer[] values, int numValues, Pointer callBacks);

    // Memory management
    void    CFRelease(Pointer obj);
    Pointer CFRetain(Pointer obj);

    // ---- Constants ----
    int kCFNumberSInt32Type   = 3;
    int kCFNumberSInt64Type   = 4;
    int kCFStringEncodingUTF8 = 0x08000100;

    Pointer kCFRunLoopDefaultMode =
        CF_LIB.getGlobalVariableAddress("kCFRunLoopDefaultMode").getPointer(0);

    Pointer kCFTypeDictionaryKeyCallBacks =
        CF_LIB.getGlobalVariableAddress("kCFTypeDictionaryKeyCallBacks");

    Pointer kCFTypeDictionaryValueCallBacks =
        CF_LIB.getGlobalVariableAddress("kCFTypeDictionaryValueCallBacks");

    Pointer kCFTypeArrayCallBacks =
        CF_LIB.getGlobalVariableAddress("kCFTypeArrayCallBacks");
}