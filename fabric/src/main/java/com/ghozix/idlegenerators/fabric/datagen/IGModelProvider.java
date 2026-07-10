package com.ghozix.idlegenerators.fabric.datagen;

import com.ghozix.idlegenerators.registry.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelLocationUtils;

public class IGModelProvider extends FabricModelProvider {
    public IGModelProvider(FabricDataOutput output) { super(output); }

    @Override
    public void generateBlockStateModels(BlockModelGenerators gen) {
        ModBlocks.GENERATORS.values().forEach(b -> {
            gen.createTrivialCube(b.get());
            gen.delegateItemModel(b.get(), ModelLocationUtils.getModelLocation(b.get()));
        });
    }

    @Override
    public void generateItemModels(ItemModelGenerators gen) {}
}
