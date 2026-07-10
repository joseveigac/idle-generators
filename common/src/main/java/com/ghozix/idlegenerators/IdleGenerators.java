package com.ghozix.idlegenerators;

import com.ghozix.idlegenerators.config.IGConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

public final class IdleGenerators {
    public static final String MOD_ID = "idlegenerators";

    public static void init() {
        AutoConfig.register(IGConfig.class, GsonConfigSerializer::new);
    }
}
