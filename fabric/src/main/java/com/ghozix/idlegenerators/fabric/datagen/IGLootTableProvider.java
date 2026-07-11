package com.ghozix.idlegenerators.fabric.datagen;

import com.ghozix.idlegenerators.registry.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

// MC 26.x: FabricDataOutput → FabricPackOutput
public class IGLootTableProvider extends FabricBlockLootSubProvider {
    public IGLootTableProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void generate() {
        ModBlocks.GENERATORS.values().forEach(b -> dropSelf(b.get()));
    }
}
