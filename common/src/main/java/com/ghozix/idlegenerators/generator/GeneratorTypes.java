package com.ghozix.idlegenerators.generator;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class GeneratorTypes {
    private GeneratorTypes() {}

    private static GeneratorType wood(String key, Item log, Item sapling) {
        return new GeneratorType(key, GeneratorCategory.WOODS, 5, 64, log,
                List.of("GBG", "SLS", "GSG"),
                Map.of('G', Items.GLASS, 'B', Items.BONE_MEAL, 'S', sapling, 'L', log),
                log);
    }

    private static GeneratorType stone(String key, int intervalSeconds, int cap, Item product,
                                       Item topCatalyst, Item sideCatalyst) {
        return stone(key, intervalSeconds, cap, product, product, topCatalyst, sideCatalyst);
    }

    // Center sample is the BLOCK form of the output (differs from product for dripstone/clay);
    // like the woods, the recipe unlocks on the center block.
    private static GeneratorType stone(String key, int intervalSeconds, int cap, Item product,
                                       Item centerBlock, Item topCatalyst, Item sideCatalyst) {
        return new GeneratorType(key, GeneratorCategory.STONES, intervalSeconds, cap, product,
                List.of("GTG", "SBS", "GTG"),
                Map.of('G', Items.GLASS, 'T', topCatalyst, 'S', sideCatalyst, 'B', centerBlock),
                centerBlock);
    }

    public static final List<GeneratorType> ALL = List.of(
        // ── Minerales (11) ─────────────────────────────────────────────
        // Cobblestone cuenta como STONES para el filtro (decisión 2026-08-02): un jugador
        // que apaga "ores" espera apagar coal→netherite, no la piedra.
        new GeneratorType("cobblestone", GeneratorCategory.STONES, 5, 512, Items.COBBLESTONE,
            List.of("GPG", "LSW", "GPG"),
            Map.of('G', Items.GLASS, 'P', Items.STONE_PICKAXE, 'L', Items.LAVA_BUCKET,
                   'S', Items.SMOOTH_STONE, 'W', Items.WATER_BUCKET),
            Items.COBBLESTONE),
        new GeneratorType("coal", GeneratorCategory.ORES, 20, 512, Items.COAL,
            List.of("GFG", "FBF", "GFG"),
            Map.of('G', Items.GLASS, 'F', Items.FURNACE, 'B', Items.COAL_BLOCK),
            Items.COAL),
        new GeneratorType("copper", GeneratorCategory.ORES, 30, 512, Items.RAW_COPPER,
            List.of("GPG", "PBP", "GPG"),
            Map.of('G', Items.GLASS, 'P', Items.LIGHTNING_ROD.weathering().unaffected(),
                   'B', Items.COPPER_BLOCK.weathering().unaffected()),
            Items.COPPER_INGOT),
        new GeneratorType("iron", GeneratorCategory.ORES, 30, 512, Items.RAW_IRON,
            List.of("GPG", "PBP", "GPG"),
            Map.of('G', Items.GLASS, 'P', Items.IRON_PICKAXE, 'B', Items.IRON_BLOCK),
            Items.IRON_INGOT),
        new GeneratorType("gold", GeneratorCategory.ORES, 35, 512, Items.GOLD_INGOT,
            List.of("GBG", "BCB", "GBG"),
            Map.of('G', Items.GLASS, 'B', Items.BLAZE_ROD, 'C', Items.GOLD_BLOCK),
            Items.GOLD_INGOT),
        new GeneratorType("diamond", GeneratorCategory.ORES, 80, 256, Items.DIAMOND,
            List.of("GOG", "OBO", "GOG"),
            Map.of('G', Items.GLASS, 'O', Items.CRYING_OBSIDIAN, 'B', Items.DIAMOND_BLOCK),
            Items.DIAMOND),
        new GeneratorType("emerald", GeneratorCategory.ORES, 60, 512, Items.EMERALD,
            List.of("GQG", "CBC", "GCG"),
            Map.of('G', Items.GLASS, 'Q', Items.BELL, 'C', Items.BOOKSHELF, 'B', Items.EMERALD_BLOCK),
            Items.EMERALD),
        new GeneratorType("lapis", GeneratorCategory.ORES, 15, 1024, Items.LAPIS_LAZULI,
            List.of("GQG", "CBC", "GQG"),
            Map.of('G', Items.GLASS, 'Q', Items.PRISMARINE_SHARD, 'C', Items.PRISMARINE_CRYSTALS,
                   'B', Items.LAPIS_BLOCK),
            Items.LAPIS_LAZULI),
        new GeneratorType("redstone", GeneratorCategory.ORES, 15, 1024, Items.REDSTONE,
            List.of("GQG", "CBC", "GQG"),
            Map.of('G', Items.GLASS, 'Q', Items.QUARTZ, 'C', Items.COMPARATOR, 'B', Items.REDSTONE_BLOCK),
            Items.REDSTONE),
        new GeneratorType("quartz", GeneratorCategory.ORES, 30, 512, Items.QUARTZ,
            List.of("GFG", "FBF", "GFG"),
            Map.of('G', Items.GLASS, 'F', Items.GLOWSTONE, 'B', Items.QUARTZ_BLOCK),
            Items.QUARTZ),
        new GeneratorType("netherite", GeneratorCategory.ORES, 100, 64, Items.NETHERITE_INGOT,
            List.of("GNG", "RBR", "GSG"),
            Map.of('G', Items.GLASS, 'N', Items.NETHER_STAR, 'R', Items.WITHER_ROSE,
                   'B', Items.NETHERITE_BLOCK, 'S', Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
            Items.NETHERITE_INGOT),
        // ── Maderas (8 en 1.21.1; pale_oak solo existe desde 1.21.4 → rama 26.2)
        wood("oak_log", Items.OAK_LOG, Items.OAK_SAPLING),
        wood("spruce_log", Items.SPRUCE_LOG, Items.SPRUCE_SAPLING),
        wood("birch_log", Items.BIRCH_LOG, Items.BIRCH_SAPLING),
        wood("jungle_log", Items.JUNGLE_LOG, Items.JUNGLE_SAPLING),
        wood("acacia_log", Items.ACACIA_LOG, Items.ACACIA_SAPLING),
        wood("dark_oak_log", Items.DARK_OAK_LOG, Items.DARK_OAK_SAPLING),
        wood("mangrove_log", Items.MANGROVE_LOG, Items.MANGROVE_PROPAGULE),
        wood("cherry_log", Items.CHERRY_LOG, Items.CHERRY_SAPLING),
        // Pale oak is exclusive to MC 26.x (1.21.4+): only present on the 26.2 branch.
        wood("pale_oak_log", Items.PALE_OAK_LOG, Items.PALE_OAK_SAPLING),
        // ── Stone & Construction (12) ──────────────────────────────────
        stone("stone", 5, 512, Items.STONE, Items.FURNACE, Items.COBBLESTONE),
        stone("granite", 5, 512, Items.GRANITE, Items.STONE_PICKAXE, Items.COBBLESTONE),
        stone("diorite", 5, 512, Items.DIORITE, Items.STONE_PICKAXE, Items.COBBLESTONE),
        stone("andesite", 5, 512, Items.ANDESITE, Items.STONE_PICKAXE, Items.COBBLESTONE),
        stone("deepslate", 10, 512, Items.DEEPSLATE, Items.STONE_PICKAXE, Items.COBBLESTONE),
        stone("tuff", 5, 512, Items.TUFF, Items.LAVA_BUCKET, Items.COBBLESTONE),
        stone("calcite", 10, 512, Items.CALCITE, Items.BONE_MEAL, Items.COBBLESTONE),
        stone("dripstone", 10, 512, Items.POINTED_DRIPSTONE, Items.DRIPSTONE_BLOCK,
              Items.WATER_BUCKET, Items.COBBLESTONE),
        stone("gravel", 5, 512, Items.GRAVEL, Items.FLINT, Items.COBBLESTONE),
        stone("sand", 5, 512, Items.SAND, Items.WATER_BUCKET, Items.COBBLESTONE),
        // 26.x groups colored variants into ColorCollection (1.21.1 uses plain Items.RED_DYE).
        stone("red_sand", 5, 512, Items.RED_SAND, Items.DYE.red(), Items.SAND),
        stone("clay", 5, 1024, Items.CLAY_BALL, Items.CLAY, Items.WATER_BUCKET, Items.COBBLESTONE)
    );

    // Declarado DESPUÉS de ALL a propósito: los campos estáticos se inicializan en orden de
    // declaración, así que aquí arriba ALL todavía sería null.
    private static final Map<String, GeneratorType> BY_KEY =
        ALL.stream().collect(Collectors.toUnmodifiableMap(GeneratorType::key, t -> t));

    /** Lookup por clave para las condiciones de carga de receta (v1.3.0). */
    @Nullable
    public static GeneratorType byKey(String key) { return BY_KEY.get(key); }
}
