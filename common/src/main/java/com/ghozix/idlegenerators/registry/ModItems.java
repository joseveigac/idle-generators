package com.ghozix.idlegenerators.registry;

import com.ghozix.idlegenerators.IdleGenerators;
import com.ghozix.idlegenerators.generator.GeneratorBlockItem;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(IdleGenerators.MOD_ID, Registries.ITEM);

    static {
        ModBlocks.GENERATORS.forEach((key, block) ->
            ITEMS.register(key + "_generator", () ->
                new GeneratorBlockItem(block.get(), new Item.Properties().arch$tab(ModCreativeTab.TAB))));
    }

    public static void register() { ITEMS.register(); }
}
