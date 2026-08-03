package dev.kemmlow.inputoptimizer;

import dev.kemmlow.inputoptimizer.Main;

public final class TickGate {
    private TickGate() {}

    private static boolean attackFired = false;
    private static boolean continueAttackFired = false;
    private static boolean useFired = false;

    public static boolean tryAttack() {
        if (!Main.getConfig().isEnabled()) return true;
        if (attackFired) return false;
        attackFired = true;
        return true;
    }

    public static boolean tryContinueAttack() {
        if (!Main.getConfig().isEnabled()) return true;
        if (continueAttackFired) return false;
        continueAttackFired = true;
        return true;
    }

    public static boolean tryUse() {
        if (!Main.getConfig().isEnabled()) return true;
        if (useFired) return false;
        useFired = true;
        return true;
    }

    public static void reset() {
        attackFired = false;
        continueAttackFired = false;
        useFired = false;
    }
}
