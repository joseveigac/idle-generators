package com.ghozix.idlegenerators.generator;

import com.ghozix.idlegenerators.config.IGConfig;
import com.ghozix.idlegenerators.logic.GeneratorLogic;
import com.ghozix.idlegenerators.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class GeneratorBlockEntity extends BlockEntity {
    private int storedAmount = 0;
    private long lastInteraction = System.currentTimeMillis();
    @Nullable private UUID ownerId;

    public GeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GENERATOR.get(), pos, state);
    }

    public GeneratorType type() { return ((GeneratorBlock) getBlockState().getBlock()).type(); }

    private long intervalMs() {
        return GeneratorLogic.effectiveIntervalMs(type().baseIntervalMs(), IGConfig.get().productionSpeedMultiplier);
    }

    public void initOnPlace(@Nullable UUID owner) {
        this.storedAmount = 0;
        this.lastInteraction = System.currentTimeMillis();
        this.ownerId = owner;
        setChanged();
    }

    /** Solo lectura, para HUD y Jade: no muta el estado. */
    public GeneratorLogic.Settle settleView(long nowMs) {
        return GeneratorLogic.settle(nowMs, lastInteraction, storedAmount, type().cap(), intervalMs());
    }

    /** Retira hasta {@code requested} items al inventario del jugador (exceso al suelo). */
    public void collect(Player player, int requested) {
        var w = GeneratorLogic.withdraw(System.currentTimeMillis(), lastInteraction,
                storedAmount, type().cap(), intervalMs(), requested);
        storedAmount = w.newStored();
        lastInteraction = w.newLastInteraction();
        setChanged();
        int remaining = w.collected();
        while (remaining > 0) {
            ItemStack stack = new ItemStack(type().product(), Math.min(remaining, type().product().getDefaultMaxStackSize()));
            remaining -= stack.getCount();
            if (!player.getInventory().add(stack)) {
                player.drop(stack, false);
            }
        }
    }

    /** Dropea el buffer al romper el bloque (si la config lo permite). */
    public void dropContents(Level level, BlockPos pos) {
        if (!IGConfig.get().dropContentsOnBreak) return;
        var s = settleView(System.currentTimeMillis());
        int remaining = s.produced();
        while (remaining > 0) {
            ItemStack stack = new ItemStack(type().product(), Math.min(remaining, type().product().getDefaultMaxStackSize()));
            remaining -= stack.getCount();
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
        }
        storedAmount = 0;
    }

    // ── acceso para tests ──
    public int getStoredAmount() { return storedAmount; }
    public void setLastInteraction(long epochMs) { this.lastInteraction = epochMs; setChanged(); }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        storedAmount = Math.max(0, tag.getInt("StoredAmount"));
        lastInteraction = tag.contains("LastInteraction") ? tag.getLong("LastInteraction") : System.currentTimeMillis();
        ownerId = tag.hasUUID("Owner") ? tag.getUUID("Owner") : null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("StoredAmount", storedAmount);
        tag.putLong("LastInteraction", lastInteraction);
        if (ownerId != null) tag.putUUID("Owner", ownerId);
    }
}
