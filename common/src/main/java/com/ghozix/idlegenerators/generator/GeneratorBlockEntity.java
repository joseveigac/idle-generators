package com.ghozix.idlegenerators.generator;

import com.ghozix.idlegenerators.config.IGConfig;
import com.ghozix.idlegenerators.logic.GeneratorLogic;
import com.ghozix.idlegenerators.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
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

    /** Intervalo efectivo (config aplicada). Público para el HUD/Jade (progreso de ciclo). */
    public long effectiveIntervalMs() {
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
        return GeneratorLogic.settle(nowMs, lastInteraction, storedAmount, type().cap(), effectiveIntervalMs());
    }

    /** Retira hasta {@code requested} items al inventario del jugador (exceso al suelo). */
    public void collect(ServerPlayer player, int requested) {
        var w = GeneratorLogic.withdraw(System.currentTimeMillis(), lastInteraction,
                storedAmount, type().cap(), effectiveIntervalMs(), requested);
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
        setChanged();
    }

    // ── acceso para tests ──
    public int getStoredAmount() { return storedAmount; }
    public void setLastInteraction(long epochMs) { this.lastInteraction = epochMs; setChanged(); }

    /** Drops buffered items when the block is removed (called by LevelChunk before the BE is unregistered). */
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState blockState) {
        Level level = getLevel();
        if (level != null && !level.isClientSide()) {
            dropContents(level, pos);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        storedAmount = Math.max(0, input.getIntOr("StoredAmount", 0));
        lastInteraction = input.getLongOr("LastInteraction", System.currentTimeMillis());
        ownerId = input.getString("Owner").map(UUID::fromString).orElse(null);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("StoredAmount", storedAmount);
        output.putLong("LastInteraction", lastInteraction);
        if (ownerId != null) output.putString("Owner", ownerId.toString());
    }
}
