package dev.kemmlow.inputoptimizer.rawinput.platform;

import dev.kemmlow.inputoptimizer.rawinput.*;
import dev.kemmlow.inputoptimizer.rawinput.native_bindings.LinuxEvdev;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class LinuxRawInputEngine extends RawInputEngine {
    private final java.util.List<Integer> fds = new java.util.ArrayList<>();

    @Override
    public boolean initialize(long glfwWindow) {
        java.io.File inputDir = new java.io.File("/dev/input");
        java.io.File[] devices = inputDir.listFiles((dir, name) -> name.startsWith("event"));
        if (devices == null) return false;
        for (java.io.File dev : devices) {
            int fd = LinuxEvdev.INSTANCE.open(
                dev.getAbsolutePath(), LinuxEvdev.O_RDONLY | LinuxEvdev.O_NONBLOCK);
            if (fd >= 0) fds.add(fd);
        }
        if (fds.isEmpty()) return false;
        this.running = true;
        Thread t = new Thread(this::readLoop, "inputoptimizer-Linux-evdev");
        t.setPriority(Thread.MAX_PRIORITY);
        t.setDaemon(true);
        t.start();
        return true;
    }

    private void readLoop() {
        byte[] buffer = new byte[LinuxEvdev.InputEvent.SIZE * 64];
        while (this.running) {
            boolean readAny = false;
            for (int fd : fds) {
                int bytes = LinuxEvdev.INSTANCE.read(fd, buffer, buffer.length);
                if (bytes > 0 && this.focused) {
                    readAny = true;
                    processEvents(buffer, bytes);
                }
            }
            if (!readAny) {
                try { Thread.sleep(0, 500_000); } catch (Exception ignored) {}
            }
        }
    }

    private void processEvents(byte[] data, int bytes) {
        ByteBuffer bb = ByteBuffer.wrap(data, 0, bytes).order(ByteOrder.nativeOrder());
        while (bb.remaining() >= LinuxEvdev.InputEvent.SIZE) {
            bb.getLong();
            bb.getLong();
            short type = bb.getShort();
            short code = bb.getShort();
            int   val  = bb.getInt();
            long  now  = System.nanoTime();

            if (type == 0x02) {
                if      (code == 0x00) accumulateDelta(val, 0);
                else if (code == 0x01) accumulateDelta(0, val);
                else if (code == 0x08) scrollQueue.add(new RawScrollEvent(0.0, val, now));
                else if (code == 0x06) scrollQueue.add(new RawScrollEvent(val, 0.0, now));
            } else if (type == 0x01 && val != 2) {
                if (code >= 0x110 && code <= 0x114) {
                    buttonQueue.add(new RawButtonEvent(code - 0x110, val, 0, now));
                } else {
                    int glfw = LinuxKeyToGlfw.convert(code);
                    if (glfw != -1) keyboardQueue.add(new RawKeyEvent(glfw, code, val, 0, now));
                }
            }
        }
    }

    @Override public void centerCursor() {}
    @Override public String platformName() { return "Linux evdev"; }

    @Override
    public void shutdown() {
        this.running = false;
        for (int fd : fds) LinuxEvdev.INSTANCE.close(fd);
        fds.clear();
    }
}