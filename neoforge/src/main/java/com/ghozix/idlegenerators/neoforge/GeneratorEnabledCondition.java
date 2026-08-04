package com.ghozix.idlegenerators.neoforge;

import com.ghozix.idlegenerators.config.IGConfig;
import com.ghozix.idlegenerators.generator.GeneratorType;
import com.ghozix.idlegenerators.generator.GeneratorTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.common.conditions.ICondition;

/**
 * Condición de carga {@code idlegenerators:generator_enabled} (v1.3.0 R4): descarta la receta y su
 * advancement cuando el generador está desactivado por config. El predicado es el mismo que usan el
 * ticker, el HUD y Jade — aquí no se duplica lógica, solo se consulta.
 */
public record GeneratorEnabledCondition(String generator) implements ICondition {
    public static final MapCodec<GeneratorEnabledCondition> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(Codec.STRING.fieldOf("generator").forGetter(GeneratorEnabledCondition::generator))
                    .apply(i, GeneratorEnabledCondition::new));

    @Override
    public boolean test(ICondition.IContext context) {
        GeneratorType type = GeneratorTypes.byKey(generator);
        // Clave desconocida (p. ej. datapack de otra versión): dejar cargar la receta.
        return type == null || IGConfig.get().isGeneratorEnabled(type.key(), type.category());
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
