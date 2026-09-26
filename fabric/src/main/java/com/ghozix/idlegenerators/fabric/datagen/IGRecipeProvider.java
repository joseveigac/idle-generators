package com.ghozix.idlegenerators.fabric.datagen;

import com.ghozix.idlegenerators.generator.GeneratorType;
import com.ghozix.idlegenerators.generator.GeneratorTypes;
import com.ghozix.idlegenerators.registry.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.crafting.Recipe;

import java.util.concurrent.CompletableFuture;

// MC 26.x datagen changes:
//   - FabricDataOutput → FabricPackOutput
//   - FabricRecipeProvider now extends RecipeProvider.Runner; override createRecipeProvider()
//     returning an anonymous RecipeProvider with buildRecipes().
//   - ShapedRecipeBuilder.shaped(cat, item) → RecipeProvider.shaped(cat, item) convenience method
//     (available inside the anonymous class which extends RecipeProvider).
//   - getName() must be implemented (DataProvider abstract method).
//   - 26.3: RecipeProvider is built from BootstrapContext<Recipe<?>> + BootstrapContext<Advancement>
//     instead of (HolderLookup.Provider, RecipeOutput); shaped()/has()/output are unchanged.
public class IGRecipeProvider extends FabricRecipeProvider {
    public IGRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public String getName() {
        return "Idle Generators Recipes";
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries,
                                                  BootstrapContext<Recipe<?>> recipes,
                                                  BootstrapContext<Advancement> advancements) {
        return new RecipeProvider(recipes, advancements) {
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
