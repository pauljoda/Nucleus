package com.pauljoda.nucleus.util;

import com.pauljoda.nucleus.common.blocks.IToolable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * This file was created for Nucleus - Java
 * <p>
 * Nucleus - Java is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/7/2017
 */
public class LevelUtils {

    /*******************************************************************************************************************
     * World Methods                                                                                                   *
     *******************************************************************************************************************/

    /**
     * Returns the direction to the left of this. This is the direction it is facing turned left
     *
     * @param toTurn Starting point
     * @return The direction turned 90 left
     */
    public static Direction rotateLeft(Direction toTurn) {
        switch (toTurn) {
            case NORTH:
                return Direction.WEST;
            case EAST:
                return Direction.NORTH;
            case SOUTH:
                return Direction.EAST;
            case WEST:
                return Direction.SOUTH;
            case UP: // No rotation on y axis
            case DOWN:
            default:
                return toTurn;
        }
    }

    /**
     * Returns the direction to the right of this. This is the direction it is facing turned right
     *
     * @param toTurn Starting point
     * @return The direction turned 90 right
     */
    public static Direction rotateRight(Direction toTurn) {
        switch (toTurn) {
            case NORTH:
                return Direction.EAST;
            case EAST:
                return Direction.SOUTH;
            case SOUTH:
                return Direction.WEST;
            case WEST:
                return Direction.NORTH;
            case UP: // No rotation on y axis
            case DOWN:
            default:
                return toTurn;
        }
    }

    /**
     * Gets a list of all capabilities that touch a BlockPos. This will search for tile
     * entities touching the BlockPos and then query them for access to their capabilities.
     *
     * @param capability The capability you want to retrieve.
     * @param level      The world that this is happening in.
     * @param pos        The position to search around.
     * @return A list of all capabilities that are being held by connected blocks.
     */
    public static <T, C> List<T> getConnectedCapabilities(BlockCapability<T, C> capability, Level level, BlockPos pos) {

        final List<T> capabilities = new ArrayList<>();

        for (final Direction side : Direction.values()) {

            final BlockEntity tile = level.getBlockEntity(pos.relative(side));

            if (tile != null &&
                    !tile.isRemoved() &&
                    CapabilityUtils.getBlockCapability(level, capability, tile.getBlockPos(), (C) side.getOpposite()) != null)
                capabilities.add(CapabilityUtils.getBlockCapability(level, capability, tile.getBlockPos(), (C) side.getOpposite()));
        }

        return capabilities;
    }

    /**
     * Checks if there is air within the given list
     *
     * @param world  The world
     * @param blocks The list of offsets from the origin
     * @param origin The origin to compare against
     * @return True if any block in the list has air
     */
    public static boolean doesContainAirBlock(Level world, List<BlockPos> blocks, BlockPos origin) {
        for (BlockPos pos : blocks)
            if (world.isEmptyBlock(new BlockPos(
                    origin.getX() + pos.getX(),
                    origin.getY() + pos.getY(),
                    origin.getZ() + pos.getZ())))
                return true;
        return false;
    }

    /**
     * Gets all BlockPos between two specified BlockPos, with options to include the inside and/or outside edges.
     *
     * @param first          The first BlockPos
     * @param second         The second BlockPos
     * @param includeInside  Whether to include the inside edges
     * @param includeOutside Whether to include the outside edges
     * @return A list of BlockPos between the first and second positions
     */
    public static List<BlockPos> getAllBetween(BlockPos first, BlockPos second, boolean includeInside, boolean includeOutside) {
        var locations = new ArrayList<BlockPos>();

        // Get direction offset (positive or negative)
        var xDirMultiplier = Integer.signum(second.getX() - first.getX());
        var yDirMultiplier = Integer.signum(second.getY() - first.getY());
        var zDirMultiplier = Integer.signum(second.getZ() - first.getZ());

        // Get depths
        var xDepth = Math.abs(second.getX() - first.getX());
        var yDepth = Math.abs(second.getY() - first.getY());
        var zDepth = Math.abs(second.getZ() - first.getZ());

        // Build List
        for (int x = 0; x <= xDepth; x++) {
            for (int y = 0; y <= yDepth; y++) {
                for (int z = 0; z <= zDepth; z++) {
                    var xa = first.getX() + (xDirMultiplier * x);
                    var ya = first.getY() + (yDirMultiplier * y);
                    var za = first.getZ() + (zDirMultiplier * z);

                    if (x == 0 || x == xDepth ||
                            y == 0 || y == yDepth ||
                            z == 0 || z == zDepth) {
                        if (includeOutside)
                            locations.add(new BlockPos(xa, ya, za));
                    } else if (includeInside)
                        locations.add(new BlockPos(xa, ya, za));
                }
            }
        }

        return locations;
    }

