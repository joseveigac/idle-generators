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

    // ── toggles de generadores (v1.3.0): tri-estado por generador + toggle de categoría ──

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

    @Test void forcedOffOverridesEnabledCategory() {
        IGConfig c = new IGConfig();
        c.generators.put("diamond", GeneratorToggle.OFF);
        assertFalse(c.isGeneratorEnabled("diamond", GeneratorCategory.ORES));
        assertTrue(c.isGeneratorEnabled("iron", GeneratorCategory.ORES));
    }

    @Test void forcedOnOverridesDisabledCategory() {
        IGConfig c = new IGConfig();
        c.oresEnabled = false;
        c.generators.put("iron", GeneratorToggle.ON);
        assertTrue(c.isGeneratorEnabled("iron", GeneratorCategory.ORES));
        assertFalse(c.isGeneratorEnabled("diamond", GeneratorCategory.ORES));
    }

    @Test void colorsCategoryFollowsItsToggleAndOverrides() {
        IGConfig c = new IGConfig();
        assertTrue(c.isGeneratorEnabled("white_dye", GeneratorCategory.COLORS));
        c.colorsEnabled = false;
        assertFalse(c.isGeneratorEnabled("white_dye", GeneratorCategory.COLORS));
        c.generators.put("white_dye", GeneratorToggle.ON);
        assertTrue(c.isGeneratorEnabled("white_dye", GeneratorCategory.COLORS));
    }

    @Test void natureCategoryFollowsItsToggleAndOverrides() {
        IGConfig c = new IGConfig();
        assertTrue(c.isGeneratorEnabled("moss", GeneratorCategory.NATURE));
        c.natureEnabled = false;
        assertFalse(c.isGeneratorEnabled("moss", GeneratorCategory.NATURE));
        c.generators.put("moss", GeneratorToggle.ON);
        assertTrue(c.isGeneratorEnabled("moss", GeneratorCategory.NATURE));
    }

    @Test void ensureAllKeysPopulatesMissingAsDefault() {
        IGConfig c = new IGConfig();
        c.generators.put("iron", GeneratorToggle.OFF);
        c.ensureAllKeys(java.util.List.of("iron", "coal"));
        assertEquals(GeneratorToggle.OFF, c.generators.get("iron")); // no pisa lo existente
        assertEquals(GeneratorToggle.DEFAULT, c.generators.get("coal"));
    }

    @Test void nullMapAndValuesAreNormalizedOnValidate() throws Exception {
        IGConfig c = new IGConfig();
        c.generators = null;
        c.validatePostLoad();
        assertNotNull(c.generators);
        c.generators.put("iron", null); // Gson deja null los valores desconocidos del JSON
        c.validatePostLoad();
        assertEquals(GeneratorToggle.DEFAULT, c.generators.get("iron"));
    }
}
