package com.pauljoda.nucleus.capabilities.item;

import com.pauljoda.nucleus.common.Savable;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class InventoryContents implements Savable {
    // List of Inventory contents
    public NonNullList<ItemStack> inventory = NonNullList.withSize(getInventorySize(), ItemStack.EMPTY);

    /*******************************************************************************************************************
     * Abstract Methods                                                                                                *
     *******************************************************************************************************************/

    /**
     * The initial size of the inventory
     *
     * @return How big to make the inventory on creation
     */
    public abstract int getInventorySize();

    /*******************************************************************************************************************
     * Savable Methods                                                                                                 *
     *******************************************************************************************************************/

    /**
     * Loads the data from the given ValueInput.
     *
     * @param input The input containing the data to be loaded.
     */
    @Override
    public void load(ValueInput input) {
        ContainerHelper.loadAllItems(input, inventory);
    }

    /**
     * Saves the data of the object into the specified ValueOutput.
     *
     * @param output The output to store the data into.
     */
    @Override
    public void save(ValueOutput output) {
        ContainerHelper.saveAllItems(output, inventory);
    }
}
