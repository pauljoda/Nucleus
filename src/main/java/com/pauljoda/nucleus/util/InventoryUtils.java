package com.pauljoda.nucleus.util;

import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * This file was created for Nucleus - Java
 * <p>
 * Nucleus - Java is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * <a href="http://creativecommons.org/licenses/by-nc-sa/4.0/">License</a>
 *
 * @author Paul Davis - pauljoda
 * @since 2/6/2017
 */
public class InventoryUtils {

    /**
     * Calculates the redstone signal to output based on how full an inventory is
     *
     * @param inventory The inventory
     * @return Redstone strength
     */
    public static int calcRedstoneFromInventory(IItemHandler inventory) {
        if (inventory == null)
            return 0;

        int i = 0;
        float f = 0.0F;
        for (int j = 0; j < inventory.getSlots(); j++) {
            ItemStack stack = inventory.getStackInSlot(j);
            if (!stack.isEmpty()) {
                f += stack.getCount() / stack.getMaxStackSize();
                i += 1;
            }
        }

        f = f / inventory.getSlots();
        return Math.floor(f * 14F) + i > 0 ? 1 : 0;
    }

    /**
     * Checks if the two stacks can merge
     *
     * @param stackOne The first stack
     * @param stackTwo The second stack
     * @return Can stacks merge
     */
    public static boolean canStacksMerge(ItemStack stackOne, ItemStack stackTwo) {
        return !(stackOne.isEmpty() || stackTwo.isEmpty()) && stackOne.getItem() == stackTwo.getItem() &&
                ItemStack.isSameItemSameComponents(stackOne, stackTwo);
    }

    /**
     * Tries to merge the two stacks
     *
     * @param stackToMerge The stack to merge
     * @param stackInSlot  The stack to merge into
     * @return True if merged at all
     */
    public static boolean tryMergeStacks(ItemStack stackToMerge, ItemStack stackInSlot) {
        if (stackInSlot.isEmpty() || !(stackInSlot.getItem() == stackToMerge.getItem()) ||
                !ItemStack.isSameItemSameComponents(stackToMerge, stackInSlot))
            return false;

        int newStackSize = stackInSlot.getCount() + stackToMerge.getCount();
        int maxStackSize = stackToMerge.getMaxStackSize();

        if (newStackSize <= maxStackSize) {
            stackToMerge.setCount(0);
            stackInSlot.setCount(newStackSize);
            return true;
        } else if (stackInSlot.getCount() < maxStackSize) {
            stackToMerge.setCount(stackToMerge.getCount() - maxStackSize - stackInSlot.getCount());
            stackInSlot.setCount(maxStackSize);
            return true;
        } else
            return false;
    }

