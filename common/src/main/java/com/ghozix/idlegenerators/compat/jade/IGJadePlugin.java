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
                long now = System.currentTimeMillis();
                var s = be.settleView(now);
                data.putInt("igProduced", s.produced());
                data.putInt("igCap", be.type().cap());
                // Mismo % que la action bar: progreso hacia el siguiente item.
                data.putInt("igPercent", (int) Math.min(99L,
                        Math.max(0L, (now - s.settledLastInteraction()) * 100 / be.effectiveIntervalMs())));
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
                tooltip.add(Component.translatable("hud.idlegenerators.status_short",
                        data.getIntOr("igProduced", 0), data.getIntOr("igCap", 0), data.getIntOr("igPercent", 0)));
            }
        }
    }
}
