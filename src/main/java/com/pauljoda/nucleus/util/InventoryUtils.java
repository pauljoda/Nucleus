package com.pauljoda.nucleus.util;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

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
    public static int calcRedstoneFromInventory(ResourceHandler<ItemResource> inventory) {
        if (inventory == null)
            return 0;

        int i = 0;
        float f = 0.0F;
        for (int j = 0; j < inventory.size(); j++) {
            ItemResource resource = inventory.getResource(j);
            if (!resource.isEmpty()) {
                f += (float) inventory.getAmountAsLong(j) / resource.toStack().getMaxStackSize();
                i += 1;
            }
        }

        f = f / inventory.size();
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
     * Used to move items from one inventory to another through NeoForge transfer handlers.
     *
     * @param source    The source inventory block entity
     * @param fromSlot  The from slot, -1 for any
     * @param target    The target inventory block entity
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

        ResourceHandler<ItemResource> fromHandler = getItemResourceHandler(source, dir.getOpposite(), checkSidedSource);
        ResourceHandler<ItemResource> toHandler = getItemResourceHandler(target, dir, checkSidedTarget);
        if (fromHandler == null || toHandler == null)
            return false;
        return moveItemResource(fromHandler, fromSlot, toHandler, intoSlot, maxAmount, doMove);
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
