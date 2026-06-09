package com.pauljoda.nucleus.common.container.slots;

import com.pauljoda.nucleus.capabilities.item.NucleusItemResourceHandler;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class ResourceSlot extends Slot {
    protected final NucleusItemResourceHandler itemHandler;
    protected final int index;

    public ResourceSlot(NucleusItemResourceHandler itemHandler, int index, int xPosition, int yPosition) {
        super(new SimpleContainer(index + 1), index, xPosition, yPosition);
        this.itemHandler = itemHandler;
        this.index = index;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return itemHandler.isValid(index, ItemResource.of(stack));
    }

    @Override
    public ItemStack getItem() {
        return itemHandler.getStack(index);
    }

    @Override
    public void set(ItemStack stack) {
        itemHandler.setStack(index, stack);
    }

    @Override
    public ItemStack remove(int amount) {
        ItemStack existing = getItem();
        if (existing.isEmpty())
            return ItemStack.EMPTY;

        ItemResource resource = ItemResource.of(existing);
        try (Transaction transaction = Transaction.openRoot()) {
            int extracted = itemHandler.extract(index, resource, amount, transaction);
            transaction.commit();
            return extracted == 0 ? ItemStack.EMPTY : resource.toStack(extracted);
        }
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public boolean mayPickup(Player player) {
        return true;
    }
}
