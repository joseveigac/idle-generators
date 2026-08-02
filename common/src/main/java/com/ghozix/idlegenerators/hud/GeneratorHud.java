package com.ghozix.idlegenerators.hud;

import com.ghozix.idlegenerators.config.IGConfig;
import com.ghozix.idlegenerators.generator.GeneratorBlockEntity;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.platform.Platform;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public final class GeneratorHud {
    private GeneratorHud() {}

    // Jade ya muestra el estado del generador al mirarlo: con Jade instalado el actionbar
    // se auto-oculta salvo que el usuario fuerce showHudWithJade en el config.
    private static final boolean JADE_LOADED = Platform.isModLoaded("jade");

    public static void register() {
        TickEvent.PLAYER_POST.register(player -> {
            if (!(player instanceof ServerPlayer sp)) return;      // solo lado servidor
            // Cada tick (Bedrock usa 5 por coste de su script engine; aquí un raycast
            // por jugador y tick es despreciable y el % sube fluido).
            IGConfig cfg = IGConfig.get();
            if (!cfg.hudEnabled || (JADE_LOADED && !cfg.showHudWithJade)) return;

            HitResult hit = sp.pick(6.0D, 1.0F, false);            // raycast 6 bloques
            if (!(hit instanceof BlockHitResult bhr)) return;
            if (!(sp.level().getBlockEntity(bhr.getBlockPos()) instanceof GeneratorBlockEntity be)) return;

            long now = System.currentTimeMillis();
            var s = be.settleView(now);
            int cap = be.type().cap();
            if (!be.isEnabledByConfig()) {
                sp.displayClientMessage(Component.translatable("hud.idlegenerators.status_disabled",
                        Component.translatable(be.getBlockState().getBlock().getDescriptionId()),
                        s.produced(), cap), true);
                return;
            }
            // Paridad Bedrock: % = progreso hacia el SIGUIENTE item (sube en tiempo real),
            // equivalente a floor((elapsed % interval) / interval * 100) del addon original.
            long intervalMs = be.effectiveIntervalMs();
            int percent = (int) Math.min(99L, Math.max(0L, (now - s.settledLastInteraction()) * 100 / intervalMs));
            sp.displayClientMessage(Component.translatable("hud.idlegenerators.status",
                    Component.translatable(be.getBlockState().getBlock().getDescriptionId()),
                    s.produced(), cap, percent), true);
        });
    }
}
