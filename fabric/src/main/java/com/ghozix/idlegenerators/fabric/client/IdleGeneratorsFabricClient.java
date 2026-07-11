package com.ghozix.idlegenerators.fabric.client;

import com.ghozix.idlegenerators.client.IGClient;
import net.fabricmc.api.ClientModInitializer;

public final class IdleGeneratorsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // MC 26.x: render_type is declared in model JSON ("render_type": "minecraft:cutout");
        // BlockRenderLayerMap no longer exists / is not needed.
        IGClient.init();
    }
}
