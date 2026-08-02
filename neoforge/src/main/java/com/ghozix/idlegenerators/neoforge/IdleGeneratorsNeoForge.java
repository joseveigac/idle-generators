package com.ghozix.idlegenerators.neoforge;

import com.ghozix.idlegenerators.IdleGenerators;
import com.ghozix.idlegenerators.client.IGClient;
import com.ghozix.idlegenerators.config.IGConfig;
import com.ghozix.idlegenerators.registry.ModBlockEntities;
import me.shedaniel.autoconfig.AutoConfigClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;

// MC 26.x / FML changes:
//   - FMLEnvironment.dist (field) → FMLEnvironment.getDist() (method)
//   - AutoConfig.getConfigScreen → AutoConfigClient.getConfigScreen (client-only class)
@Mod(IdleGenerators.MOD_ID)
public final class IdleGeneratorsNeoForge {
    public IdleGeneratorsNeoForge(IEventBus modBus, ModContainer container) {
        // Run our common setup.
        IdleGenerators.init();

        modBus.addListener(IdleGeneratorsNeoForge::registerCapabilities);

        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            IGClient.init();
            container.registerExtensionPoint(IConfigScreenFactory.class,
                    (mc, parent) -> AutoConfigClient.getConfigScreen(IGConfig.class, parent).get());
        }
    }

    // v1.3.0: expone el buffer a tubos NeoForge y a la ruta capability de sus tolvas.
    // VanillaContainerWrapper respeta canPlaceItem del Container (verificado en bytecode
    // 26.2.0.8-beta) → la inserción queda bloqueada; extracción libre por cualquier cara.
    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.GENERATOR.get(),
                (be, side) -> VanillaContainerWrapper.of(be));
    }
}
