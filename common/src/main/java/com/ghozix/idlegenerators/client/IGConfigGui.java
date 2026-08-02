package com.ghozix.idlegenerators.client;

import com.ghozix.idlegenerators.config.GeneratorToggle;
import com.ghozix.idlegenerators.config.IGConfig;
import com.ghozix.idlegenerators.generator.GeneratorCategory;
import com.ghozix.idlegenerators.generator.GeneratorType;
import com.ghozix.idlegenerators.generator.GeneratorTypes;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** GUI custom para el mapa de toggles (v1.3.0): un desplegable por categoría con un selector
 *  ON/OFF/Según-categoría por generador — nada de escribir claves a mano. Los nombres reusan
 *  las traducciones de bloque, así los sets futuros aparecen solos. */
@Environment(EnvType.CLIENT)
public final class IGConfigGui {
    private IGConfigGui() {}

    public static void register() {
        // 1.21.1: getGuiRegistry vive en AutoConfig (el split a AutoConfigClient es 26.x).
        AutoConfig.getGuiRegistry(IGConfig.class).registerPredicateProvider(
                IGConfigGui::buildGeneratorEntries,
                field -> field.getDeclaringClass() == IGConfig.class && field.getName().equals("generators"));
    }

    @SuppressWarnings("rawtypes")
    private static List<AbstractConfigListEntry> buildGeneratorEntries(
            String i13n, Field field, Object config, Object defaults, GuiRegistryAccess registry) {
        IGConfig cfg = (IGConfig) config;
        ConfigEntryBuilder eb = ConfigEntryBuilder.create();
        List<AbstractConfigListEntry> out = new ArrayList<>();
        for (GeneratorCategory category : GeneratorCategory.values()) {
            var sub = eb.startSubCategory(Component.translatable(
                    "text.autoconfig.idlegenerators.category." + category.name().toLowerCase(Locale.ROOT)));
            for (GeneratorType type : GeneratorTypes.ALL) {
                if (type.category() != category) continue;
                String key = type.key();
                sub.add(eb.startEnumSelector(
                                Component.translatable("block.idlegenerators." + type.blockId()),
                                GeneratorToggle.class,
                                cfg.generators.getOrDefault(key, GeneratorToggle.DEFAULT))
                        .setDefaultValue(GeneratorToggle.DEFAULT)
                        .setEnumNameProvider(value -> Component.translatable(
                                "text.autoconfig.idlegenerators.toggle." + value.name().toLowerCase(Locale.ROOT)))
                        .setSaveConsumer(value -> cfg.generators.put(key, value))
                        .build());
            }
            out.add(sub.build());
        }
        return out;
    }
}
