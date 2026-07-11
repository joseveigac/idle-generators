# Idle Generators

**Place a generator and let it work — even while the world is closed.**

Idle Generators adds 19 craftable block-generators that produce resources passively over real time. Close your world, go offline, come back later — the generators track how much time has passed and fill up their internal buffer. When you return, an action-bar HUD shows the current buffer, and you can collect or break the block to retrieve everything that accumulated.

## Features

- **19 generators** — 11 mineral types and 8 wood types, each with a configurable production interval and cap.
- **Offline production** — generators use real-world timestamps so items accumulate while the game is closed.
- **Action-bar HUD** — live buffer readout (items stored / cap) updates every tick when you look at a generator.
- **Collect or break** — right-click to collect without breaking; break the block to get the buffer plus the block itself.
- **Animated core renderer** — the generator block displays a rotating animated core whose appearance reflects the output type.
- **Optional Jade support** — look-at tooltip shows buffer status when [Jade](https://modrinth.com/mod/jade) is installed.
- **Cloth Config integration** — per-generator intervals and caps are fully configurable in-game.

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

## Requirements

- **Minecraft 1.21.1** — NeoForge or Fabric
- [Architectury API](https://modrinth.com/mod/architectury-api)
- [Cloth Config](https://modrinth.com/mod/cloth-config) (+ [Fabric API](https://modrinth.com/mod/fabric-api) on Fabric; [ModMenu](https://modrinth.com/mod/modmenu) optional on Fabric for the config screen)
- **Required on CLIENT and SERVER**

Optional: [Jade](https://modrinth.com/mod/jade) for look-at tooltips.

## Also available for Minecraft Bedrock

A Bedrock Edition version of Idle Generators is available as a behavior-pack addon.

---

All Rights Reserved © ghozix. Modpack inclusion via CurseForge/Modrinth-hosted packs is welcome; rehosting the jar is not permitted.
