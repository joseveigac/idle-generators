package com.ghozix.idlegenerators.registry;

import com.ghozix.idlegenerators.IdleGenerators;
import com.ghozix.idlegenerators.generator.GeneratorBlockEntity;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;
import java.util.stream.Collectors;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(IdleGenerators.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<GeneratorBlockEntity>> GENERATOR =
            BLOCK_ENTITIES.register("generator", () -> new BlockEntityType<>(
                    GeneratorBlockEntity::new,
                    ModBlocks.GENERATORS.values().stream().map(RegistrySupplier::get).collect(Collectors.toSet())
            ));

    public static void register() { BLOCK_ENTITIES.register(); }
}
