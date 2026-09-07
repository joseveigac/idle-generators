package com.ghozix.idlegenerators.generator;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.List;
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

    /** Set Colors (v1.3.0): paridad Bedrock — 16 tintes, receta GTG/BDB/GTG, 5 s, cap 1024. */
    @Test void colorsSetMatchesBedrockParity() {
        List<GeneratorType> colors = GeneratorTypes.ALL.stream()
                .filter(t -> t.category() == GeneratorCategory.COLORS).toList();
        assertEquals(List.of("white_dye", "light_gray_dye", "gray_dye", "black_dye",
                "brown_dye", "red_dye", "orange_dye", "yellow_dye", "lime_dye", "green_dye",
                "cyan_dye", "light_blue_dye", "blue_dye", "purple_dye", "magenta_dye", "pink_dye"),
                colors.stream().map(GeneratorType::key).toList());
        for (GeneratorType t : colors) {
            assertEquals(5, t.intervalSeconds(), t.key());
            assertEquals(1024, t.cap(), t.key());
            assertEquals(List.of("GTG", "BDB", "GTG"), t.pattern(), t.key());
            assertSame(Items.GLASS, t.recipeKeys().get('G'), t.key());
            assertSame(Items.BONE_MEAL, t.recipeKeys().get('B'), t.key());
            assertSame(t.product(), t.recipeKeys().get('D'), t.key()); // regla de la muestra
            assertSame(t.product(), t.unlockItem(), t.key());          // desbloqueo por la muestra
        }
    }

    /** Set Nature (v1.4.0): paridad Bedrock — 8 generadores, receta GTG/BDB/GTG con enredaderas
     *  como par fijo, fuente temática T e intervalo/cap por generador (tiers del diseño). */
    @Test void natureSetMatchesBedrockParity() {
        List<GeneratorType> nature = GeneratorTypes.ALL.stream()
                .filter(t -> t.category() == GeneratorCategory.NATURE).toList();
        assertEquals(List.of("amethyst", "honeycomb", "glow_lichen", "moss", "spore_blossom",
                "big_dripleaf", "flowering_azalea", "resin"),
                nature.stream().map(GeneratorType::key).toList());
        for (GeneratorType t : nature) {
            assertEquals(List.of("GTG", "BDB", "GTG"), t.pattern(), t.key());
            assertSame(Items.GLASS, t.recipeKeys().get('G'), t.key());
            assertSame(Items.VINE, t.recipeKeys().get('B'), t.key());     // par fijo del set
            assertSame(t.product(), t.recipeKeys().get('D'), t.key()); // regla de la muestra
            assertSame(t.product(), t.unlockItem(), t.key());          // desbloqueo por la muestra
        }
        assertNature("amethyst", Items.AMETHYST_SHARD, Items.CALCITE, 20, 512);
        assertNature("honeycomb", Items.HONEYCOMB, Items.HONEY_BOTTLE, 15, 512);
        assertNature("glow_lichen", Items.GLOW_LICHEN, Items.BONE_MEAL, 5, 512);
        assertNature("moss", Items.MOSS_BLOCK, Items.MOSS_CARPET, 5, 512);
        assertNature("spore_blossom", Items.SPORE_BLOSSOM, Items.MOSS_BLOCK, 30, 256);
        assertNature("big_dripleaf", Items.BIG_DRIPLEAF, Items.SMALL_DRIPLEAF, 10, 256);
        assertNature("flowering_azalea", Items.FLOWERING_AZALEA, Items.AZALEA, 10, 256);
        assertNature("resin", Items.RESIN_CLUMP, Items.PALE_OAK_LOG, 20, 512);
    }

    private static void assertNature(String key, net.minecraft.world.item.Item product,
                                     net.minecraft.world.item.Item source, int seconds, int cap) {
        GeneratorType t = GeneratorTypes.byKey(key);
        assertNotNull(t, key);
        assertSame(product, t.product(), key);
        assertSame(source, t.recipeKeys().get('T'), key);
        assertEquals(seconds, t.intervalSeconds(), key);
        assertEquals(cap, t.cap(), key);
    }
}
