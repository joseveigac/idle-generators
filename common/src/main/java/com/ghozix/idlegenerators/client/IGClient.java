package com.ghozix.idlegenerators.client;

import com.ghozix.idlegenerators.registry.ModBlockEntities;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class IGClient {
    private IGClient() {}

    public static void init() {
        // En CLIENT_SETUP los registros ya están poblados en ambos loaders.
        ClientLifecycleEvent.CLIENT_SETUP.register(mc ->
                BlockEntityRendererRegistry.register(ModBlockEntities.GENERATOR.get(),
                        ctx -> new GeneratorCoreRenderer()));
    }
}
