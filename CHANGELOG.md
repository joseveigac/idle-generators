# Changelog

## 1.2.0 — Minecraft 1.21.1

Stone & Construction update: 12 new generators (31 total). Suggested in part by community feedback on the Bedrock edition page (deepslate for skyblock).

- **New generators:** Stone, Granite, Diorite, Andesite, Deepslate, Tuff, Calcite, Dripstone (produces pointed dripstone), Gravel, Sand, Red Sand, Clay (produces clay balls).
- Recipes follow the house format: glass corners, product sample in the center, thematic vanilla catalysts on the edges. Buckets are returned on craft.
- Fully localized: English + Spanish (es_ES, names verified against vanilla).

> Version jump: 1.0.0 → 1.2.0. The Java edition skips 1.1.0 to align version numbers with the Bedrock edition, whose v1.1.0 (Wood Update) content was already included in Java 1.0.0. From v1.2.0 onward, both editions share version numbers for content updates.

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
