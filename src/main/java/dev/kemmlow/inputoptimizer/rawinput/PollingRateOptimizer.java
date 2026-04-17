package dev.kemmlow.inputoptimizer.rawinput;

import java.util.concurrent.ConcurrentLinkedQueue;

public final class PollingRateOptimizer {
    private final ConcurrentLinkedQueue<TimedDelta> deltaQueue = new ConcurrentLinkedQueue<>();

    public void addDelta(double dx, double dy, long timestampNanos) {
        this.deltaQueue.add(new TimedDelta(dx, dy, timestampNanos));
    }

    public double[] consumeWeighted(long frameTimeNanos) {
        double totalX = 0.0;
        double totalY = 0.0;
        double totalWeight = 0.0;
        long now = System.nanoTime();

        TimedDelta delta;
        while ((delta = this.deltaQueue.poll()) != null) {
            long age = now - delta.timestamp;
            double weight = 1.0;
            if (frameTimeNanos > 0 && age < frameTimeNanos) {
                weight = 1.0 + ((double) (frameTimeNanos - age) / frameTimeNanos);
            }
            totalX += delta.dx * weight;
            totalY += delta.dy * weight;
            totalWeight += weight;
        }

        if (totalWeight == 0.0) return new double[]{0.0, 0.0};
        double scale = Math.max(totalWeight / 2.0, 1.0);
        return new double[]{totalX / scale, totalY / scale};
    }

    public void clear() {
        this.deltaQueue.clear();
    }

    private record TimedDelta(double dx, double dy, long timestamp) {}
}