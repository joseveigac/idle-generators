package com.ghozix.idlegenerators.fabric;

import com.ghozix.idlegenerators.config.IGConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfigClient;

// MC 26.x / Cloth Config 26.2.x: AutoConfig.getConfigScreen moved to AutoConfigClient.getConfigScreen
// (screen entry point is now client-only; register/getConfigHolder remain on AutoConfig).
public class IGModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfigClient.getConfigScreen(IGConfig.class, parent).get();
    }
}
