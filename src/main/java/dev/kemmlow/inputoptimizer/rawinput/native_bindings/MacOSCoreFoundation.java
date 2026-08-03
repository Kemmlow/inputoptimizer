package dev.kemmlow.inputoptimizer.rawinput.native_bindings;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;

public interface MacOSCoreFoundation extends Library {
    NativeLibrary CF_LIB = NativeLibrary.getInstance("CoreFoundation");

    MacOSCoreFoundation INSTANCE = Native.load("CoreFoundation", MacOSCoreFoundation.class);

    Pointer CFRunLoopGetCurrent();
    void    CFRunLoopRun();
    void    CFRunLoopStop(Pointer runLoop);

    Pointer CFDictionaryCreateMutable(Pointer allocator, int capacity,
                                      Pointer keyCallBacks, Pointer valueCallBacks);
    void    CFDictionarySetValue(Pointer dict, Pointer key, Pointer value);

    Pointer CFNumberCreate(Pointer allocator, int theType, Pointer valuePtr);

    Pointer CFStringCreateWithCString(Pointer allocator, String cStr, int encoding);

    Pointer CFArrayCreate(Pointer allocator, Pointer[] values, int numValues, Pointer callBacks);

    void    CFRelease(Pointer obj);
    Pointer CFRetain(Pointer obj);

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