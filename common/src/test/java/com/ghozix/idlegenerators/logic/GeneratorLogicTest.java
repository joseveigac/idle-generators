package com.ghozix.idlegenerators.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GeneratorLogicTest {
    private static final long INTERVAL = 5_000L; // 5 s

    @Test void producesOneItemPerFullCycle() {
        var s = GeneratorLogic.settle(35_000L, 0L, 0, 512, INTERVAL);
        assertEquals(7, s.produced());
    }

    @Test void partialCycleDoesNotProduce() {
        var s = GeneratorLogic.settle(4_999L, 0L, 0, 512, INTERVAL);
        assertEquals(0, s.produced());
    }

    @Test void productionIsCappedAtCap() {
        var s = GeneratorLogic.settle(1_000_000_000L, 0L, 500, 512, INTERVAL);
        assertEquals(512, s.produced());
    }

    @Test void settleAdvancesOnlyWholeCycles() {
        // 17.5s transcurridos = 3 ciclos + 2500ms de progreso que debe conservarse
        var s = GeneratorLogic.settle(17_500L, 0L, 0, 512, INTERVAL);
        assertEquals(3 * INTERVAL, s.settledLastInteraction());
    }

    @Test void withdrawKeepsSubCycleProgress() {
        var w = GeneratorLogic.withdraw(17_500L, 0L, 0, 512, INTERVAL, 2);
        assertEquals(2, w.collected());
        assertEquals(1, w.newStored());
        assertEquals(3 * INTERVAL, w.newLastInteraction()); // NO salta a 17500
    }

    @Test void withdrawMoreThanProducedGivesAllAvailable() {
        var w = GeneratorLogic.withdraw(10_000L, 0L, 3, 512, INTERVAL, 64);
        assertEquals(5, w.collected()); // 3 en buffer + 2 ciclos
        assertEquals(0, w.newStored());
    }

    @Test void clockMovedBackwardsClampsToNow() {
        var s = GeneratorLogic.settle(1_000L, 50_000L, 7, 512, INTERVAL);
        assertEquals(7, s.produced());                 // no produce de más
        assertEquals(1_000L, s.settledLastInteraction()); // clamp a now
    }

    @Test void hugeElapsedDoesNotOverflow() {
        long fiftyYearsMs = 50L * 365 * 24 * 3600 * 1000;
        var s = GeneratorLogic.settle(fiftyYearsMs, 0L, 0, 1024, INTERVAL);
        assertEquals(1024, s.produced());
    }

    @Test void speedMultiplierShortensInterval() {
        assertEquals(2_500L, GeneratorLogic.effectiveIntervalMs(5_000L, 2.0));
        assertEquals(5_000L, GeneratorLogic.effectiveIntervalMs(5_000L, 1.0));
        // multiplicador inválido → intervalo base
        assertEquals(5_000L, GeneratorLogic.effectiveIntervalMs(5_000L, 0.0));
        assertEquals(5_000L, GeneratorLogic.effectiveIntervalMs(5_000L, -3.0));
    }

    @Test void nonPositiveIntervalIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> GeneratorLogic.settle(1_000L, 0L, 0, 512, 0L));
        assertThrows(IllegalArgumentException.class, () -> GeneratorLogic.settle(1_000L, 0L, 0, 512, -5_000L));
    }

    @Test void cappedBufferStopsAccumulatingButKeepsTimestampFresh() {
        // Con el buffer al cap, lastInteraction debe seguir avanzando para no
        // acumular "ciclos fantasma" que se cobrarían tras retirar.
        var w1 = GeneratorLogic.withdraw(100_000L, 0L, 512, 512, INTERVAL, 1);
        assertEquals(1, w1.collected());
        assertEquals(511, w1.newStored());
        // inmediatamente después no hay 20 ciclos pendientes:
        var s = GeneratorLogic.settle(w1.newLastInteraction() + 1_000L, w1.newLastInteraction(), w1.newStored(), 512, INTERVAL);
        assertEquals(511, s.produced());
    }
}
