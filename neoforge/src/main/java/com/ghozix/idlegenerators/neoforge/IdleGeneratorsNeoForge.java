package com.ghozix.idlegenerators.neoforge;

import com.ghozix.idlegenerators.IdleGenerators;
import com.ghozix.idlegenerators.client.IGClient;
import com.ghozix.idlegenerators.config.IGConfig;
import com.ghozix.idlegenerators.registry.ModBlockEntities;
import me.shedaniel.autoconfig.AutoConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

@Mod(IdleGenerators.MOD_ID)
public final class IdleGeneratorsNeoForge {
    public IdleGeneratorsNeoForge(IEventBus modBus, ModContainer container) {
        // Run our common setup.
        IdleGenerators.init();

        modBus.addListener(IdleGeneratorsNeoForge::registerCapabilities);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            IGClient.init();
            container.registerExtensionPoint(IConfigScreenFactory.class,
                    (mc, parent) -> AutoConfig.getConfigScreen(IGConfig.class, parent).get());
        }
    }

    // v1.3.0: expone el buffer a tubos NeoForge y a la ruta capability de sus tolvas.
    // 1.21.1 = API pre-rework: IItemHandler vía SidedInvWrapper (respeta las reglas sided
    // del WorldlyContainer; con side null nuestras overrides igualmente bloquean inserción).
    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.GENERATOR.get(),
                (be, side) -> new SidedInvWrapper(be, side));
    }
}
