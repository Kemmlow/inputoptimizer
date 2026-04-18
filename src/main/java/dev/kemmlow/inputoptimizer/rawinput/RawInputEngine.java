package dev.kemmlow.inputoptimizer.rawinput;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.DoubleAdder;

public abstract class RawInputEngine {
    protected final DoubleAdder deltaX = new DoubleAdder();
    protected final DoubleAdder deltaY = new DoubleAdder();
    protected final ConcurrentLinkedQueue<RawButtonEvent> buttonQueue = new ConcurrentLinkedQueue<>();
    protected final ConcurrentLinkedQueue<RawScrollEvent> scrollQueue = new ConcurrentLinkedQueue<>();
    protected final ConcurrentLinkedQueue<RawKeyEvent> keyboardQueue = new ConcurrentLinkedQueue<>();
    protected volatile boolean running = false;
    protected volatile boolean focused = false;
    protected volatile boolean gameFocused = false;

    public abstract boolean initialize(long glfwWindow);
    public abstract void shutdown();
    public abstract void centerCursor();
    public abstract String platformName();

    public void accumulateDelta(double dx, double dy) {
        deltaX.add(dx);
        deltaY.add(dy);
    }

    public double[] pollBothDeltas() {
        return new double[]{deltaX.sumThenReset(), deltaY.sumThenReset()};
    }

    public void resetDeltas() {
        deltaX.reset();
        deltaY.reset();
    }

    public RawButtonEvent pollButton() {
        return this.buttonQueue.poll();
    }

    public RawScrollEvent pollScroll() {
        return this.scrollQueue.poll();
    }

    public RawKeyEvent pollKey() {
        return this.keyboardQueue.poll();
    }

    public void clearEvents() {
        this.buttonQueue.clear();
        this.scrollQueue.clear();
        this.keyboardQueue.clear();
    }

    public boolean isRunning() {
        return this.running;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
        if (!focused) {
            resetDeltas();
            clearEvents();
        }
    }

    public void setGameFocused(boolean gameFocused) {
        this.gameFocused = gameFocused;
    }
}