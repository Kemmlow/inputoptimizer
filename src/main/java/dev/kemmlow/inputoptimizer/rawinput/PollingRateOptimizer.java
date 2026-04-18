package dev.kemmlow.inputoptimizer.rawinput;

import java.util.concurrent.atomic.AtomicLong;

public final class PollingRateOptimizer {
    private final AtomicLong frameAccumX = new AtomicLong(0);
    private final AtomicLong frameAccumY = new AtomicLong(0);

    public void addDelta(double dx, double dy, long timestampNanos) {
        addAtomic(this.frameAccumX, dx);
        addAtomic(this.frameAccumY, dy);
    }

    public double[] consumeWeighted(long frameTimeNanos) {
        double x = Double.longBitsToDouble(this.frameAccumX.getAndSet(0));
        double y = Double.longBitsToDouble(this.frameAccumY.getAndSet(0));
        return new double[]{x, y};
    }

    public void clear() {
        this.frameAccumX.set(0);
        this.frameAccumY.set(0);
    }

    private static void addAtomic(AtomicLong bits, double value) {
        long prev, next;
        do {
            prev = bits.get();
            next = Double.doubleToLongBits(Double.longBitsToDouble(prev) + value);
        } while (!bits.compareAndSet(prev, next));
    }
}