package com.ghozix.idlegenerators.config;

import com.ghozix.idlegenerators.generator.GeneratorCategory;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

import java.util.ArrayList;
import java.util.List;

@Config(name = "idlegenerators")
public class IGConfig implements ConfigData {
    public boolean hudEnabled = true;
    public boolean showHudWithJade = false;
    public boolean dropContentsOnBreak = true;
    public double productionSpeedMultiplier = 1.0;
    public boolean oresEnabled = true;
    public boolean woodsEnabled = true;
    public boolean stonesEnabled = true;
    public List<String> enabledGenerators = new ArrayList<>();
    public List<String> disabledGenerators = new ArrayList<>();

    /** Predicado de activación (v1.3.0): override individual > toggle de categoría. */
    public boolean isGeneratorEnabled(String key, GeneratorCategory category) {
        if (enabledGenerators.contains(key)) return true;
        if (disabledGenerators.contains(key)) return false;
        return switch (category) {
            case ORES -> oresEnabled;
            case WOODS -> woodsEnabled;
            case STONES -> stonesEnabled;
        };
    }

    @Override
    public void validatePostLoad() {
        if (productionSpeedMultiplier <= 0.0 || !Double.isFinite(productionSpeedMultiplier)) {
            productionSpeedMultiplier = 1.0;
        }
        // Gson puede dejar las listas a null si el JSON las trae malformadas.
        if (enabledGenerators == null) enabledGenerators = new ArrayList<>();
        if (disabledGenerators == null) disabledGenerators = new ArrayList<>();
    }

    public static IGConfig get() {
        return AutoConfig.getConfigHolder(IGConfig.class).getConfig();
    }
}
