package com.ghozix.idlegenerators.fabric.client;

import com.ghozix.idlegenerators.registry.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;

public final class IdleGeneratorsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // El modelo del generador tiene huecos de cristal (cutout); en Fabric la
        // capa de render se registra por código (en NeoForge la lee del model JSON).
        ModBlocks.GENERATORS.values().forEach(b ->
                BlockRenderLayerMap.INSTANCE.putBlock(b.get(), RenderType.cutout()));
    }
}
