package dev.kemmlow.inputoptimizer.rawinput;

public final class RawMouseEvent {
    public final double deltaX;
    public final double deltaY;
    public final long timestamp;

    public RawMouseEvent(double deltaX, double deltaY, long timestamp) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.timestamp = timestamp;
    }
}