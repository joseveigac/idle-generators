package com.ghozix.idlegenerators.fabric;

import com.ghozix.idlegenerators.IdleGenerators;
import com.ghozix.idlegenerators.config.IGConfig;
import com.ghozix.idlegenerators.generator.GeneratorType;
import com.ghozix.idlegenerators.generator.GeneratorTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

/**
 * Gemela Fabric de la condición {@code idlegenerators:generator_enabled} (v1.3.0 R4). Mismo id y
 * mismo campo {@code generator} que la de NeoForge, para que un único JSON de doble clave sirva a
 * los dos loaders. El predicado es el de {@code IGConfig}, no se duplica lógica.
 */
public record GeneratorEnabledCondition(String generator) implements ResourceCondition {
    public static final MapCodec<GeneratorEnabledCondition> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(Codec.STRING.fieldOf("generator").forGetter(GeneratorEnabledCondition::generator))
                    .apply(i, GeneratorEnabledCondition::new));

    public static final ResourceConditionType<GeneratorEnabledCondition> TYPE =
            ResourceConditionType.create(
                    ResourceLocation.fromNamespaceAndPath(IdleGenerators.MOD_ID, "generator_enabled"), CODEC);

    @Override
    public ResourceConditionType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean test(HolderLookup.Provider registryLookup) {
        GeneratorType type = GeneratorTypes.byKey(generator);
        // Clave desconocida (p. ej. datapack de otra versión): dejar cargar la receta.
        return type == null || IGConfig.get().isGeneratorEnabled(type.key(), type.category());
    }
}
