package com.pauljoda.nucleus.capabilities.item;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.function.BiPredicate;
import java.util.function.IntConsumer;

public class NucleusItemResourceHandler implements ResourceHandler<ItemResource> {
    private final InventoryContents inventory;
    private final BiPredicate<Integer, ItemStack> slotValidator;
    private final IntConsumer changedCallback;
    private final InventoryJournal inventoryJournal = new InventoryJournal();

    public NucleusItemResourceHandler(InventoryContents inventory, BiPredicate<Integer, ItemStack> slotValidator,
                                      IntConsumer changedCallback) {
        this.inventory = inventory;
        this.slotValidator = slotValidator;
        this.changedCallback = changedCallback;
    }

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
        if (!isValidSlot(index) || !isValid(index, resource))
            return 0;
        return 64;
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        return resource.isEmpty() || isValidSlot(index) && slotValidator.test(index, resource.toStack());
    }

    @Override
    public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (resource.isEmpty() || amount <= 0 || !isValid(index, resource))
            return 0;

        ItemStack existing = getStack(index);
        if (!existing.isEmpty() && !resource.matches(existing))
            return 0;

        int limit = Math.min(amount, (int) getCapacityAsLong(index, resource));
        if (!existing.isEmpty())
            limit -= existing.getCount();
        int inserted = Math.max(0, limit);
        if (inserted > 0) {
            inventoryJournal.updateSnapshots(transaction);
            if (existing.isEmpty()) {
                inventory.inventory.set(index, resource.toStack(inserted));
            } else {
                existing.grow(inserted);
            }
            changedCallback.accept(index);
        }
        return inserted;
    }

    @Override
    public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
        ItemStack existing = getStack(index);
        if (resource.isEmpty() || amount <= 0 || existing.isEmpty() || !resource.matches(existing))
            return 0;

        int extracted = Math.min(amount, existing.getCount());
        if (extracted > 0) {
            inventoryJournal.updateSnapshots(transaction);
            if (existing.getCount() <= extracted) {
                inventory.inventory.set(index, ItemStack.EMPTY);
            } else {
                existing.shrink(extracted);
            }
            changedCallback.accept(index);
        }
        return extracted;
    }

    public ItemStack getStack(int index) {
        return isValidSlot(index) ? inventory.inventory.get(index) : ItemStack.EMPTY;
    }

    public void setStack(int index, ItemStack stack) {
        if (isValidSlot(index)) {
            inventory.inventory.set(index, stack);
            changedCallback.accept(index);
        }
    }

    private boolean isValidSlot(int index) {
        return index >= 0 && index < inventory.inventory.size();
    }

    private class InventoryJournal extends SnapshotJournal<NonNullList<ItemStack>> {
        @Override
        protected NonNullList<ItemStack> createSnapshot() {
            NonNullList<ItemStack> snapshot = NonNullList.withSize(inventory.inventory.size(), ItemStack.EMPTY);
            for (int i = 0; i < inventory.inventory.size(); i++)
                snapshot.set(i, inventory.inventory.get(i).copy());
            return snapshot;
        }

        @Override
        protected void revertToSnapshot(NonNullList<ItemStack> snapshot) {
            for (int i = 0; i < inventory.inventory.size(); i++)
                inventory.inventory.set(i, snapshot.get(i).copy());
        }
    }
}
