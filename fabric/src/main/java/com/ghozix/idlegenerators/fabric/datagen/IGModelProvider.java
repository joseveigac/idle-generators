package com.ghozix.idlegenerators.fabric.datagen;

import com.ghozix.idlegenerators.IdleGenerators;
import com.ghozix.idlegenerators.registry.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.Set;

public class IGModelProvider extends FabricModelProvider {
    private static final TextureSlot GEN = TextureSlot.create("gen");
    // Blockstate → solo el marco (el núcleo lo dibuja GeneratorCoreRenderer animado).
    private static final ModelTemplate FRAME_TEMPLATE = new ModelTemplate(
            Optional.of(ResourceLocation.fromNamespaceAndPath(IdleGenerators.MOD_ID, "block/generator_frame")),
            Optional.of("_frame"), GEN);
    // Item → modelo completo (marco + núcleo estático) para inventario/mano.
    private static final ModelTemplate FULL_TEMPLATE = new ModelTemplate(
            Optional.of(ResourceLocation.fromNamespaceAndPath(IdleGenerators.MOD_ID, "block/generator_base")),
            Optional.empty(), GEN);
    // Generators whose item shows a 3D model of its product (Bedrock generator_item_<key>.geo.json).
    private static final Set<String> CORE_3D = Set.of("nether_wart", "wither_rose", "nether_star");

    public IGModelProvider(FabricDataOutput output) { super(output); }

    @Override
    public void generateBlockStateModels(BlockModelGenerators gen) {
        ModBlocks.GENERATORS.values().forEach(b -> {
            Block block = b.get();
            TextureMapping mapping = new TextureMapping().put(GEN, TextureMapping.getBlockTexture(block));
            ResourceLocation frame = FRAME_TEMPLATE.create(block, mapping, gen.modelOutput);
            String key = BuiltInRegistries.BLOCK.getKey(block).getPath().replace("_generator", "");
            ModelTemplate fullTemplate = CORE_3D.contains(key)
                    ? new ModelTemplate(Optional.of(ResourceLocation.fromNamespaceAndPath(IdleGenerators.MOD_ID,
                            "block/generator_base_" + key)), Optional.empty(), GEN)
                    : FULL_TEMPLATE;
            ResourceLocation full = fullTemplate.create(block, mapping, gen.modelOutput);
            gen.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, frame));
            gen.delegateItemModel(block, full);
        });
    }

    @Override
    public void generateItemModels(ItemModelGenerators gen) {}
}
