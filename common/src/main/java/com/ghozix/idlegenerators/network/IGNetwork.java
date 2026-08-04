package com.ghozix.idlegenerators.network;

import com.ghozix.idlegenerators.IdleGenerators;
import com.ghozix.idlegenerators.client.CreativeTabRefresher;
import com.ghozix.idlegenerators.config.ClientToggles;
import com.ghozix.idlegenerators.config.IGConfig;
import com.ghozix.idlegenerators.generator.GeneratorType;
import com.ghozix.idlegenerators.generator.GeneratorTypes;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Única red del mod (v1.3.0 R4): el servidor le dice al cliente qué generadores están desactivados,
 * para que la pestaña creativa y el tooltip reflejen la config DEL SERVIDOR y no la del jugador.
 * La producción, el HUD y Jade no pasan por aquí: ya leen {@link IGConfig} en el servidor.
 */
public final class IGNetwork {
    private IGNetwork() {}

    public record S2CDisabledGenerators(Set<String> keys) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<S2CDisabledGenerators> TYPE =
                new CustomPacketPayload.Type<>(
                        ResourceLocation.fromNamespaceAndPath(IdleGenerators.MOD_ID, "disabled_generators"));

        public static final StreamCodec<RegistryFriendlyByteBuf, S2CDisabledGenerators> CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.collection(HashSet::new, ByteBufCodecs.STRING_UTF8),
                        S2CDisabledGenerators::keys,
                        S2CDisabledGenerators::new);

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /** Se llama desde IdleGenerators.init(), en ambos lados. */
    public static void register() {
        // Divergencia 1.21.1: en Architectury 13.x el adaptor de servidor de Fabric NO implementa
        // registerS2C — registrar un receiver S2C en un entorno servidor lanza AbstractMethodError
        // (visto en runDatagen, 2026-08-04; en 26.x/Arch 21 la misma llamada vale en ambos lados).
        // Receiver solo en el cliente físico; el dedicado registra solo el TIPO para poder enviar.
        EnvExecutor.runInEnv(Env.CLIENT, () -> IGNetwork::registerClientReceiver);
        EnvExecutor.runInEnv(Env.SERVER, () -> () -> NetworkManager.registerS2CPayloadType(
                S2CDisabledGenerators.TYPE, S2CDisabledGenerators.CODEC));

        PlayerEvent.PLAYER_JOIN.register(IGNetwork::sendTo);
    }

    private static void registerClientReceiver() {
        // El handler NO puede tocar clases de cliente directamente (esta clase también existe en el
        // dedicado): CreativeTabRefresher va detrás de EnvExecutor y solo se carga al correr el
        // lambda, cosa que aquí ya es siempre en cliente.
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                S2CDisabledGenerators.TYPE, S2CDisabledGenerators.CODEC,
                (payload, context) -> context.queue(() -> {
                    ClientToggles.set(payload.keys());
                    EnvExecutor.runInEnv(Env.CLIENT, () -> CreativeTabRefresher::run);
                }));
    }

    private static Set<String> disabledKeys() {
        IGConfig cfg = IGConfig.get();
        return GeneratorTypes.ALL.stream()
                .filter(t -> !cfg.isGeneratorEnabled(t.key(), t.category()))
                .map(GeneratorType::key)
                .collect(Collectors.toUnmodifiableSet());
    }

    private static void sendTo(ServerPlayer player) {
        // Cliente sin el mod o que rechaza el canal: no enviar (lanzaría al intentarlo).
        if (!NetworkManager.canPlayerReceive(player, S2CDisabledGenerators.TYPE)) return;
        NetworkManager.sendToPlayer(player, new S2CDisabledGenerators(disabledKeys()));
    }

    public static void sendToAll(MinecraftServer server) {
        if (server == null) return;
        Set<String> keys = disabledKeys();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (NetworkManager.canPlayerReceive(player, S2CDisabledGenerators.TYPE)) {
                NetworkManager.sendToPlayer(player, new S2CDisabledGenerators(keys));
            }
        }
    }
}
