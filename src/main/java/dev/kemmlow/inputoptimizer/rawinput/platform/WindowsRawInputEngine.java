package dev.kemmlow.inputoptimizer.rawinput.platform;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.*;
import com.sun.jna.platform.win32.WinUser.WindowProc;
import com.sun.jna.ptr.IntByReference;
import dev.kemmlow.inputoptimizer.rawinput.*;
import dev.kemmlow.inputoptimizer.rawinput.native_bindings.WindowsUser32Ex;
import org.lwjgl.glfw.GLFW;

public class WindowsRawInputEngine extends RawInputEngine {
    private static final int WM_INPUT = 0x00FF;
    private static final int RID_INPUT = 0x10000003;
    private static final int RIM_TYPEMOUSE = 0;
    private static final int RIM_TYPEKEYBOARD = 1;
    private static final int RIDEV_INPUTSINK = 0x00000100;
    private static final int RIDEV_NOLEGACY = 0x00000030;
    private static final int HEADER_SIZE = 8 + (2 * Native.POINTER_SIZE);
    private static final int MOUSE_SIZE = HEADER_SIZE + 24;
    private static final int KB_SIZE = HEADER_SIZE + 16;

    private static final int BATCH_SIZE = 8192; // Large buffer for high polling rates
    private final Memory batchBuffer = new Memory(BATCH_SIZE);
    private final IntByReference pcbSize = new IntByReference(BATCH_SIZE);

    private WinDef.HWND targetHwnd;
    private String windowClass;
    private volatile int centerX, centerY;

    @Override
    public boolean initialize(long glfwWindow) {
        this.windowClass = "inputoptimizer_Win32_" + glfwWindow;
        this.running = true;
        Thread thread = new Thread(this::messageLoop, "inputoptimizer-Win32-RawBatch");
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.setDaemon(true);
        thread.start();
        return true;
    }

    private void messageLoop() {
        WinDef.HINSTANCE hInst = Kernel32.INSTANCE.GetModuleHandle(null);
        WinUser.WNDCLASSEX wc = new WinUser.WNDCLASSEX();
        wc.lpfnWndProc = (WindowProc) (hwnd, uMsg, wParam, lParam) -> {
            if (uMsg == WM_INPUT) drainBuffer();
            return User32.INSTANCE.DefWindowProc(hwnd, uMsg, wParam, lParam);
        };
        wc.lpszClassName = this.windowClass;
        wc.hInstance = hInst;
        wc.cbSize = wc.size();
        User32.INSTANCE.RegisterClassEx(wc);

        this.targetHwnd = User32.INSTANCE.CreateWindowEx(0, this.windowClass, "inputoptimizerTarget", 0, 0, 0, 0, 0, null, null, hInst, null);
        register(false);

        WinUser.MSG msg = new WinUser.MSG();
        while (this.running && User32.INSTANCE.GetMessage(msg, null, 0, 0) > 0) {
            User32.INSTANCE.TranslateMessage(msg);
            User32.INSTANCE.DispatchMessage(msg);
        }
        User32.INSTANCE.UnregisterClass(this.windowClass, hInst);
    }

    private void drainBuffer() {
        if (!this.focused) return;
        while (true) {
            this.pcbSize.setValue(BATCH_SIZE);
            int count = WindowsUser32Ex.INSTANCE.GetRawInputBuffer(batchBuffer, pcbSize, HEADER_SIZE);
            if (count <= 0) break;

            long now = System.nanoTime();
            int offset = 0;
            for (int i = 0; i < count; i++) {
                Pointer ptr = batchBuffer.share(offset);
                int type = ptr.getInt(0);
                if (type == RIM_TYPEMOUSE) {
                    processMouse(ptr, now);
                    offset += align(MOUSE_SIZE);
                } else if (type == RIM_TYPEKEYBOARD) {
                    processKeyboard(ptr, now);
                    offset += align(KB_SIZE);
                } else break;
            }
        }
    }

    private void processMouse(Pointer p, long now) {
        if ((p.getShort(HEADER_SIZE) & 0x01) == 0) { // Relative motion
            accumulateDelta(p.getInt(HEADER_SIZE + 12), p.getInt(HEADER_SIZE + 16));
            if (this.gameFocused) centerCursor();
        }
        int btnFlags = p.getShort(HEADER_SIZE + 4) & 0xFFFF;
        if (btnFlags != 0) {
            if ((btnFlags & 0x0001) != 0) this.buttonQueue.add(new RawButtonEvent(0, 1, 0, now));
            if ((btnFlags & 0x0002) != 0) this.buttonQueue.add(new RawButtonEvent(0, 0, 0, now));
            // ... (Add other buttons 1-4 here using standard Raw Input mask)
        }
    }

    private void processKeyboard(Pointer p, long now) {
        if ((p.getShort(HEADER_SIZE) & 0x04) != 0) return; // Repeat
        int vk = p.getShort(HEADER_SIZE + 4) & 0xFFFF;
        int msg = p.getInt(HEADER_SIZE + 8);
        int glfw = VKeyToGlfw.convert((short)vk);
        if (glfw != -1) this.keyboardQueue.add(new RawKeyEvent(glfw, 0, (msg == 0x0100 ? 1 : 0), 0, now));
    }

    private int align(int s) { return (s + Native.POINTER_SIZE - 1) & ~(Native.POINTER_SIZE - 1); }

    private void register(boolean excl) {
        WindowsUser32Ex.RAWINPUTDEVICE[] devs = (WindowsUser32Ex.RAWINPUTDEVICE[]) new WindowsUser32Ex.RAWINPUTDEVICE().toArray(2);
        devs[0].usUsagePage = 0x01; devs[0].usUsage = 0x02; devs[0].hwndTarget = targetHwnd;
        devs[0].dwFlags = excl ? (RIDEV_INPUTSINK | RIDEV_NOLEGACY) : RIDEV_INPUTSINK;
        devs[1].usUsagePage = 0x01; devs[1].usUsage = 0x06; devs[1].hwndTarget = targetHwnd; devs[1].dwFlags = RIDEV_INPUTSINK;
        WindowsUser32Ex.INSTANCE.RegisterRawInputDevices(devs, 2, devs[0].size());
    }

    public void updateCenter(int x, int y) { this.centerX = x; this.centerY = y; }
    @Override public void centerCursor() { if (centerX != 0) User32.INSTANCE.SetCursorPos(centerX, centerY); }
    @Override public void shutdown() { this.running = false; }
    @Override public String platformName() { return "Windows Raw-Batch"; }
}