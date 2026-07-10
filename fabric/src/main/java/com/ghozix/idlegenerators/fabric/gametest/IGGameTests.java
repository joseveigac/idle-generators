package com.ghozix.idlegenerators.fabric.gametest;

import com.ghozix.idlegenerators.generator.GeneratorBlockEntity;
import com.ghozix.idlegenerators.registry.ModBlocks;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;

public class IGGameTests implements FabricGameTest {
    private static final BlockPos POS = new BlockPos(1, 1, 1);
    private static final long TEN_CYCLES_IRON = 10 * 30_000L;

    private GeneratorBlockEntity placeIron(GameTestHelper helper) {
        helper.setBlock(POS, ModBlocks.GENERATORS.get("iron").get());
        GeneratorBlockEntity be = (GeneratorBlockEntity) helper.getBlockEntity(POS);
        be.setLastInteraction(System.currentTimeMillis() - TEN_CYCLES_IRON);
        return be;
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public void rightClickCollectsOne(GameTestHelper helper) {
        placeIron(helper);
        var player = helper.makeMockServerPlayerInLevel();
        player.setGameMode(GameType.SURVIVAL);
        helper.useBlock(POS, player);
        helper.runAfterDelay(2, () -> {
            if (player.getInventory().countItem(Items.RAW_IRON) != 1) {
                helper.fail("expected exactly 1 raw iron, got " + player.getInventory().countItem(Items.RAW_IRON));
            }
            GeneratorBlockEntity be = (GeneratorBlockEntity) helper.getBlockEntity(POS);
            if (be.getStoredAmount() != 9) helper.fail("expected 9 buffered, got " + be.getStoredAmount());
            helper.succeed();
        });
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public void sneakRightClickCollectsUpToStack(GameTestHelper helper) {
        placeIron(helper);
        var player = helper.makeMockServerPlayerInLevel();
        player.setGameMode(GameType.SURVIVAL);
        player.setShiftKeyDown(true);
        helper.useBlock(POS, player);
        helper.runAfterDelay(2, () -> {
            if (player.getInventory().countItem(Items.RAW_IRON) != 10) {
                helper.fail("expected all 10 produced, got " + player.getInventory().countItem(Items.RAW_IRON));
            }
            helper.succeed();
        });
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public void breakingDropsBlockAndBuffer(GameTestHelper helper) {
        GeneratorBlockEntity be = placeIron(helper);
        helper.runAfterDelay(2, () -> {
            helper.destroyBlock(POS);
            helper.runAfterDelay(5, () -> {
                helper.assertItemEntityPresent(Items.RAW_IRON, POS, 2.0);
                helper.succeed();
            });
        });
    }
}
