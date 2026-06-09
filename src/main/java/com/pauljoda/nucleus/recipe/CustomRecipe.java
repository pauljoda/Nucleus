package com.pauljoda.nucleus.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * A type of recipe that does not return the standard ItemStack output from an input
 *
 * @param <C>
 */
public interface CustomRecipe<C extends RecipeInput> extends Recipe<C> {

    @Override
    default ItemStack assemble(C pContainer) {
        return ItemStack.EMPTY;
    }

    @Override
    default boolean showNotification() {
        return false;
    }

    @Override
    default String group() {
        return "";
    }

    @Override
    default PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    default RecipeBookCategory recipeBookCategory() {
        return new RecipeBookCategory();
    }
}
