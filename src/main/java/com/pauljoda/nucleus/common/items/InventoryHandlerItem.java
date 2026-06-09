package com.pauljoda.nucleus.common.items;

import com.pauljoda.nucleus.capabilities.item.InventoryContents;
import com.pauljoda.nucleus.common.components.ItemInventoryComponent;
import com.pauljoda.nucleus.registration.NucleusDataComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/**
 * This file was created for Nucleus
 * <p>
 * Nucleus is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 11/13/17
 */
public abstract class InventoryHandlerItem implements ResourceHandler<ItemResource> {

    // Variables
    private ItemStack heldStack;

    private final InventoryContents inventory;
    private final InventoryJournal inventoryJournal = new InventoryJournal();

    /**
     * Creates a handler with given stack
     *
     * @param stack Stack to attach to
     */
    public InventoryHandlerItem(ItemStack stack) {
        heldStack = stack;

        inventory = initializeInventory();

        checkStackTag();
    }

    /*******************************************************************************************************************
     * Abstract Methods                                                                                                *
     *******************************************************************************************************************/

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
     * InventoryHandler                                                                                                *
     *******************************************************************************************************************/

    /**
     * Retrieves the inventory contents.
     *
     * @return The inventory contents.
     */
    public InventoryContents getInventory() {
        return inventory;
    }

    /**
     * Makes sure we always have a valid tag
     */
    protected void checkStackTag() {
        ItemInventoryComponent component = heldStack.get(NucleusDataComponents.ITEM_INVENTORY.get());
        if (component != null) {
            component.loadInto(inventory.inventory);
            return;
        }

        saveInventoryToStack();
    }

    protected void saveInventoryToStack() {
        heldStack.set(NucleusDataComponents.ITEM_INVENTORY.get(), ItemInventoryComponent.from(inventory.inventory));
    }

    protected void restoreInventoryFromComponent(ItemInventoryComponent component) {
        component.loadInto(inventory.inventory);
        saveInventoryToStack();
    }

    /*******************************************************************************************************************
     * ResourceHandler                                                                                                 *
     *******************************************************************************************************************/

    @Override
    public int size() {
        return inventory.inventory.size();
    }

    @Override
    public ItemResource getResource(int index) {
        return ItemResource.of(getStack(index));
    }

    @Override
    public long getAmountAsLong(int index) {
        return getStack(index).getCount();
    }

    @Override
    public long getCapacityAsLong(int index, ItemResource resource) {
        if (!resource.isEmpty() && !isValid(index, resource))
            return 0;
        return isValidSlot(index) ? 64 : 0;
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        return resource.isEmpty() || isValidSlot(index) && isItemValidForSlot(index, resource.toStack());
    }

    @Override
    public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (resource.isEmpty() || amount <= 0)
            return 0;

        int inserted = insertedAmount(index, resource.toStack(amount));
        if (inserted > 0) {
            inventoryJournal.updateSnapshots(transaction);
            insertStack(index, resource.toStack(inserted));
        }
        return inserted;
    }

    @Override
    public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (resource.isEmpty() || amount <= 0 || !resource.matches(getStack(index)))
            return 0;

        int extracted = extractedAmount(index, amount);
        if (extracted > 0) {
            inventoryJournal.updateSnapshots(transaction);
            extractStack(index, extracted);
        }
        return extracted;
    }

    private boolean isValidSlot(int slot) {
        return slot >= 0 && slot < inventory.inventory.size();
    }

    private ItemStack getStack(int slot) {
        return isValidSlot(slot) ? inventory.inventory.get(slot) : ItemStack.EMPTY;
    }

    private int insertedAmount(int slot, ItemStack stack) {
        if (stack.isEmpty() || !isValid(slot, ItemResource.of(stack)))
            return 0;

        ItemStack existing = getStack(slot);
        int limit = 64;
        if (!existing.isEmpty()) {
            if (!ItemStack.isSameItemSameComponents(stack, existing))
                return 0;
            limit -= existing.getCount();
        }
        return Math.max(0, Math.min(stack.getCount(), limit));
    }

    private void insertStack(int slot, ItemStack stack) {
        ItemStack existing = getStack(slot);
        if (existing.isEmpty()) {
            inventory.inventory.set(slot, stack.copy());
        } else {
            existing.grow(stack.getCount());
        }
        saveInventoryToStack();
    }

    private int extractedAmount(int slot, int amount) {
        ItemStack existing = getStack(slot);
        if (existing.isEmpty())
            return 0;
        return Math.min(amount, existing.getCount());
    }

    private void extractStack(int slot, int amount) {
        ItemStack existing = getStack(slot);
        if (existing.getCount() <= amount) {
            inventory.inventory.set(slot, ItemStack.EMPTY);
        } else {
            existing.shrink(amount);
        }
        saveInventoryToStack();
    }

    private class InventoryJournal extends SnapshotJournal<ItemInventoryComponent> {
        @Override
        protected ItemInventoryComponent createSnapshot() {
            return ItemInventoryComponent.from(inventory.inventory);
        }

        @Override
        protected void revertToSnapshot(ItemInventoryComponent snapshot) {
            restoreInventoryFromComponent(snapshot);
        }
    }

}
