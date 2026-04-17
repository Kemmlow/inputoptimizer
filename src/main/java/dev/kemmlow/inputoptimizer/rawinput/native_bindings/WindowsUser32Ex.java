package dev.kemmlow.inputoptimizer.rawinput.native_bindings;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface WindowsUser32Ex extends StdCallLibrary {
    WindowsUser32Ex INSTANCE = Native.load("user32", WindowsUser32Ex.class, W32APIOptions.DEFAULT_OPTIONS);

    @Structure.FieldOrder({"usUsagePage", "usUsage", "dwFlags", "hwndTarget"})
    class RAWINPUTDEVICE extends Structure {
        public short usUsagePage;
        public short usUsage;
        public int dwFlags;
        public HWND hwndTarget;
    }

    boolean RegisterRawInputDevices(RAWINPUTDEVICE[] pRawInputDevices, int uiNumDevices, int cbSize);
    int GetRawInputBuffer(Pointer pData, IntByReference pcbSize, int cbSizeHeader);
}