    /**
     * Used to move items from one inventory to another. You can wrap it if you have to
     * but since you'll be calling from us, there shouldn't be an issue
     *
     * @param source    The source inventory, can be IInventory, ISideInventory, or preferably IItemHandler
     * @param fromSlot  The from slot, -1 for any
     * @param target    The target inventory, can be IInventory, ISideInventory, or preferably IItemHandler
     * @param intoSlot  The slot to move into the target, -1 for any
     * @param maxAmount The max amount to move/extract
     * @param dir       The direction moving into, so the face of the fromInventory
     * @param doMove    True to actually do the move, false to simulate
     * @return True if something was moved
     */
    public static boolean moveItemInto(Object source, int fromSlot, Object target, int intoSlot,
                                       int maxAmount, Direction dir, boolean doMove, boolean checkSidedSource,
                                       boolean checkSidedTarget) {
        // Null Checks
        if (source == null || target == null)
            return false;

        if ((checkSidedSource && source instanceof BlockEntity) || (checkSidedTarget && target instanceof BlockEntity)) {
            ResourceHandler<ItemResource> fromHandler = getItemResourceHandler(source, dir.getOpposite(), checkSidedSource);
            ResourceHandler<ItemResource> toHandler = getItemResourceHandler(target, dir, checkSidedTarget);
            if (fromHandler == null || toHandler == null)
                return false;
            return moveItemResource(fromHandler, fromSlot, toHandler, intoSlot, maxAmount, doMove);
        }

        // Object to hold source
        IItemHandler fromInventory;

        // If source is not an item handler, attempt to cast
        if (!(source instanceof IItemHandler)) {
            if (source instanceof WorldlyContainer) {
                fromInventory = new SidedInvWrapper((WorldlyContainer) source, dir.getOpposite()); // Wrap sided
            } else
                return false; // Not valid
        } else {
            // Cast item handlers
            fromInventory = (IItemHandler) source;
        }

        IItemHandler targetInventory;

        // If sink is not an item handler, attempt to cast
        if (!(target instanceof IItemHandler)) {
            if (target instanceof WorldlyContainer) {
                targetInventory = new SidedInvWrapper((WorldlyContainer) target, dir); // Wrap sided
            } else
                return false; // Not valid
        } else {
            // Cast item handlers
            targetInventory = (IItemHandler) target;
        }

        // Load slots
        List<Integer> fromSlots = new ArrayList<>();
        List<Integer> toSlots = new ArrayList<>();

        // Add From Slots
        if (fromSlot != -1)
            fromSlots.add(fromSlot);
        else
            for (int x = 0; x < fromInventory.getSlots(); x++)
                fromSlots.add(x);

        // Add to slots
        if (intoSlot != -1)
            toSlots.add(intoSlot);
        else
            for (int x = 0; x < targetInventory.getSlots(); x++)
                toSlots.add(x);

        // Do actual movement
        for (Integer fromSlot1 : fromSlots) { // Cycle fromInventory
            if (!fromInventory.getStackInSlot(fromSlot1).isEmpty()) { // If we have something
                ItemStack fromStack = fromInventory.extractItem(fromSlot1, maxAmount, true); // Simulate to get stack
                if (!fromStack.isEmpty()) { // Make sure we got something
                    for (Integer toSlot : toSlots) { // Cycle to inventory
                        int slotID = toSlot; // Grab slot
                        ItemStack movedStack =
                                targetInventory.insertItem(slotID, fromStack.copy(), !doMove); // Try insert
                        if (!ItemStack.matches(fromStack, movedStack)) { // If a change was made to the stack
                            fromInventory.extractItem(fromSlot1,
                                    (!movedStack.isEmpty()) ? fromStack.getCount() - movedStack.getCount() : maxAmount,
                                    !doMove); // Extract from original
                            return true; // Exit
                        }
                    }
                }
            }
        }
        return false; // Failed to move something
    }

    private static ResourceHandler<ItemResource> getItemResourceHandler(Object source, Direction side, boolean sided) {
        if (source instanceof BlockEntity tile) {
            if (tile.getLevel() == null)
                return null;
            return tile.getLevel().getCapability(Capabilities.Item.BLOCK, tile.getBlockPos(), sided ? side : null);
        }
        return null;
    }

    private static boolean moveItemResource(ResourceHandler<ItemResource> source, int fromSlot,
                                            ResourceHandler<ItemResource> target, int intoSlot,
                                            int maxAmount, boolean doMove) {
        List<Integer> fromSlots = new ArrayList<>();
        List<Integer> toSlots = new ArrayList<>();

        if (fromSlot != -1)
            fromSlots.add(fromSlot);
        else
            for (int slot = 0; slot < source.size(); slot++)
                fromSlots.add(slot);

        if (intoSlot != -1)
            toSlots.add(intoSlot);
        else
            for (int slot = 0; slot < target.size(); slot++)
                toSlots.add(slot);

        for (int sourceSlot : fromSlots) {
            ItemResource resource = source.getResource(sourceSlot);
            if (resource.isEmpty())
                continue;

            int amount = Math.min(maxAmount, source.getAmountAsInt(sourceSlot));
            if (amount <= 0)
                continue;

            for (int targetSlot : toSlots) {
                try (Transaction transaction = Transaction.openRoot()) {
                    int extracted = source.extract(sourceSlot, resource, amount, transaction);
                    int inserted = target.insert(targetSlot, resource, extracted, transaction);
                    if (inserted > 0) {
                        if (!doMove)
                            return true;
                        transaction.commit();
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
