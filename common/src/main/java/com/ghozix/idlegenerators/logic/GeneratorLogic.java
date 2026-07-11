package com.ghozix.idlegenerators.logic;

/**
 * Matemática de producción pura (sin Minecraft). Misma semántica que el addon
 * Bedrock v1.1.0: producción lazy por timestamp real, con la invariante de que
 * lastInteraction solo avanza ciclos ENTEROS (el progreso de sub-ciclo se conserva).
 */
public final class GeneratorLogic {
    private GeneratorLogic() {}

    public record Settle(int produced, long settledLastInteraction) {}
    public record Withdraw(int collected, int newStored, long newLastInteraction) {}

    public static long effectiveIntervalMs(long baseIntervalMs, double speedMultiplier) {
        if (speedMultiplier <= 0.0 || !Double.isFinite(speedMultiplier)) return baseIntervalMs;
        return Math.max(50L, Math.round(baseIntervalMs / speedMultiplier));
    }

    public static Settle settle(long nowMs, long lastInteractionMs, int stored, int cap, long intervalMs) {
        if (intervalMs <= 0) throw new IllegalArgumentException("intervalMs must be positive: " + intervalMs);
        if (nowMs < lastInteractionMs) {
            // Reloj del sistema movido hacia atrás: clamp, el buffer se conserva.
            return new Settle(Math.min(stored, cap), nowMs);
        }
        long cycles = (nowMs - lastInteractionMs) / intervalMs;
        long produced = Math.min((long) stored + cycles, cap);
        // Al tope, el excedente de ciclos se descarta avanzando el timestamp
        // hasta el último ciclo entero (nunca acumula "ciclos fantasma").
        return new Settle((int) produced, lastInteractionMs + cycles * intervalMs);
    }

    public static Withdraw withdraw(long nowMs, long lastInteractionMs, int stored, int cap, long intervalMs, int requested) {
        Settle s = settle(nowMs, lastInteractionMs, stored, cap, intervalMs);
        int collected = Math.min(s.produced(), Math.max(0, requested));
        return new Withdraw(collected, s.produced() - collected, s.settledLastInteraction());
    }
}
