package com.ghozix.idlegenerators.registry;

import com.ghozix.idlegenerators.IdleGenerators;
import com.ghozix.idlegenerators.config.ClientToggles;
import com.ghozix.idlegenerators.generator.GeneratorType;
import com.ghozix.idlegenerators.generator.GeneratorTypes;
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

    // v1.3.0 R4: el contenido se genera con un callback (displayItems) en vez de con arch$tab en
    // cada item, porque arch$tab es estático y no se puede filtrar por config.
    // Se usa displayItems y NO CreativeTabRegistry.modify a propósito: JEI, EMI y REI construyen su
    // lista de items desde las pestañas, y REI en concreto invoca el displayItemsGenerator de la
    // pestaña directamente (verificado en bytecode, spike S1 2026-08-04), así que este único punto
    // filtra la pestaña y los tres visores a la vez.
    public static final RegistrySupplier<CreativeModeTab> TAB = CREATIVE_TABS.register("tab",
            () -> CreativeTabRegistry.create(builder -> builder
                    .title(Component.translatable("itemGroup.idlegenerators.tab"))
                    .icon(() -> new ItemStack(ModBlocks.GENERATORS.get("iron").get()))
                    .displayItems((params, output) -> {
                        // Recorre ALL, no el mapa, para conservar el orden de siempre.
                        for (GeneratorType type : GeneratorTypes.ALL) {
                            if (ClientToggles.isDisabled(type.key())) continue;
                            output.accept(ModItems.BY_KEY.get(type.key()).get());
                        }
                    })));

    private ModCreativeTab() {}

    public static void register() { CREATIVE_TABS.register(); }
}
