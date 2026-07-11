package com.ghozix.idlegenerators.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "idlegenerators")
public class IGConfig implements ConfigData {
    public boolean hudEnabled = true;
    public boolean dropContentsOnBreak = true;
    public double productionSpeedMultiplier = 1.0;

    @Override
    public void validatePostLoad() {
        if (productionSpeedMultiplier <= 0.0 || !Double.isFinite(productionSpeedMultiplier)) {
            productionSpeedMultiplier = 1.0;
        }
    }

    public static IGConfig get() {
        return AutoConfig.getConfigHolder(IGConfig.class).getConfig();
    }
}
