package com.ghozix.idlegenerators.client;

import com.ghozix.idlegenerators.registry.ModCreativeTab;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;

/**
 * Reconstruye el contenido de nuestra pestaña creativa cuando cambia el conjunto de generadores
 * desactivados. Sin esto el filtrado no se vería hasta el siguiente relog.
 *
 * <p>OJO: {@code CreativeModeTabs.tryRebuildTabContents} NO sirve aquí. Su primera instrucción
 * comprueba {@code CACHED_PARAMETERS.needsUpdate(flags, permisos, registros)} y sale con
 * {@code false} sin reconstruir si nada de eso cambió — que es exactamente nuestro caso, porque lo
 * que cambia es la config del mod (verificado en bytecode de 26.2, 2026-08-04). Por eso se llama a
 * {@code buildContents} directamente sobre nuestra pestaña, que además es más barato: no toca las
 * pestañas de vanilla ni las de otros mods.
 */
@Environment(EnvType.CLIENT)
public final class CreativeTabRefresher {
    private CreativeTabRefresher() {}

    public static void run() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        CreativeModeTab.ItemDisplayParameters params = new CreativeModeTab.ItemDisplayParameters(
                mc.player.connection.enabledFeatures(),
                mc.options.operatorItemsTab().get(),
                mc.level.registryAccess());
        ModCreativeTab.TAB.get().buildContents(params);
        // La pestaña de BÚSQUEDA es otra pestaña, que agrega el contenido de las de tipo CATEGORY
        // ya construidas: vanilla lo hace en dos pasadas dentro de buildAllTabContents (verificado
        // en bytecode 26.2). Reconstruir solo la nuestra la dejaba con la lista vieja, así que el
        // item seguía apareciendo en la búsqueda del creativo y en la lista de JEI/REI, que leen
        // getSearchTabDisplayItems(). El orden importa: primero la nuestra, luego la de búsqueda.
        CreativeModeTabs.searchTab().buildContents(params);
    }
}
