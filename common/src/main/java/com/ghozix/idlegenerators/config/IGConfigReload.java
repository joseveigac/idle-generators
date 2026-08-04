package com.ghozix.idlegenerators.config;

import com.ghozix.idlegenerators.IdleGenerators;
import com.ghozix.idlegenerators.network.IGNetwork;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.utils.GameInstance;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Recarga en caliente (v1.3.0 R4). Sin esto, {@code /reload} recargaría los datapacks pero las
 * condiciones se evaluarían contra la config que ya está en memoria (AutoConfig no relee el JSON
 * por su cuenta), así que un admin tendría que reiniciar el servidor entero para cambiar un toggle.
 *
 * <p>El flujo del admin pasa a ser: editar {@code config/idlegenerators.json} + {@code /reload}.
 *
 * <p>Reparto de responsabilidades:
 * <ul>
 *   <li><b>Releer el JSON</b> → {@link #reloadConfigFromDisk()}, llamado desde un hook NATIVO de
 *       cada loader que corre antes del pipeline de recarga (ver nota de abajo).</li>
 *   <li><b>Reenviar el conjunto a los clientes</b> → el listener de aquí, registrado por
 *       Architectury, que corre al final de la recarga.</li>
 * </ul>
 */
public final class IGConfigReload {
    private IGConfigReload() {}

    /**
     * Relee {@code config/idlegenerators.json} del disco.
     *
     * <p>Lo llaman los hooks nativos de cada loader (Fabric {@code START_DATA_PACK_RELOAD},
     * NeoForge {@code AddServerReloadListenersEvent}), no el listener de abajo.
     *
     * <p>⚠ El sitio "natural" sería {@code PreparableReloadListener.prepareSharedState}, que vanilla
     * invoca sobre todos los listeners antes de arrancar ninguna tarea de recarga. NO funciona a
     * través de Architectury: su wrapper de Fabric ({@code ReloadListenerRegistryImpl$1}) solo
     * reimplementa {@code getName()} y {@code reload(...)}, y como {@code prepareSharedState} es un
     * método {@code default} se queda en el no-op y la config nunca se relee (verificado en
     * bytecode y reproducido con un {@code /reload} real por RCON, 2026-08-04: seguía cargando 1617
     * recetas en vez de 1616).
     */
    public static void reloadConfigFromDisk() {
        AutoConfig.getConfigHolder(IGConfig.class).load();
    }

    public static void register() {
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new ResyncListener(),
                Identifier.fromNamespaceAndPath(IdleGenerators.MOD_ID, "config"));

        // Guardar la pantalla de Cloth (solo pasa en local/LAN) también reenvía: si no, cambiar un
        // toggle desde la GUI no se vería en la pestaña hasta el siguiente /reload o relog.
        AutoConfig.getConfigHolder(IGConfig.class).registerSaveListener((holder, cfg) -> {
            IGNetwork.sendToAll(GameInstance.getServer());
            return InteractionResult.PASS;
        });
    }

    /** Reenvía el conjunto desactivado cuando la recarga de datapacks ya ha terminado. */
    private static final class ResyncListener implements PreparableReloadListener {
        @Override
        public String getName() {
            return IdleGenerators.MOD_ID + ":config";
        }

        @Override
        public CompletableFuture<Void> reload(SharedState state, Executor backgroundExecutor,
                                              PreparationBarrier barrier, Executor gameExecutor) {
            return barrier.wait(Unit.INSTANCE)
                    .thenRunAsync(() -> IGNetwork.sendToAll(GameInstance.getServer()), gameExecutor);
        }
    }
}
