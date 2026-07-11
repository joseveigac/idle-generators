package com.ghozix.idlegenerators.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class IGDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(IGRecipeProvider::new);
        pack.addProvider(IGLootTableProvider::new);
        // IGModelProvider is excluded: FabricModelProvider extends a CLIENT-only class
        // (net.minecraft.client.data.models.ModelProvider) and cannot be loaded in server-mode datagen.
        // Block/item model JSONs are written as static files in common/src/main/generated/ (blockstates,
        // models/block/, models/item/) and items/ dispatch files in common/src/main/resources/assets/.
    }
}
