package dev.kemmlow.inputoptimizer.rawinput.platform;

import dev.kemmlow.inputoptimizer.rawinput.*;
import dev.kemmlow.inputoptimizer.rawinput.native_bindings.LinuxEvdev;
import org.lwjgl.glfw.GLFW;
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
            int fd = LinuxEvdev.INSTANCE.open(dev.getAbsolutePath(), LinuxEvdev.O_RDONLY | LinuxEvdev.O_NONBLOCK);
            if (fd >= 0) fds.add(fd);
        }
        this.running = true;
        Thread t = new Thread(this::readLoop, "inputoptimizer-Linux-evdev");
        t.setPriority(Thread.MAX_PRIORITY);
        t.setDaemon(true);
        t.start();
        return true;
    }

    private void readLoop() {
        byte[] buffer = new byte[LinuxEvdev.InputEvent.SIZE * 64]; // Read 64 events at once
        while (this.running) {
            boolean readAny = false;
            for (int fd : fds) {
                int bytes = LinuxEvdev.INSTANCE.read(fd, buffer, buffer.length);
                if (bytes > 0 && focused) {
                    readAny = true;
                    processEvents(buffer, bytes);
                }
            }
            if (!readAny) try { Thread.sleep(1); } catch (Exception ignored) {}
        }
    }

    private void processEvents(byte[] data, int bytes) {
        ByteBuffer bb = ByteBuffer.wrap(data, 0, bytes).order(ByteOrder.nativeOrder());
        while (bb.remaining() >= LinuxEvdev.InputEvent.SIZE) {
            bb.getLong(); bb.getLong(); // skip time
            short type = bb.getShort(); short code = bb.getShort(); int val = bb.getInt();
            long now = System.nanoTime();
            if (type == 0x02) { // EV_REL
                if (code == 0x00) accumulateDelta(val, 0);
                else if (code == 0x01) accumulateDelta(0, val);
            } else if (type == 0x01 && val != 2) { // EV_KEY
                int glfw = LinuxKeyToGlfw.convert(code);
                if (glfw != -1) keyboardQueue.add(new RawKeyEvent(glfw, code, val, 0, now));
            }
        }
    }

    @Override public void centerCursor() {}
    @Override public String platformName() { return "Linux evdev-Multi"; }
    @Override public void shutdown() { this.running = false; }
}