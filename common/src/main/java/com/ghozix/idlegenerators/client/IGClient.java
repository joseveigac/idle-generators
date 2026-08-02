package com.ghozix.idlegenerators.client;

import com.ghozix.idlegenerators.registry.ModBlockEntities;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class IGClient {
    private IGClient() {}

    public static void init() {
        // listen() dispara cuando el BE type ya está registrado (inmediato en el
        // client init de Fabric; al poblarse los registros en NeoForge). OJO: no
        // usar ClientLifecycleEvent.CLIENT_SETUP aquí — en Fabric lo dispara el
        // entrypoint client de Architectury, que corre ANTES que el nuestro (orden
        // de dependencias), así que un listener registrado aquí no llega a verlo.
        ModBlockEntities.GENERATOR.listen(type ->
                BlockEntityRendererRegistry.register(type, ctx -> new GeneratorCoreRenderer(ctx)));

        // v1.3.0: la GUI del mapa de toggles (desplegables por categoría) es un provider custom.
        IGConfigGui.register();
    }
}