    /**
     * Returns a tuple containing two lists of BlockPos: one list for the blocks on the outside edges,
     * and one list for the blocks on the inside edges, between the given first and second BlockPos.
     *
     * @param first  The first BlockPos
     * @param second The second BlockPos
     * @return A tuple containing two lists of BlockPos: one list for the inside edges, and one list for the outside edges
     */
    public static Tuple<List<BlockPos>, List<BlockPos>> getAllBetweenTogether(BlockPos first, BlockPos second) {
        var inside = new ArrayList<BlockPos>();
        var outside = new ArrayList<BlockPos>();

        // Get direction offset (positive or negative)
        var xDirMultiplier = Integer.signum(second.getX() - first.getX());
        var yDirMultiplier = Integer.signum(second.getY() - first.getY());
        var zDirMultiplier = Integer.signum(second.getZ() - first.getZ());

        // Get depths
        var xDepth = Math.abs(second.getX() - first.getX());
        var yDepth = Math.abs(second.getY() - first.getY());
        var zDepth = Math.abs(second.getZ() - first.getZ());

        // Build List
        for (int x = 0; x <= xDepth; x++) {
            for (int y = 0; y <= yDepth; y++) {
                for (int z = 0; z <= zDepth; z++) {
                    var xa = first.getX() + (xDirMultiplier * x);
                    var ya = first.getY() + (yDirMultiplier * y);
                    var za = first.getZ() + (zDirMultiplier * z);

                    if (x == 0 || x == xDepth ||
                            y == 0 || y == yDepth ||
                            z == 0 || z == zDepth) {
                        outside.add(new BlockPos(xa, ya, za));
                    } else
                        inside.add(new BlockPos(xa, ya, za));
                }
            }
        }

        return new Tuple<>(inside, outside);
    }


    /*******************************************************************************************************************
     * Spawning Methods                                                                                                *
     *******************************************************************************************************************/

    /**
     * Drops and Array of ItemStacks into the world
     *
     * @param world  Instance of ``World
     * @param stacks ItemStack Array to drop into the world
     * @param pos    BlockPos to drop them from
     */
    public static void dropStacks(Level world, List<ItemStack> stacks, BlockPos pos) {
        stacks.forEach((ItemStack stack) -> dropStack(world, stack, pos));
    }

    /**
     * Drops a ItemStack into the world
     *
     * @param world Instance of ``World
     * @param stack temStack Array to drop into the world
     * @param pos   BlockPos to drop them from
     */
    public static void dropStack(Level world, ItemStack stack, BlockPos pos) {
        if (stack != null && stack.getCount() > 0) {
            float rx = world.getRandom().nextFloat() * 0.8F;
            float ry = world.getRandom().nextFloat() * 0.8F;
            float rz = world.getRandom().nextFloat() * 0.8F;

            ItemEntity itemEntity = new ItemEntity(world,
                    pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz,
                    stack.copy());

            float factor = 0.05F;

            itemEntity.setDeltaMovement(
                    world.getRandom().nextGaussian() * factor,
                    world.getRandom().nextGaussian() * factor + 0.2F,
                    world.getRandom().nextGaussian() * factor
            );

            world.addFreshEntity(itemEntity);

            stack.setCount(0);
        }
    }

    /**
     * Helper method to drop items in an inventory, used on break mostly
     *
     * @param itemHandler The itemhandler
     * @param world       The world
     * @param pos         The block pos
     */
    public static void dropStacksInInventory(IItemHandler itemHandler, Level world, BlockPos pos) {
        for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
            ItemStack stack = itemHandler.getStackInSlot(slot);
            if (!stack.isEmpty())
                dropStack(world, stack, pos);
        }
    }

    /*******************************************************************************************************************
     * IToolable Helpers                                                                                               *
     *******************************************************************************************************************/

    /**
     * Breaks the block and saves the NBT to the tag, calls getStackDropped to drop (just item)
     *
     * @param world The world
     * @param pos   The block pos
     * @param block The block object
     * @return True if successful
     */
    public static boolean breakBlockSavingNBT(Level world, BlockPos pos, @Nonnull IToolable block) {
        if (world.isClientSide()) return false;
        if (world.getBlockEntity(pos) != null) {
            BlockEntity savableTile = world.getBlockEntity(pos);
            CompoundTag tag = savableTile.saveCustomOnly(world.registryAccess());
            ItemStack stack = block.getStackDroppedByWrench(world, pos);
            stack.set(DataComponents.BLOCK_ENTITY_DATA, TypedEntityData.of(savableTile.getType(), tag));
            dropStack(world, stack, pos);
            world.removeBlockEntity(pos); // Cancel drop logic
            world.removeBlock(pos, false);
            return true;
        }
        return false;
    }

    /**
     * Call this after onBlockPlacedBy to write saved data to the stack if present
     *
     * @param world The world
     * @param pos   The block position
     * @param stack The stack that had the tag
     */
    public static void writeStackNBTToBlock(Level world, BlockPos pos, ItemStack stack) {
        if (stack.has(DataComponents.BLOCK_ENTITY_DATA) && world.getBlockEntity(pos) != null) {
            BlockEntity tile = world.getBlockEntity(pos);
            stack.get(DataComponents.BLOCK_ENTITY_DATA).loadInto(tile, world.registryAccess());
        }
    }
}
