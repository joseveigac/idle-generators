package com.ghozix.idlegenerators.registry;

import com.ghozix.idlegenerators.IdleGenerators;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(IdleGenerators.MOD_ID, Registries.ITEM);

    static {
        // MC 26.x: Item.Properties requires setId(ResourceKey<Item>) — Architectury DeferredRegister
        // does not inject the key automatically (verified on Amethyst Resonance 26.2, 2026-07).
        ModBlocks.GENERATORS.forEach((key, block) -> {
            String itemId = key + "_generator";
            ITEMS.register(itemId, () ->
                new BlockItem(block.get(), new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM,
                        Identifier.fromNamespaceAndPath(IdleGenerators.MOD_ID, itemId)))
                    // 26.x: el BlockItem ya NO hereda el nombre del bloque; sin esto el
                    // description id sería item.idlegenerators.* (key cruda, sin lang).
                    .useBlockDescriptionPrefix()
                    .arch$tab(ModCreativeTab.TAB)));
        });
    }

    public static void register() { ITEMS.register(); }
}
