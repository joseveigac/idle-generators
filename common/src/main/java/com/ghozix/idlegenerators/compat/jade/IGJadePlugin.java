package com.ghozix.idlegenerators.compat.jade;

import com.ghozix.idlegenerators.IdleGenerators;
import com.ghozix.idlegenerators.generator.GeneratorBlock;
import com.ghozix.idlegenerators.generator.GeneratorBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

/**
 * Integración OPCIONAL con Jade: esta clase solo la carga Jade cuando está
 * instalado (entrypoint "jade" en Fabric; escaneo de @WailaPlugin en NeoForge).
 * Nada del mod referencia esta clase.
 */
@WailaPlugin
public class IGJadePlugin implements IWailaPlugin {
    private static final Identifier UID =
            Identifier.fromNamespaceAndPath(IdleGenerators.MOD_ID, "generator");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(new GeneratorDataProvider(), GeneratorBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(new GeneratorComponentProvider(), GeneratorBlock.class);
    }

    private static final class GeneratorDataProvider implements IServerDataProvider<BlockAccessor> {
        @Override
        public Identifier getUid() { return UID; }

        @Override
        public void appendServerData(CompoundTag data, BlockAccessor accessor) {
            if (accessor.getBlockEntity() instanceof GeneratorBlockEntity be) {
                var s = be.settleView(System.currentTimeMillis());
                data.putInt("igProduced", s.produced());
                data.putInt("igCap", be.type().cap());
                // El cliente extrapola en vivo desde estos dos valores (Jade solo
                // refresca los server data cada varios ticks y el % se vería a saltos).
                data.putLong("igLast", s.settledLastInteraction());
                data.putLong("igInterval", be.effectiveIntervalMs());
            }
        }
    }

    private static final class GeneratorComponentProvider implements IBlockComponentProvider {
        @Override
        public Identifier getUid() { return UID; }

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            CompoundTag data = accessor.getServerData();
            if (data.contains("igProduced")) {
                int produced = data.getIntOr("igProduced", 0);
                int cap = data.getIntOr("igCap", 0);
                long last = data.getLongOr("igLast", 0L);
                long interval = Math.max(1L, data.getLongOr("igInterval", 1L));
                // Extrapolación en vivo (appendTooltip corre cada frame): en singleplayer
                // los relojes cliente/servidor son el mismo; con skew (multiplayer) se
                // clampa y en el peor caso el % queda estático hasta el siguiente sync.
                long elapsed = System.currentTimeMillis() - last;
                int percent = 0;
                if (elapsed >= 0 && cap > 0) {
                    produced = (int) Math.min((long) produced + elapsed / interval, cap);
                    percent = (int) Math.min(99L, (elapsed % interval) * 100 / interval);
                }
                tooltip.add(Component.translatable("hud.idlegenerators.status_short",
                        produced, cap, percent));
            }
        }
    }
}
