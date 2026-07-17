# Changelog

## 1.2.0 — Minecraft 26.2

Stone & Construction update: 12 new generators (32 total). Suggested in part by community feedback on the Bedrock edition page (deepslate for skyblock).

- **New generators:** Stone, Granite, Diorite, Andesite, Deepslate, Tuff, Calcite, Dripstone (produces pointed dripstone), Gravel, Sand, Red Sand, Clay (produces clay balls).
- Recipes follow the house format: glass corners, the block as the center sample (dripstone block, clay block), stone-pickaxe / bucket / dye catalysts on the edges. Buckets are returned on craft.
- The action-bar HUD now auto-hides when Jade is installed (its look-at tooltip already shows the same info). New config option **Show HUD when Jade is installed** (default off) forces it back on.
- Fully localized: English + Spanish (es_ES, names verified against vanilla).

> Version jump: 1.0.0 → 1.2.0. The Java edition skips 1.1.0 to align version numbers with the Bedrock edition, whose v1.1.0 (Wood Update) content was already included in Java 1.0.0. From v1.2.0 onward, both editions share version numbers for content updates.

## 1.0.0 — Minecraft 26.2

Port from Minecraft 1.21.1 to Minecraft 26.2 (NeoForge + Fabric), with one new generator.

- **New: Pale Oak Log Generator** — 20th generator type, crafted with pale oak materials.
- Ported to Minecraft 26.2 (Java 25, unobfuscated game, loom-no-remap toolchain).
- Updated all dependencies: Architectury API 21.0.2, Fabric API 0.154.2+26.2, NeoForge 26.2.0.8-beta, Cloth Config 26.2.155.
- Block entity serialisation migrated to the new `ValueInput`/`ValueOutput` API.
- Block entity renderer rewritten for the new two-generic `BlockEntityRenderer<T, S>` design with explicit render states.
- `BlockRenderLayerMap` removed (render type is now declared in the model JSON via `"render_type": "minecraft:cutout"`).
- Item model dispatch files added (`assets/idlegenerators/items/`) for the new 26.2 item model format.

## 1.0.0

Initial release for Minecraft 1.21.1 (Fabric + NeoForge).

- 19 craftable generators: 11 mineral types (Cobblestone, Coal, Copper, Iron, Gold, Diamond, Emerald, Lapis, Redstone, Quartz, Netherite) and 8 wood types (Oak, Spruce, Birch, Jungle, Acacia, Dark Oak, Mangrove, Cherry).
- Offline production: generators track elapsed real time via saved timestamps so items accumulate while the world is closed.
- Collect behavior: right-click to take buffered items without breaking the block.
- Break behavior: breaking the block drops the buffer contents plus the block item.
- Action-bar HUD: live buffer readout (stored / cap) displayed every tick when looking at a generator.
- Animated block entity renderer: rotating core whose visual reflects the output type.
- Optional Jade integration: look-at tooltip shows buffer status when Jade is present.
- Cloth Config integration: production interval and buffer cap are configurable per generator type.
