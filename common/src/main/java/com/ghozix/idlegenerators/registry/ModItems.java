package com.ghozix.idlegenerators.registry;

import com.ghozix.idlegenerators.IdleGenerators;
import com.ghozix.idlegenerators.generator.GeneratorBlockItem;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.LinkedHashMap;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(IdleGenerators.MOD_ID, Registries.ITEM);

    /** key ("iron") → item registrado. Lo consume el contenido de la pestaña (ModCreativeTab). */
    public static final LinkedHashMap<String, RegistrySupplier<Item>> BY_KEY = new LinkedHashMap<>();

    static {
        // MC 26.x: Item.Properties requires setId(ResourceKey<Item>) — Architectury DeferredRegister
        // does not inject the key automatically (verified on Amethyst Resonance 26.2, 2026-07).
        ModBlocks.GENERATORS.forEach((key, block) -> {
            String itemId = key + "_generator";
            // v1.3.0 R4: SIN arch$tab. Esa asignación es estática y no se puede filtrar por config;
            // la pestaña se puebla ahora con un callback en ModCreativeTab.
            BY_KEY.put(key, ITEMS.register(itemId, () ->
                new GeneratorBlockItem(block.get(), new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM,
                        Identifier.fromNamespaceAndPath(IdleGenerators.MOD_ID, itemId)))
                    // 26.x: el BlockItem ya NO hereda el nombre del bloque; sin esto el
                    // description id sería item.idlegenerators.* (key cruda, sin lang).
                    .useBlockDescriptionPrefix())));
        });
    }

    public static void register() { ITEMS.register(); }
}
