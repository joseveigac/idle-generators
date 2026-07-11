package com.ghozix.idlegenerators.hud;

import com.ghozix.idlegenerators.config.IGConfig;
import com.ghozix.idlegenerators.generator.GeneratorBlockEntity;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public final class GeneratorHud {
    private GeneratorHud() {}

    public static void register() {
        TickEvent.PLAYER_POST.register(player -> {
            if (!(player instanceof ServerPlayer sp)) return;      // solo lado servidor
            if (sp.tickCount % 5 != 0) return;                     // cada 0.25 s, como Bedrock
            if (!IGConfig.get().hudEnabled) return;

            HitResult hit = sp.pick(6.0D, 1.0F, false);            // raycast 6 bloques
            if (!(hit instanceof BlockHitResult bhr)) return;
            if (!(sp.level().getBlockEntity(bhr.getBlockPos()) instanceof GeneratorBlockEntity be)) return;

            long now = System.currentTimeMillis();
            var s = be.settleView(now);
            int cap = be.type().cap();
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
