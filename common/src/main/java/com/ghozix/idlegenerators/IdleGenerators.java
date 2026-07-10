package com.ghozix.idlegenerators;

import com.ghozix.idlegenerators.config.IGConfig;
import com.ghozix.idlegenerators.registry.ModBlocks;
import com.ghozix.idlegenerators.registry.ModItems;
import com.ghozix.idlegenerators.registry.ModBlockEntities;
import com.ghozix.idlegenerators.registry.ModCreativeTab;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

public final class IdleGenerators {
    public static final String MOD_ID = "idlegenerators";

    public static void init() {
        AutoConfig.register(IGConfig.class, GsonConfigSerializer::new);
        ModCreativeTab.register();
        ModBlocks.register();
        ModItems.register();
        ModBlockEntities.register();
    }
}
