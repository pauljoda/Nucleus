package com.pauljoda.nucleus.common.blocks.entity.item;

import com.pauljoda.nucleus.capabilities.item.InventoryContents;
import com.pauljoda.nucleus.capabilities.item.NucleusItemResourceHandler;
import com.pauljoda.nucleus.common.blocks.entity.Syncable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * This file was created for Nucleus - Java
 * <p>
 * Nucleus - Java is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/6/2017
 */
public abstract class InventoryHandler extends Syncable {

    private final InventoryContents inventory;
    private final NucleusItemResourceHandler itemResourceHandler;

    public InventoryHandler(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);

        inventory = initializeInventory();
        itemResourceHandler = new NucleusItemResourceHandler(inventory, this::isItemValidForSlot, slot -> markForUpdate(3));
    }

    /*******************************************************************************************************************
     * Abstract Methods                                                                                                *
     *******************************************************************************************************************/

    /**
     * The initial size of the inventory
     *
     * @return How big to make the inventory on creation
     */
    protected abstract int getInventorySize();

    /**
     * Used to define if an item is valid for a slot
     *
     * @param index The slot id
     * @param stack The stack to check
     * @return True if you can put this there
     */
    protected abstract boolean isItemValidForSlot(int index, ItemStack stack);

    /**
     * Initializes the inventory for an InventoryHandlerItem object.
     *
     * @return The initialized InventoryHolder object.
     */
    protected abstract InventoryContents initializeInventory();

    /*******************************************************************************************************************
     * Capabilities                                                                                                    *
     *******************************************************************************************************************/

    /**
     * Retrieves the item capability of the inventory.
     *
     * @return The item capability of the inventory.
     */
    public NucleusItemResourceHandler getItemResourceHandler() {
        return itemResourceHandler;
    }

    /**
     * Retrieves the item capability of the inventory in the specified direction.
     *
     * @param direction The direction in which to retrieve the item capability.
     * @return The item capability of the inventory in the specified direction.
     */
    public NucleusItemResourceHandler getItemResourceHandlerSided(Direction direction) {
        return getItemResourceHandler();
    }

    /**
     * Retrieves the current contents of the inventory.
     *
     * @return The InventoryContents object representing the current state of the inventory.
     */
    public InventoryContents getInventoryContents() {
        return inventory;
    }

    /*******************************************************************************************************************
     * TileEntity                                                                                                      *
     *******************************************************************************************************************/

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        inventory.load(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        inventory.save(output);
    }
}
