package com.ghozix.idlegenerators.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class IGDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(IGRecipeProvider::new);
        pack.addProvider(IGLootTableProvider::new);
        pack.addProvider(IGModelProvider::new);
    }
}
