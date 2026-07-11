# Changelog

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
