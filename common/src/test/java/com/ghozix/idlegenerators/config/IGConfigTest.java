package com.ghozix.idlegenerators.config;

import com.ghozix.idlegenerators.generator.GeneratorCategory;
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

    // ── toggles de generadores (v1.3.0) ──

    @Test void allGeneratorsEnabledByDefault() {
        IGConfig c = new IGConfig();
        assertTrue(c.isGeneratorEnabled("iron", GeneratorCategory.ORES));
        assertTrue(c.isGeneratorEnabled("oak_log", GeneratorCategory.WOODS));
        assertTrue(c.isGeneratorEnabled("cobblestone", GeneratorCategory.STONES));
    }

    @Test void categoryToggleDisablesOnlyItsCategory() {
        IGConfig c = new IGConfig();
        c.oresEnabled = false;
        assertFalse(c.isGeneratorEnabled("iron", GeneratorCategory.ORES));
        assertTrue(c.isGeneratorEnabled("oak_log", GeneratorCategory.WOODS));
    }

    @Test void individualDisableOverridesEnabledCategory() {
        IGConfig c = new IGConfig();
        c.disabledGenerators.add("diamond");
        assertFalse(c.isGeneratorEnabled("diamond", GeneratorCategory.ORES));
        assertTrue(c.isGeneratorEnabled("iron", GeneratorCategory.ORES));
    }

    @Test void individualEnableOverridesDisabledCategory() {
        IGConfig c = new IGConfig();
        c.oresEnabled = false;
        c.enabledGenerators.add("iron");
        assertTrue(c.isGeneratorEnabled("iron", GeneratorCategory.ORES));
        assertFalse(c.isGeneratorEnabled("diamond", GeneratorCategory.ORES));
    }

    @Test void enabledListWinsIfKeyIsInBothLists() {
        IGConfig c = new IGConfig();
        c.enabledGenerators.add("iron");
        c.disabledGenerators.add("iron");
        assertTrue(c.isGeneratorEnabled("iron", GeneratorCategory.ORES));
    }

    @Test void nullListsAreNormalizedOnValidate() throws Exception {
        IGConfig c = new IGConfig();
        c.enabledGenerators = null;
        c.disabledGenerators = null;
        c.validatePostLoad();
        assertNotNull(c.enabledGenerators);
        assertNotNull(c.disabledGenerators);
    }
}
