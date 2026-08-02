package com.ghozix.idlegenerators.generator;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.Map;

public record GeneratorType(String key, GeneratorCategory category, int intervalSeconds, int cap, Item product,
                            List<String> pattern, Map<Character, ItemLike> recipeKeys,
                            ItemLike unlockItem) {
    public long baseIntervalMs() { return intervalSeconds * 1000L; }
    public String blockId() { return key + "_generator"; }
}
