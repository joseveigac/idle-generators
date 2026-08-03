package com.ghozix.idlegenerators.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;

import com.ghozix.idlegenerators.IdleGenerators;
import com.ghozix.idlegenerators.registry.ModBlockEntities;

public final class IdleGeneratorsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        IdleGenerators.init();

        // v1.3.0 R4: condición de carga de recetas. Aquí, antes de que se carguen datapacks.
        ResourceConditions.register(GeneratorEnabledCondition.TYPE);

        // v1.3.0: expone el buffer a tubos Fabric (las tolvas vanilla ya ven el WorldlyContainer).
        // La inserción queda bloqueada por canPlaceItem/canPlaceItemThroughFace del BE.
        ItemStorage.SIDED.registerForBlockEntity(
                (be, direction) -> InventoryStorage.of(be, direction),
                ModBlockEntities.GENERATOR.get());
    }
}
