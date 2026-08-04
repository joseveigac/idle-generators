package com.ghozix.idlegenerators.config;

import java.util.Set;

/**
 * Conjunto de generadores desactivados TAL COMO LO DICE EL SERVIDOR (llega por S2C al login y en
 * cada recarga de datapacks). Solo se rellena en el cliente; en un dedicado queda vacío y nadie lo
 * consulta.
 *
 * <p>NO lleva {@code @Environment}: la referencian clases comunes y Fabric borraría la clase del
 * jar de servidor. Si el paquete no llega nunca (servidor sin el mod, canal rechazado), el
 * conjunto queda vacío = todo visible, que es el comportamiento previo a v1.3.0.
 */
public final class ClientToggles {
    private static volatile Set<String> disabled = Set.of();

    private ClientToggles() {}

    public static void set(Set<String> keys) {
        disabled = Set.copyOf(keys);
    }

    /** Al desconectar: si no, el conjunto de un servidor contaminaría el siguiente mundo. */
    public static void clear() {
        disabled = Set.of();
    }

    public static boolean isDisabled(String key) {
        return disabled.contains(key);
    }
}
