package com.ghozix.idlegenerators.fabric.datagen;

import com.ghozix.idlegenerators.generator.GeneratorType;
import com.ghozix.idlegenerators.generator.GeneratorTypes;
import com.ghozix.idlegenerators.registry.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;

import java.util.concurrent.CompletableFuture;

public class IGRecipeProvider extends FabricRecipeProvider {
    public IGRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        for (GeneratorType type : GeneratorTypes.ALL) {
            var builder = ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS,
                    ModBlocks.GENERATORS.get(type.key()).get());
            type.pattern().forEach(builder::pattern);
            type.recipeKeys().forEach(builder::define);
            builder.unlockedBy("has_" + type.key(), has(type.unlockItem()))
                   .save(output);
        }
    }
}
