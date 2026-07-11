package com.ghozix.idlegenerators.fabric.datagen;

import com.ghozix.idlegenerators.IdleGenerators;
import com.ghozix.idlegenerators.registry.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class IGModelProvider extends FabricModelProvider {
    private static final TextureSlot GEN = TextureSlot.create("gen");
    private static final ModelTemplate GENERATOR_TEMPLATE = new ModelTemplate(
            Optional.of(ResourceLocation.fromNamespaceAndPath(IdleGenerators.MOD_ID, "block/generator_base")),
            Optional.empty(), GEN);

    public IGModelProvider(FabricDataOutput output) { super(output); }

    @Override
    public void generateBlockStateModels(BlockModelGenerators gen) {
        ModBlocks.GENERATORS.values().forEach(b -> {
            Block block = b.get();
            TextureMapping mapping = new TextureMapping().put(GEN, TextureMapping.getBlockTexture(block));
            ResourceLocation model = GENERATOR_TEMPLATE.create(block, mapping, gen.modelOutput);
            gen.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, model));
            gen.delegateItemModel(block, model);
        });
    }

    @Override
    public void generateItemModels(ItemModelGenerators gen) {}
}
