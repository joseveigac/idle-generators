package com.ghozix.idlegenerators.registry;

import com.ghozix.idlegenerators.IdleGenerators;
import com.ghozix.idlegenerators.generator.GeneratorBlockItem;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import java.util.LinkedHashMap;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(IdleGenerators.MOD_ID, Registries.ITEM);

    /** key ("iron") → item registrado. Lo consume el contenido de la pestaña (ModCreativeTab). */
    public static final LinkedHashMap<String, RegistrySupplier<Item>> BY_KEY = new LinkedHashMap<>();

    static {
        // v1.3.0 R4: SIN arch$tab. Esa asignación es estática y no se puede filtrar por config;
        // la pestaña se puebla ahora con un callback en ModCreativeTab.
        ModBlocks.GENERATORS.forEach((key, block) ->
            BY_KEY.put(key, ITEMS.register(key + "_generator", () ->
                new GeneratorBlockItem(block.get(), new Item.Properties()))));
    }

    public static void register() { ITEMS.register(); }
}
