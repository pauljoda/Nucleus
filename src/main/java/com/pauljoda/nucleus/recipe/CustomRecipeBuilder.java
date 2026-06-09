package com.pauljoda.nucleus.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

/**
 * This interface represents a custom recipe builder.
 *
 * @param <R> The type of recipe.
 */
public interface CustomRecipeBuilder<R extends Recipe<?>> extends RecipeBuilder {


    /**
     * Creates a recipe.
     *
     * @return The created recipe.
     */
    R createRecipe();

    /**
     * Saves a recipe with the given {@code pRecipeOutput} and {@code pId}.
     *
     * @param pRecipeOutput The output of the recipe.
     * @param pId           The resource location to assign to the saved recipe.
     */
    @Override
    default void save(RecipeOutput pRecipeOutput, ResourceKey<Recipe<?>> pId) {
        var recipe = createRecipe();
        pRecipeOutput.accept(pId, recipe, null);
    }

    /*******************************************************************************************************************
     * Basic Implementations                                                                                           *
     *******************************************************************************************************************/

    /**
     * Sets the criterion for unlocking the recipe.
     *
     * @param pName      the name of the criterion.
     * @param pCriterion the criterion used to unlock the recipe.
     * @return the RecipeBuilder instance.
     */
    @Override
    default RecipeBuilder unlockedBy(String pName, Criterion<?> pCriterion) {
        return this;
    }

    /**
     * Specifies the group for the recipe.
     *
     * @param pGroupName The name of the group for the recipe. Can be null.
     * @return The modified RecipeBuilder instance.
     */
    @Override
    default RecipeBuilder group(@Nullable String pGroupName) {
        return this;
    }

    /**
     * Retrieves the result of the recipe.
     *
     * @return The result of the recipe.
     */
    @Override
    default ResourceKey<Recipe<?>> defaultId() {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("nucleus", "custom_recipe"));
    }
}
