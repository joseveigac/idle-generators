package com.ghozix.idlegenerators.neoforge;

import com.ghozix.idlegenerators.IdleGenerators;
import com.ghozix.idlegenerators.client.IGClient;
import com.ghozix.idlegenerators.config.IGConfig;
import me.shedaniel.autoconfig.AutoConfigClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// MC 26.x / FML changes:
//   - FMLEnvironment.dist (field) → FMLEnvironment.getDist() (method)
//   - AutoConfig.getConfigScreen → AutoConfigClient.getConfigScreen (client-only class)
@Mod(IdleGenerators.MOD_ID)
public final class IdleGeneratorsNeoForge {
    public IdleGeneratorsNeoForge(ModContainer container) {
        // Run our common setup.
        IdleGenerators.init();

        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            IGClient.init();
            container.registerExtensionPoint(IConfigScreenFactory.class,
                    (mc, parent) -> AutoConfigClient.getConfigScreen(IGConfig.class, parent).get());
        }
    }
}
