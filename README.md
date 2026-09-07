# Idle Generators

**Place a generator and let it work — even while the world is closed.**

Idle Generators adds 56 craftable block-generators that produce resources passively over real time. Close your world, go offline, come back later — the generators track how much time has passed and fill up their internal buffer. When you return, an action-bar HUD shows the current buffer, and you can collect or break the block to retrieve everything that accumulated.

## Features

- **56 generators** — 11 mineral types, 9 wood types, 12 stone & construction types, 16 dye colors and 8 nature types, each with its own production interval and buffer cap (see the table below).
- **Offline production** — generators use real-world timestamps so items accumulate while the game is closed.
- **Action-bar HUD** — live buffer readout (items stored / cap) updates every tick when you look at a generator.
- **Collect or break** — right-click to collect without breaking; break the block to get the buffer plus the block itself.
- **Animated core renderer** — the generator block displays a rotating animated core whose appearance reflects the output type.
- **Optional Jade support** — look-at tooltip shows buffer status when [Jade](https://modrinth.com/mod/jade) is installed.
- **Hopper and pipe output** — the buffer is a real inventory: hoppers, droppers and mod pipes can pull from any side. Insertion is blocked; a comparator reads buffer fullness 0–15.
- **Turn generators off — or take them out of the game** — toggle a whole category (ores / woods / stones / colors / nature) or override any single generator to forced ON / forced OFF. A disabled generator loses its recipe and disappears from the recipe book, the creative tab and the JEI/EMI/REI item list. Generators already placed in the world are never destroyed: they just stop producing and can still be emptied.
- **Cloth Config integration** — in-game config screen for the HUD, a global production speed multiplier, drop-on-break, and the per-generator toggles. On a server, edit `config/idlegenerators.json` and run `/reload` to apply changes without a restart.

## Generators

| Generator | Interval | Buffer cap |
|---|---|---|
| Cobblestone | 5 s | 512 |
| Lapis | 15 s | 1024 |
| Redstone | 15 s | 1024 |
| Coal | 20 s | 512 |
| Copper | 30 s | 512 |
| Iron | 30 s | 512 |
| Quartz | 30 s | 512 |
| Gold | 35 s | 512 |
| Emerald | 60 s | 512 |
| Diamond | 80 s | 256 |
| Netherite | 100 s | 64 |
| Oak Log | 5 s | 64 |
| Spruce Log | 5 s | 64 |
| Birch Log | 5 s | 64 |
| Jungle Log | 5 s | 64 |
| Acacia Log | 5 s | 64 |
| Dark Oak Log | 5 s | 64 |
| Mangrove Log | 5 s | 64 |
| Cherry Log | 5 s | 64 |
| Pale Oak Log | 5 s | 64 |
| Stone | 5 s | 512 |
| Granite | 5 s | 512 |
| Diorite | 5 s | 512 |
| Andesite | 5 s | 512 |
| Deepslate | 10 s | 512 |
| Tuff | 5 s | 512 |
| Calcite | 10 s | 512 |
| Dripstone (pointed dripstone) | 10 s | 512 |
| Gravel | 5 s | 512 |
| Sand | 5 s | 512 |
| Red Sand | 5 s | 512 |
| Clay (clay balls) | 5 s | 1024 |
| White Dye | 5 s | 1024 |
| Light Gray Dye | 5 s | 1024 |
| Gray Dye | 5 s | 1024 |
| Black Dye | 5 s | 1024 |
| Brown Dye | 5 s | 1024 |
| Red Dye | 5 s | 1024 |
| Orange Dye | 5 s | 1024 |
| Yellow Dye | 5 s | 1024 |
| Lime Dye | 5 s | 1024 |
| Green Dye | 5 s | 1024 |
| Cyan Dye | 5 s | 1024 |
| Light Blue Dye | 5 s | 1024 |
| Blue Dye | 5 s | 1024 |
| Purple Dye | 5 s | 1024 |
| Magenta Dye | 5 s | 1024 |
| Pink Dye | 5 s | 1024 |
| Amethyst (amethyst shards) | 20 s | 512 |
| Honeycomb | 15 s | 512 |
| Glow Lichen | 5 s | 512 |
| Moss (moss blocks) | 5 s | 512 |
| Spore Blossom | 30 s | 256 |
| Big Dripleaf | 10 s | 256 |
| Flowering Azalea | 10 s | 256 |
| Resin (resin clumps) | 20 s | 512 |

## Requirements

- **Minecraft 26.2** — NeoForge or Fabric
- [Architectury API](https://modrinth.com/mod/architectury-api)
- [Cloth Config](https://modrinth.com/mod/cloth-config) (+ [Fabric API](https://modrinth.com/mod/fabric-api) on Fabric; [ModMenu](https://modrinth.com/mod/modmenu) optional on Fabric for the config screen)
- **Required on CLIENT and SERVER**

Optional: [Jade](https://modrinth.com/mod/jade) for look-at tooltips.

## Also available for Minecraft Bedrock

A Bedrock Edition version of Idle Generators is available as a behavior-pack addon.

---

All Rights Reserved © ghozix. Modpack inclusion via CurseForge/Modrinth-hosted packs is welcome; rehosting the jar is not permitted.
