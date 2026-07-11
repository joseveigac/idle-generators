package com.ghozix.idlegenerators.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IGConfigTest {
    @Test void defaultsMatchSpec() {
        IGConfig c = new IGConfig();
        assertTrue(c.hudEnabled);
        assertTrue(c.dropContentsOnBreak);
        assertEquals(1.0, c.productionSpeedMultiplier);
    }

    @Test void invalidMultiplierIsResetOnValidate() throws Exception {
        IGConfig c = new IGConfig();
        c.productionSpeedMultiplier = -2.0;
        c.validatePostLoad();
        assertEquals(1.0, c.productionSpeedMultiplier);
    }
}
