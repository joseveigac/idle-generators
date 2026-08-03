package com.ghozix.idlegenerators.neoforge;

import com.ghozix.idlegenerators.IdleGenerators;
import com.ghozix.idlegenerators.client.IGClient;
import com.ghozix.idlegenerators.config.IGConfig;
import com.ghozix.idlegenerators.config.IGConfigReload;
import com.ghozix.idlegenerators.registry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import me.shedaniel.autoconfig.AutoConfigClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;

// MC 26.x / FML changes:
//   - FMLEnvironment.dist (field) → FMLEnvironment.getDist() (method)
//   - AutoConfig.getConfigScreen → AutoConfigClient.getConfigScreen (client-only class)
@Mod(IdleGenerators.MOD_ID)
public final class IdleGeneratorsNeoForge {
    // v1.3.0 R4: condición de carga que descarta la receta de un generador desactivado.
    private static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS =
            DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, IdleGenerators.MOD_ID);

    static {
        CONDITION_CODECS.register("generator_enabled", () -> GeneratorEnabledCondition.CODEC);
    }

    public IdleGeneratorsNeoForge(IEventBus modBus, ModContainer container) {
        // Run our common setup.
        IdleGenerators.init();

        CONDITION_CODECS.register(modBus);
        modBus.addListener(IdleGeneratorsNeoForge::registerCapabilities);

        // v1.3.0 R4: releer la config antes de que /reload evalúe las condiciones. Este evento del
        // game bus se dispara al construir la lista de listeners, antes de ejecutar la recarga; el
        // prepareSharedState del listener de Architectury NO vale — ver IGConfigReload.
        NeoForge.EVENT_BUS.addListener(AddServerReloadListenersEvent.class,
                event -> IGConfigReload.reloadConfigFromDisk());

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
