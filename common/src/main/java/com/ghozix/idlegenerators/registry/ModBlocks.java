package com.ghozix.idlegenerators.registry;

import com.ghozix.idlegenerators.IdleGenerators;
import com.ghozix.idlegenerators.generator.GeneratorBlock;
import com.ghozix.idlegenerators.generator.GeneratorType;
import com.ghozix.idlegenerators.generator.GeneratorTypes;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

import java.util.LinkedHashMap;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(IdleGenerators.MOD_ID, Registries.BLOCK);

    /** key ("iron") → bloque registrado ("iron_generator"). Orden de GeneratorTypes.ALL. */
    public static final LinkedHashMap<String, RegistrySupplier<Block>> GENERATORS = new LinkedHashMap<>();

    static {
        for (GeneratorType type : GeneratorTypes.ALL) {
            GENERATORS.put(type.key(), BLOCKS.register(type.blockId(), () ->
                new GeneratorBlock(type, BlockBehaviour.Properties.of()
                        .setId(ResourceKey.create(Registries.BLOCK,
                                Identifier.fromNamespaceAndPath(IdleGenerators.MOD_ID, type.blockId())))
                        .strength(1.0F, 1200.0F)          // 1s de minado; a prueba de explosiones
                        .pushReaction(PushReaction.BLOCK) // no empujable por pistones
                        .sound(SoundType.STONE)
                        .noOcclusion())));                // el modelo tiene huecos de cristal (cutout)
        }
    }

    public static void register() { BLOCKS.register(); }
}
