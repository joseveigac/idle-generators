package com.ghozix.idlegenerators.generator;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** A diferencia del resto de tests, este toca GeneratorTypes.ALL, que referencia Items → hay
 *  que arrancar los registros de vanilla o el <clinit> revienta con "Not bootstrapped". */
class GeneratorTypesTest {
    @BeforeAll static void bootstrapMinecraft() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test void byKeyFindsEveryGenerator() {
        for (GeneratorType type : GeneratorTypes.ALL) {
            assertSame(type, GeneratorTypes.byKey(type.key()));
        }
    }

    @Test void byKeyReturnsNullForUnknownKey() {
        assertNull(GeneratorTypes.byKey("no_existe"));
    }

    /** BY_KEY usa toUnmodifiableMap, que lanza si hay claves repetidas: este test lo convierte
     *  en un fallo con nombre en vez de un ExceptionInInitializerError opaco. */
    @Test void everyGeneratorKeyIsUnique() {
        assertEquals(GeneratorTypes.ALL.size(),
                GeneratorTypes.ALL.stream().map(GeneratorType::key).distinct().count());
    }
}
