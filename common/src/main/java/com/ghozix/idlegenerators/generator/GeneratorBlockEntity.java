package com.ghozix.idlegenerators.generator;

import com.ghozix.idlegenerators.config.IGConfig;
import com.ghozix.idlegenerators.logic.GeneratorLogic;
import com.ghozix.idlegenerators.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class GeneratorBlockEntity extends BlockEntity implements WorldlyContainer {
    /** Buffer REAL de salida (v1.3.0): la producción vive en stacks, no en un contador virtual —
     *  requisito para tolvas/tubos sin bugs de transacción (skill capabilities.md, lazy-buffer trap). */
    private final NonNullList<ItemStack> slots;
    private final int[] allSlots;
    private long lastInteraction = System.currentTimeMillis();
    @Nullable private UUID ownerId;

    public GeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GENERATOR.get(), pos, state);
        GeneratorType type = ((GeneratorBlock) state.getBlock()).type();
        int perSlot = type.product().getDefaultMaxStackSize();
        int count = (type.cap() + perSlot - 1) / perSlot;
        this.slots = NonNullList.withSize(count, ItemStack.EMPTY);
        this.allSlots = new int[count];
        for (int i = 0; i < count; i++) allSlots[i] = i;
    }

    public GeneratorType type() { return ((GeneratorBlock) getBlockState().getBlock()).type(); }

    /** Intervalo efectivo (config aplicada). Público para el HUD/Jade (progreso de ciclo). */
    public long effectiveIntervalMs() {
        return GeneratorLogic.effectiveIntervalMs(type().baseIntervalMs(), IGConfig.get().productionSpeedMultiplier);
    }

    public boolean isEnabledByConfig() {
        GeneratorType type = type();
        return IGConfig.get().isGeneratorEnabled(type.key(), type.category());
    }

    public void initOnPlace(@Nullable UUID owner) {
        clearContent();
        this.lastInteraction = System.currentTimeMillis();
        this.ownerId = owner;
        setChanged();
    }

    /** Solo lectura, para HUD y Jade: no muta el estado. */
    public GeneratorLogic.Settle settleView(long nowMs) {
        return GeneratorLogic.settle(nowMs, lastInteraction, countItems(), type().cap(), effectiveIntervalMs());
    }

    /** Ticker de servidor (cada tick): ÚNICO punto que materializa producción, junto con las
     *  interacciones directas (collect/drop). Nunca producir desde los getters del Container:
     *  un tubo simulando (transacción que se aborta) se comería lo materializado a mitad de lectura. */
    public static void serverTick(Level level, BlockPos pos, BlockState state, GeneratorBlockEntity be) {
        be.produce(System.currentTimeMillis());
    }

    private void produce(long nowMs) {
        if (!isEnabledByConfig()) {
            // Desactivado: congela el progreso sin acumular ciclos (máx. un guardado por intervalo).
            if (nowMs - lastInteraction >= effectiveIntervalMs()) {
                lastInteraction = nowMs;
                setChanged();
            }
            return;
        }
        int total = countItems();
        var s = GeneratorLogic.settle(nowMs, lastInteraction, total, type().cap(), effectiveIntervalMs());
        int delta = s.produced() - total;
        if (delta <= 0 && s.settledLastInteraction() == lastInteraction) return;
        lastInteraction = s.settledLastInteraction();
        if (delta > 0) fillSlots(delta);
        setChanged();
    }

    private void fillSlots(int amount) {
        var product = type().product();
        int perSlot = product.getDefaultMaxStackSize();
        int[] counts = new int[slots.size()];
        for (int i = 0; i < slots.size(); i++) counts[i] = slots.get(i).getCount();
        int[] filled = GeneratorLogic.distribute(counts, perSlot, amount);
        for (int i = 0; i < slots.size(); i++) {
            if (filled[i] != counts[i]) slots.set(i, new ItemStack(product, filled[i]));
        }
    }

    private int countItems() {
        int total = 0;
        for (int i = 0; i < slots.size(); i++) total += slots.get(i).getCount();
        return total;
    }

    /** Retira hasta {@code requested} items al inventario del jugador (exceso al suelo). */
    public void collect(ServerPlayer player, int requested) {
        produce(System.currentTimeMillis()); // el click siempre ve la producción al milisegundo
        int remaining = Math.max(0, requested);
        boolean took = false;
        for (int i = 0; i < slots.size() && remaining > 0; i++) {
            ItemStack slot = slots.get(i);
            if (slot.isEmpty()) continue;
            ItemStack out = slot.split(Math.min(remaining, slot.getCount()));
            if (slot.isEmpty()) slots.set(i, ItemStack.EMPTY);
            remaining -= out.getCount();
            took = true;
            if (!player.getInventory().add(out)) {
                player.drop(out, false);
            }
        }
        if (took) setChanged();
    }

    /** Dropea el buffer al romper el bloque (si la config lo permite). */
    public void dropContents(Level level, BlockPos pos) {
        if (!IGConfig.get().dropContentsOnBreak) return;
        produce(System.currentTimeMillis()); // materializa lo pendiente antes de dropear
        Containers.dropContents(level, pos, this);
        clearContent();
    }

    // ── acceso para tests ──
    public int getStoredAmount() { return countItems(); }
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
        clearContent();
        ContainerHelper.loadAllItems(input, slots);
        int legacyStored = Math.max(0, input.getIntOr("StoredAmount", 0));
        if (countItems() == 0 && legacyStored > 0) {
            // Migración v1.2.0 → v1.3.0: el buffer virtual pasa a slots reales.
            fillSlots(Math.min(legacyStored, type().cap()));
        }
        lastInteraction = input.getLongOr("LastInteraction", System.currentTimeMillis());
        ownerId = input.getString("Owner").map(UUID::fromString).orElse(null);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, slots);
        // Total redundante para compatibilidad hacia atrás: un downgrade a v1.2.0 conserva el buffer.
        output.putInt("StoredAmount", countItems());
        output.putLong("LastInteraction", lastInteraction);
        if (ownerId != null) output.putString("Owner", ownerId.toString());
    }

    // ── WorldlyContainer: extracción por las 6 caras, inserción bloqueada ──

    @Override public int getContainerSize() { return slots.size(); }

    @Override public boolean isEmpty() {
        for (int i = 0; i < slots.size(); i++) {
            if (!slots.get(i).isEmpty()) return false;
        }
        return true;
    }

    @Override public ItemStack getItem(int slot) {
        return slot >= 0 && slot < slots.size() ? slots.get(slot) : ItemStack.EMPTY;
    }

    @Override public ItemStack removeItem(int slot, int amount) {
        ItemStack removed = ContainerHelper.removeItem(slots, slot, amount);
        if (!removed.isEmpty()) setChanged();
        return removed;
    }

    @Override public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(slots, slot);
    }

    @Override public void setItem(int slot, ItemStack stack) {
        if (slot < 0 || slot >= slots.size()) return;
        slots.set(slot, stack);
        setChanged();
    }

    @Override public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override public void clearContent() {
        for (int i = 0; i < slots.size(); i++) slots.set(i, ItemStack.EMPTY);
    }

    @Override public int[] getSlotsForFace(Direction side) { return allSlots; }

    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) { return false; }

    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) { return true; }

    // Guardia sin-cara: cubre wrappers y tubos que consultan sin dirección.
    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return false; }
}
