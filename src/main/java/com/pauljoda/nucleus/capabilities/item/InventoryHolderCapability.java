package com.pauljoda.nucleus.capabilities.item;

import com.pauljoda.nucleus.common.container.IInventoryCallback;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class InventoryHolderCapability implements IItemHandlerModifiable {

    // Variables
    // A list to hold all callback objects
    public List<IInventoryCallback> callBacks = new ArrayList<>();
    // List of Inventory contents
    public final InventoryContents inventoryContents;

    public InventoryHolderCapability(InventoryContents contents) {
        this.inventoryContents = contents;
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

    /*******************************************************************************************************************
     * InventoryHolder                                                                                                 *
     *******************************************************************************************************************/

    /**
     * Add a callback to this inventory
     *
     * @param iInventoryCallback The callback you wish to add
     * @return This object, to enable chaining
     */
    public InventoryHolderCapability addCallback(IInventoryCallback iInventoryCallback) {
        callBacks.add(iInventoryCallback);
        return this;
    }

    /**
     * Called when the inventory has a change
     *
     * @param slot The slot that changed
     */
    protected void onInventoryChanged(int slot) {
        callBacks.forEach((IInventoryCallback callback) -> callback.onInventoryChanged(this, slot));
    }

    /**
     * Used to copy from an existing inventory
     *
     * @param inventory The inventory to copy from
     */
    public void copyFrom(IItemHandler inventory) {
        for (int i = 0; i < inventory.getSlots(); i++) {
            if (i < inventoryContents.inventory.size()) {
                ItemStack stack = inventory.getStackInSlot(i);
                if (!stack.isEmpty())
                    inventoryContents.inventory.set(i, stack.copy());
                else
                    inventoryContents.inventory.set(i, ItemStack.EMPTY);
            }
        }
    }

    /**
     * Makes sure this slot is within our range
     *
     * @param slot Which slot
     */
    protected boolean isValidSlot(int slot) {
        return slot >= 0 && slot < inventoryContents.inventory.size();
    }

    /*******************************************************************************************************************
     * IItemHandlerModifiable                                                                                          *
     *******************************************************************************************************************/

    /**
     * Overrides the stack in the given slot. This method is used by the
     * standard Forge helper methods and classes. It is not intended for
     * general use by other mods, and the handler may throw an error if it
     * is called unexpectedly.
     *
     * @param slot  Slot to modify
     * @param stack ItemStack to set slot to (may be null)
     * @throws RuntimeException if the handler is called in a way that the handler
     *                          was not expecting.
     **/
    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        if (!isValidSlot(slot))
            return;
        if (ItemStack.isSameItemSameComponents(this.inventoryContents.inventory.get(slot), stack))
            return;
        this.inventoryContents.inventory.set(slot, stack);
        onInventoryChanged(slot);
    }

    /*******************************************************************************************************************
     * IItemHandler Methods                                                                                            *
     *******************************************************************************************************************/
    /**
     * Returns the number of slots available
     *
     * @return The number of slots available
     **/
    @Override
    public int getSlots() {
        return inventoryContents.inventory.size();
    }

    /**
     * Returns the ItemStack in a given slot.
     * <p>
     * The result's stack size may be greater than the itemstacks max size.
     * <p>
     * If the result is null, then the slot is empty.
     * If the result is not null but the stack size is zero, then it represents
     * an empty slot that will only accept* a specific itemstack.
     * <p>
     * IMPORTANT: This ItemStack MUST NOT be modified. This method is not for
     * altering an inventories contents. Any implementers who are able to detect
     * modification through this method should throw an exception.
     * p
     * SERIOUSLY: DO NOT MODIFY THE RETURNED ITEMSTACK
     *
     * @param slot Slot to query
     * @return ItemStack in given slot. May not be null.
     **/
    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slot) {
        if (!isValidSlot(slot))
            return ItemStack.EMPTY;
        return inventoryContents.inventory.get(slot);
    }

    /**
     * Inserts an ItemStack into the given slot and return the remainder.
     * The ItemStack should not be modified in this function!
     * Note: This behaviour is subtly different from IFluidHandlers.fill()
     *
     * @param slot     Slot to insert into.
     * @param stack    ItemStack to insert.
     * @param simulate If true, the insertion is only simulated
     * @return The remaining ItemStack that was not inserted (if the entire stack is accepted, then return null).
     * May be the same as the input ItemStack if unchanged, otherwise a new ItemStack.
     **/
    @Nonnull
    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (!isInputSlot(slot) || !isItemValidForSlot(slot, stack))
            return stack;

        if (stack.isEmpty() || !isValidSlot(slot))
            return stack;

        ItemStack existing = this.inventoryContents.inventory.get(slot);

        int limit = getSlotLimit(slot);

        if (!existing.isEmpty()) {
            if (!ItemStack.isSameItemSameComponents(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (existing.isEmpty()) {
                this.inventoryContents.inventory.set(slot, reachedLimit ? stack.copyWithCount(limit) : stack);
            } else {
                existing.setCount(existing.getCount() + (reachedLimit ? limit : stack.getCount()));
            }
            onInventoryChanged(slot);
        }

        return reachedLimit ? stack.copyWithCount(stack.getCount() - limit) : ItemStack.EMPTY;
    }

    /**
     * Checks if the specified slot is an input slot.
     *
     * @param slot The slot number to check.
     * @return true if the slot is an input slot, false otherwise.
     */
    public boolean isInputSlot(int slot) {
        return true;
    }

    /**
     * Extracts an ItemStack from the given slot. The returned value must be null
     * if nothing is extracted, otherwise it's stack size must not be greater than amount or the
     * itemstacks getMaxStackSize().
     *
     * @param slot     Slot to extract from.
     * @param amount   Amount to extract (may be greater than the current stacks max limit)
     * @param simulate If true, the extraction is only simulated
     * @return ItemStack extracted from the slot, must be null, if nothing can be extracted
     **/
    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!isOutputSlot(slot) || amount == 0)
            return ItemStack.EMPTY;

        if (!isValidSlot(slot))
            return ItemStack.EMPTY;
        ItemStack existing = this.inventoryContents.inventory.get(slot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract) {
            if (!simulate) {
                this.inventoryContents.inventory.set(slot, ItemStack.EMPTY);
                onInventoryChanged(slot);
            }
            return existing;
        } else {
            if (!simulate) {
                this.inventoryContents.inventory.set(slot, existing.copyWithCount(existing.getCount() - toExtract));
                onInventoryChanged(slot);
            }

            return existing.copyWithCount(toExtract);
        }
    }

    /**
     * Checks if the current slot is an output slot.
     *
     * @return true if the current slot is an output slot, false otherwise.
     */
    public boolean isOutputSlot(int slot) {
        return true;
    }

    /**
     * Retrieves the maximum stack size allowed to exist in the given slot.
     *
     * @param slot Slot to query.
     * @return The maximum stack size allowed in the slot.
     */
    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    /**
     * <p>
     * This function re-implements the vanilla function.
     * It should be used instead of simulated insertions in cases where the contents and state of the inventory are
     * irrelevant, mainly for the purpose of automation and logic (for instance, testing if a minecart can wait
     * to deposit its items into a full inventory, or if the items in the minecart can never be placed into the
     * inventory and should move on).
     * </p>
     * <ul>
     * <li>isItemValid is false when insertion of the item is never valid.</li>
     * <li>When isItemValid is true, no assumptions can be made and insertion must be simulated case-by-case.</li>
     * <li>The actual items in the inventory, its fullness, or any other state are <strong>not</strong> considered by isItemValid.</li>
     * </ul>
     *
     * @param slot  Slot to query for validity
     * @param stack Stack to test with for validity
     * @return true if the slot can insert the ItemStack, not considering the current state of the inventory.
     * false if the slot can never insert the ItemStack in any situation.
     */
    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return isItemValidForSlot(slot, stack);
    }
}
