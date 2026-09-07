# Changelog

## 1.4.0 — Nature

Nature update: 7 new generators of living Overworld nature (54 total; the Resin generator needs Minecraft 1.21.4+ and ships only in the 26.2 build). Same content and version number as the Bedrock edition's v1.4.0.

- **New generators:** Amethyst (amethyst shards), Honeycomb, Glow Lichen, Moss (moss blocks), Spore Blossom, Big Dripleaf, Flowering Azalea.
- Recipes follow the Colors layout: glass corners, two vines on the sides (the set's signature), a thematic ingredient top and bottom (calcite, honey bottle, bone meal, moss carpet, moss block, small dripleaf, azalea) and the produced item itself as the center sample.
- Three speed tiers: moss and glow lichen every 5 s (cap 512); big dripleaf and flowering azalea every 10 s and honeycomb every 15 s; amethyst every 20 s (cap 512) and spore blossom, the rarest, every 30 s (cap 256).
- New category toggle **Nature generators enabled** plus the usual per-generator override; nature generators appear in their own dropdown of the config screen.
- Fully localized: English + Spanish (es_ES, names verified against vanilla).

## 1.3.0 — Automation & Control

Generators can now be piped into hoppers, and server owners can turn any generator off — or take it out of the game entirely.

**Automation**

- **Hopper and pipe output** — the buffer is exposed as a real inventory, so hoppers, droppers and mod pipes can pull items out of a generator from any side. Insertion stays blocked: a generator produces, it does not store what you put in.
- **Comparator output** — a comparator reading a generator reports buffer fullness on the usual 0–15 scale.
- Right-clicking to collect and breaking the block still work exactly as before.

**Per-generator control**

- **Category toggles** — turn ore, wood or stone generators on or off as a group.
- **Per-generator override** — each of the 31 generators has its own three-state setting: follow the category, force ON, or force OFF. The config screen shows them as a dropdown per category, so there are no raw keys to type.
- **Disabled generators are out of the game, not just idle** — a disabled generator loses its recipe, disappears from the recipe book, from the creative tab and from the item list of JEI, EMI and REI. Its tooltip says so in red.
- **Already-placed generators are never destroyed.** Blocks and items are never unregistered, so an existing world keeps working: a disabled generator simply stops producing, and its buffer can still be emptied by hand or by hopper. You can still place one you already own.
- **Hot reload** — edit `config/idlegenerators.json` on a server and run `/reload`; the change applies without a restart. On a dedicated server, what counts is the server's config: connected clients are told which generators are disabled, so everyone sees the same thing.

**Timing note:** production, the HUD and Jade react instantly. Recipes, the creative tab, the recipe viewers and the tooltip update when you join the world or on `/reload`.

## 1.2.0 — Minecraft 1.21.1

Stone & Construction update: 12 new generators (31 total). Suggested in part by community feedback on the Bedrock edition page (deepslate for skyblock).

- **New generators:** Stone, Granite, Diorite, Andesite, Deepslate, Tuff, Calcite, Dripstone (produces pointed dripstone), Gravel, Sand, Red Sand, Clay (produces clay balls).
- Recipes follow the house format: glass corners, the block as the center sample (dripstone block, clay block), stone-pickaxe / bucket / dye catalysts on the edges. Buckets are returned on craft.
- The action-bar HUD now auto-hides when Jade is installed (its look-at tooltip already shows the same info). New config option **Show HUD when Jade is installed** (default off) forces it back on.
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
