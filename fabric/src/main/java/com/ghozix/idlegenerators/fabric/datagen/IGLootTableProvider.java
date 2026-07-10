package com.ghozix.idlegenerators.fabric.datagen;

import com.ghozix.idlegenerators.registry.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class IGLootTableProvider extends FabricBlockLootTableProvider {
    public IGLootTableProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void generate() {
        ModBlocks.GENERATORS.values().forEach(b -> dropSelf(b.get()));
    }
}
