package dev.kemmlow.inputoptimizer.rawinput;

public final class RawKeyEvent {
    public final int key;
    public final int scancode;
    public final int action;
    public final int modifiers;
    public final long timestamp;

    public RawKeyEvent(int key, int scancode, int action, int modifiers, long timestamp) {
        this.key = key;
        this.scancode = scancode;
        this.action = action;
        this.modifiers = modifiers;
        this.timestamp = timestamp;
    }
}