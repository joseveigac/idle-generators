package com.ghozix.idlegenerators;

import com.ghozix.idlegenerators.config.IGConfig;
import com.ghozix.idlegenerators.config.IGConfigReload;
import com.ghozix.idlegenerators.generator.GeneratorType;
import com.ghozix.idlegenerators.generator.GeneratorTypes;
import com.ghozix.idlegenerators.hud.GeneratorHud;
import com.ghozix.idlegenerators.network.IGNetwork;
import com.ghozix.idlegenerators.registry.ModBlocks;
import com.ghozix.idlegenerators.registry.ModItems;
import com.ghozix.idlegenerators.registry.ModBlockEntities;
import com.ghozix.idlegenerators.registry.ModCreativeTab;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

public final class IdleGenerators {
    public static final String MOD_ID = "idlegenerators";

    public static void init() {
        var holder = AutoConfig.register(IGConfig.class, GsonConfigSerializer::new);
        // El JSON lista todos los generadores con su tri-estado (DEFAULT si no existía).
        holder.getConfig().ensureAllKeys(GeneratorTypes.ALL.stream().map(GeneratorType::key).toList());
        holder.save();
        ModCreativeTab.register();
        ModBlocks.register();
        ModItems.register();
        ModBlockEntities.register();
        GeneratorHud.register();
        IGNetwork.register();
        IGConfigReload.register();
    }
}
