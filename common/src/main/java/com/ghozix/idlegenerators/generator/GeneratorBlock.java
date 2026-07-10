package com.ghozix.idlegenerators.generator;

import com.ghozix.idlegenerators.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class GeneratorBlock extends Block implements EntityBlock {
    private final GeneratorType type;

    public GeneratorBlock(GeneratorType type, Properties properties) {
        super(properties);
        this.type = type;
    }

    public GeneratorType type() { return type; }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GeneratorBlockEntity(pos, state);
    }
}
