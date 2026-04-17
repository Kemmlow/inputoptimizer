package dev.kemmlow.inputoptimizer.rawinput;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public abstract class RawInputEngine {
    protected final AtomicLong deltaXBits = new AtomicLong(0);
    protected final AtomicLong deltaYBits = new AtomicLong(0);
    protected final ConcurrentLinkedQueue<RawButtonEvent> buttonQueue = new ConcurrentLinkedQueue<>();
    protected final ConcurrentLinkedQueue<RawScrollEvent> scrollQueue = new ConcurrentLinkedQueue<>();
    protected final ConcurrentLinkedQueue<RawKeyEvent> keyboardQueue = new ConcurrentLinkedQueue<>();
    protected final PollingRateOptimizer pollingOptimizer = new PollingRateOptimizer();
    protected volatile boolean running = false;
    protected volatile boolean focused = false;
    protected volatile boolean gameFocused = false;
    protected volatile boolean usePollingOptimizer = true;

    public abstract boolean initialize(long glfwWindow);

    public abstract void shutdown();

    public abstract void centerCursor();

    public abstract String platformName();

    public void accumulateDelta(double dx, double dy) {
        if (this.usePollingOptimizer) {
            this.pollingOptimizer.addDelta(dx, dy, System.nanoTime());
        }
        addAtomicDouble(this.deltaXBits, dx);
        addAtomicDouble(this.deltaYBits, dy);
    }

    public double pollDeltaX() {
        return Double.longBitsToDouble(this.deltaXBits.getAndSet(0));
    }

    public double pollDeltaY() {
        return Double.longBitsToDouble(this.deltaYBits.getAndSet(0));
    }

    public double[] pollWeightedDeltas(long frameTimeNanos) {
        this.deltaXBits.set(0);
        this.deltaYBits.set(0);
        return this.pollingOptimizer.consumeWeighted(frameTimeNanos);
    }

    public void resetDeltas() {
        this.deltaXBits.set(0);
        this.deltaYBits.set(0);
        this.pollingOptimizer.clear();
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
            this.resetDeltas();
            this.clearEvents();
        }
    }

    public void setGameFocused(boolean gameFocused) {
        this.gameFocused = gameFocused;
    }

    public PollingRateOptimizer getPollingOptimizer() {
        return this.pollingOptimizer;
    }

    private static void addAtomicDouble(AtomicLong bits, double value) {
        long prev;
        long next;
        do {
            prev = bits.get();
            double current = Double.longBitsToDouble(prev);
            next = Double.doubleToLongBits(current + value);
        } while (!bits.compareAndSet(prev, next));
    }
}