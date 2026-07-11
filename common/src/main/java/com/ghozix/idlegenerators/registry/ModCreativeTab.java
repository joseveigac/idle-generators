package com.ghozix.idlegenerators.registry;

import com.ghozix.idlegenerators.IdleGenerators;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(IdleGenerators.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> TAB = CREATIVE_TABS.register("tab",
            () -> CreativeTabRegistry.create(
                    Component.translatable("itemGroup.idlegenerators.tab"),
                    () -> new ItemStack(ModBlocks.GENERATORS.get("iron").get())));

    private ModCreativeTab() {}

    public static void register() { CREATIVE_TABS.register(); }
}
