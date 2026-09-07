package com.ghozix.idlegenerators.config;

import com.ghozix.idlegenerators.generator.GeneratorCategory;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Config(name = "idlegenerators")
public class IGConfig implements ConfigData {
    public boolean hudEnabled = true;
    public boolean showHudWithJade = false;
    public boolean dropContentsOnBreak = true;
    public double productionSpeedMultiplier = 1.0;
    public boolean oresEnabled = true;
    public boolean woodsEnabled = true;
    public boolean stonesEnabled = true;
    public boolean colorsEnabled = true;
    public boolean natureEnabled = true;
    /** key → tri-estado. En la GUI se pinta con un provider custom (IGConfigGui):
     *  desplegable por categoría con un selector por generador, nada de escribir claves. */
    public Map<String, GeneratorToggle> generators = new LinkedHashMap<>();

    /** Predicado de activación (v1.3.0): override individual > toggle de categoría. */
    public boolean isGeneratorEnabled(String key, GeneratorCategory category) {
        return switch (generators.getOrDefault(key, GeneratorToggle.DEFAULT)) {
            case ON -> true;
            case OFF -> false;
            case DEFAULT -> switch (category) {
                case ORES -> oresEnabled;
                case WOODS -> woodsEnabled;
                case STONES -> stonesEnabled;
                case COLORS -> colorsEnabled;
                case NATURE -> natureEnabled;
            };
        };
    }

    /** Rellena las claves que falten como DEFAULT (así el JSON lista todos los generadores). */
    public void ensureAllKeys(Collection<String> keys) {
        for (String key : keys) generators.putIfAbsent(key, GeneratorToggle.DEFAULT);
    }

    @Override
    public void validatePostLoad() {
        if (productionSpeedMultiplier <= 0.0 || !Double.isFinite(productionSpeedMultiplier)) {
            productionSpeedMultiplier = 1.0;
        }
        // Gson deja a null el mapa o sus valores si el JSON viene malformado.
        if (generators == null) generators = new LinkedHashMap<>();
        generators.replaceAll((k, v) -> v == null ? GeneratorToggle.DEFAULT : v);
    }

    public static IGConfig get() {
        return AutoConfig.getConfigHolder(IGConfig.class).getConfig();
    }
}
