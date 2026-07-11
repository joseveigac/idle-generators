package com.ghozix.idlegenerators.fabric.datagen;

import com.ghozix.idlegenerators.generator.GeneratorType;
import com.ghozix.idlegenerators.generator.GeneratorTypes;
import com.ghozix.idlegenerators.registry.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.concurrent.CompletableFuture;

// MC 26.x datagen changes:
//   - FabricDataOutput → FabricPackOutput
//   - FabricRecipeProvider now extends RecipeProvider.Runner; override createRecipeProvider()
//     returning an anonymous RecipeProvider with buildRecipes().
//   - ShapedRecipeBuilder.shaped(cat, item) → RecipeProvider.shaped(cat, item) convenience method
//     (available inside the anonymous class which extends RecipeProvider).
//   - getName() must be implemented (DataProvider abstract method).
public class IGRecipeProvider extends FabricRecipeProvider {
    public IGRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public String getName() {
        return "Idle Generators Recipes";
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput recipeOutput) {
        return new RecipeProvider(registries, recipeOutput) {
            @Override
            public void buildRecipes() {
                for (GeneratorType type : GeneratorTypes.ALL) {
                    var builder = shaped(RecipeCategory.DECORATIONS,
                            ModBlocks.GENERATORS.get(type.key()).get());
                    type.pattern().forEach(builder::pattern);
                    type.recipeKeys().forEach(builder::define);
                    builder.unlockedBy("has_" + type.key(), has(type.unlockItem()))
                           .save(output);
                }
            }
        };
    }
}
