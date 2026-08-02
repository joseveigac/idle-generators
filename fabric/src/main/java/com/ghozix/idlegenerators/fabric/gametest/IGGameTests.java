package com.ghozix.idlegenerators.fabric.gametest;

import com.ghozix.idlegenerators.config.IGConfig;
import com.ghozix.idlegenerators.generator.GeneratorBlockEntity;
import com.ghozix.idlegenerators.registry.ModBlocks;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

public class IGGameTests implements FabricGameTest {
    private static final BlockPos POS = new BlockPos(1, 1, 1);
    private static final long TEN_CYCLES_IRON = 10 * 30_000L;

    private GeneratorBlockEntity place(GameTestHelper helper, String key, BlockPos pos, long msInPast) {
        helper.setBlock(pos, ModBlocks.GENERATORS.get(key).get());
        GeneratorBlockEntity be = (GeneratorBlockEntity) helper.getBlockEntity(pos);
        be.setLastInteraction(System.currentTimeMillis() - msInPast);
        return be;
    }

    private GeneratorBlockEntity placeIron(GameTestHelper helper) {
        return place(helper, "iron", POS, TEN_CYCLES_IRON);
    }

    private static int countInContainer(Container container, Item item) {
        int n = 0;
        for (int i = 0; i < container.getContainerSize(); i++) {
            if (container.getItem(i).is(item)) n += container.getItem(i).getCount();
        }
        return n;
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

    // ── v1.3.0: automatización ──

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE, timeoutTicks = 100)
    public void hopperBelowDrainsIntoChest(GameTestHelper helper) {
        BlockPos chest = new BlockPos(1, 1, 1);
        BlockPos hopper = new BlockPos(1, 2, 1);
        BlockPos gen = new BlockPos(1, 3, 1);
        helper.setBlock(chest, Blocks.CHEST);
        helper.setBlock(hopper, Blocks.HOPPER); // facing down por defecto: chupa del generador, empuja al cofre
        place(helper, "iron", gen, TEN_CYCLES_IRON);
        // Tolva vanilla: 1 item cada 8 ticks → en 60 ticks deben haber cruzado ≥3.
        helper.runAfterDelay(60, () -> {
            Container chestBE = (ChestBlockEntity) helper.getBlockEntity(chest);
            int moved = countInContainer(chestBE, Items.RAW_IRON);
            if (moved < 3) helper.fail("expected >=3 raw iron in chest, got " + moved);
            helper.succeed();
        });
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public void disabledGeneratorDoesNotProduceButEnabledDoes(GameTestHelper helper) {
        // OJO: los gametests comparten servidor Y CONFIG en paralelo — este test desactiva
        // "diamond" porque ningún otro test lo usa (con "iron" congelaba a los demás).
        BlockPos diamondPos = new BlockPos(1, 1, 1);
        BlockPos coalPos = new BlockPos(3, 1, 1);
        GeneratorBlockEntity diamond = place(helper, "diamond", diamondPos, 10 * 80_000L);
        GeneratorBlockEntity coal = place(helper, "coal", coalPos, 10 * 20_000L);
        IGConfig.get().disabledGenerators.add("diamond");
        helper.runAfterDelay(10, () -> {
            int diamondStored = diamond.getStoredAmount();
            int coalStored = coal.getStoredAmount();
            IGConfig.get().disabledGenerators.remove("diamond");
            if (coalStored == 0) helper.fail("enabled coal generator produced nothing (ticker missing?)");
            if (diamondStored != 0) helper.fail("disabled diamond generator produced " + diamondStored);
            // Reactivado: el progreso quedó a cero, no hay regalo retroactivo.
            helper.runAfterDelay(5, () -> {
                if (diamond.getStoredAmount() != 0) {
                    helper.fail("re-enabling backfilled " + diamond.getStoredAmount() + " items");
                }
                helper.succeed();
            });
        });
    }

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    public void transferApiExtractsButCannotInsert(GameTestHelper helper) {
        place(helper, "iron", POS, TEN_CYCLES_IRON);
        helper.runAfterDelay(2, () -> { // un tick del ticker materializa la producción
            var storage = ItemStorage.SIDED.find(helper.getLevel(), helper.absolutePos(POS), Direction.NORTH);
            if (storage == null) {
                helper.fail("no ItemStorage exposed");
                return;
            }
            try (Transaction tx = Transaction.openOuter()) {
                long got = storage.extract(ItemVariant.of(Items.RAW_IRON), 5, tx);
                tx.commit();
                if (got != 5) helper.fail("expected to extract 5, got " + got);
            }
            try (Transaction tx = Transaction.openOuter()) {
                long in = storage.insert(ItemVariant.of(Items.DIRT), 1, tx);
                if (in != 0) helper.fail("insertion should be blocked, accepted " + in);
                // sin commit: no-op
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
