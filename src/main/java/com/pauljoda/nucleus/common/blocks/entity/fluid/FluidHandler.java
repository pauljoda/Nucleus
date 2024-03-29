package com.pauljoda.nucleus.common.blocks.entity.fluid;

import com.pauljoda.nucleus.common.blocks.entity.Syncable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;

/**
 * This file was created for Nucleus - Java
 * <p>
 * Nucleus - Java is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/10/2017
 */
public abstract class FluidHandler extends Syncable implements IFluidHandler {

    // NBT Tags
    protected static final String SIZE_NBT_TAG = "Size";
    protected static final String TANK_ID_NBT_TAG = "TankID";
    protected static final String TANKS_NBT_TAG = "Tanks";

    // Tanks
    public FluidTank[] tanks;

    /**
     * Default constructor, calls the setupTanks method to setup the tanks
     */
    public FluidHandler(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        setupTanks();
    }

    /*******************************************************************************************************************
     * Abstract Methods                                                                                                *
     *******************************************************************************************************************/

    /**
     * Used to set up the tanks needed. You can insert any number of tanks
     */
    protected abstract void setupTanks();

    /**
     * Which tanks can input
     *
     * @return An array with the indexes of the input tanks
     */
    protected abstract int[] getInputTanks();

    /**
     * Which tanks can output
     *
     * @return An array with the indexes of the output tanks
     */
    protected abstract int[] getOutputTanks();

    /*******************************************************************************************************************
     * FluidHandler                                                                                                    *
     *******************************************************************************************************************/

    /**
     * Called when something happens to the tank, you should mark the block for update here if a tile
     */
    public void onTankChanged(FluidTank tank) {
        markForUpdate(3);
    }

    /**
     * Used to convert a number of buckets into MB
     *
     * @param buckets How many buckets
     * @return The amount of buckets in MB
     */
    public int bucketsToMB(int buckets) {
        return FluidType.BUCKET_VOLUME * buckets;
    }

    /**
     * Returns true if the given fluid can be inserted
     * <p>
     * More formally, this should return true if fluid is able to enter
     */
    protected boolean canFill(Fluid fluid) {
        for (Integer x : getInputTanks()) {
            if (x < tanks.length)
                if ((tanks[x].getFluid().isEmpty() || tanks[x].getFluid().getFluid() == null) ||
                        (tanks[x].getFluid().isEmpty() && tanks[x].getFluid().getFluid() == fluid))
                    return true;
        }
        return false;
    }

    /**
     * Returns true if the given fluid can be extracted
     * <p>
     * More formally, this should return true if fluid is able to leave
     */
    protected boolean canDrain(Fluid fluid) {
        for (Integer x : getOutputTanks()) {
            if (x < tanks.length)
                if (!tanks[x].getFluid().isEmpty() && tanks[x].getFluid().getFluid() != Fluids.EMPTY)
                    return true;
        }
        return false;
    }

    /**
     * Returns the fluid handler for the object.
     *
     * @return The fluid handler.
     */
    public IFluidHandler getFluidHandler() {
        return this;
    }

    /*******************************************************************************************************************
     * Tile Methods                                                                                                    *
     *******************************************************************************************************************/

    /**
     * Used to save the object to an NBT tag
     *
     * @param compound The tag to save to
     */
    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        int id = 0;
        compound.putInt(SIZE_NBT_TAG, tanks.length);
        ListTag tagList = new ListTag();
        for (FluidTank tank : tanks) {
            if (tank != null) {
                CompoundTag tankCompound = new CompoundTag();
                tankCompound.putByte(TANK_ID_NBT_TAG, (byte) id);
                id += 1;
                tank.writeToNBT(tankCompound);
                tagList.add(tankCompound);
            }
        }
        compound.put(TANKS_NBT_TAG, tagList);
    }

    /**
     * Used to read from an NBT tag
     *
     * @param compound The tag to read from
     */
    @Override
    public void saveAdditional(@Nonnull CompoundTag compound) {
        super.saveAdditional(compound);
        ListTag tagList = compound.getList(TANKS_NBT_TAG, 10);
        int size = compound.getInt(SIZE_NBT_TAG);
        if (size != tanks.length && compound.contains(SIZE_NBT_TAG)) tanks = new FluidTank[size];
        for (int x = 0; x < tagList.size(); x++) {
            CompoundTag tankCompound = tagList.getCompound(x);
            byte position = tankCompound.getByte(TANK_ID_NBT_TAG);
            if (position < tanks.length)
                tanks[position].readFromNBT(tankCompound);
        }
    }

    /*******************************************************************************************************************
     * IFluidHandler                                                                                                   *
     *******************************************************************************************************************/

    /**
     * Returns the number of fluid storage units ("tanks") available
     *
     * @return The number of tanks available
     */
    @Override
    public int getTanks() {
        return tanks.length;
    }

    /**
     * Returns the FluidStack in a given tank.
     *
     * <p>
     * <strong>IMPORTANT:</strong> This FluidStack <em>MUST NOT</em> be modified. This method is not for
     * altering internal contents. Any implementers who are able to detect modification via this method
     * should throw an exception. It is ENTIRELY reasonable and likely that the stack returned here will be a copy.
     * </p>
     *
     * <p>
     * <strong><em>SERIOUSLY: DO NOT MODIFY THE RETURNED FLUIDSTACK</em></strong>
     * </p>
     *
     * @param tank Tank to query.
     * @return FluidStack in a given tank. FluidStack.EMPTY if the tank is empty.
     */
    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        FluidStack fluidStack = FluidStack.EMPTY.copy();
        if (tank < tanks.length && tank >= 0)
            return tanks[tank].getFluid().copy(); // Others should never modify, copy to prevent happening
        return fluidStack;
    }

    /**
     * Retrieves the maximum fluid amount for a given tank.
     *
     * @param tank Tank to query.
     * @return The maximum fluid amount held by the tank.
     */
    @Override
    public int getTankCapacity(int tank) {
        return tank < tanks.length && tank >= 0 ?
                tanks[tank].getCapacity()
                : 0;
    }

    /**
     * This function is a way to determine which fluids can exist inside a given handler. General purpose tanks will
     * basically always return TRUE for this.
     *
     * @param tank  Tank to query for validity
     * @param stack Stack to test with for validity
     * @return TRUE if the tank can hold the FluidStack, not considering current state.
     * (Basically, is a given fluid EVER allowed in this tank?) Return FALSE if the answer to that question is 'no.'
     */
    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return tank < tanks.length && tank >= 0 && tanks[tank].isFluidValid(stack); // First two checks prevent third
    }

    /**
     * Fills fluid into internal tanks, distribution is left entirely to the IFluidHandler.
     *
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be filled.
     * @param action   If false, fill will only be simulated.
     * @return Amount of resource that was (or would have been, if simulated) filled.
     */
    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (!resource.isEmpty() && resource.getFluid() != Fluids.EMPTY && canFill(resource.getFluid())) {
            for (Integer x : getInputTanks()) {
                if (x < tanks.length) {
                    if (tanks[x].fill(resource, action) > 0) {
                        int actual = tanks[x].fill(resource, action);
                        if (action.execute()) onTankChanged(tanks[x]);
                        return actual;
                    }
                }
            }
        }
        return 0;
    }

    /**
     * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
     * <p>
     * This method is not Fluid-sensitive.
     *
     * @param maxDrain Maximum amount of fluid to drain.
     * @param doDrain  If false, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction doDrain) {
        FluidStack fluidStack = FluidStack.EMPTY.copy();
        for (Integer x : getOutputTanks()) {
            if (x < tanks.length) {
                fluidStack = tanks[x].drain(maxDrain, doDrain);
                if (!fluidStack.isEmpty()) {
                    tanks[x].drain(maxDrain, doDrain);
                    if (doDrain.execute()) onTankChanged(tanks[x]);
                    return fluidStack;
                }
            }
        }
        return fluidStack;
    }

    /**
     * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
     *
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be drained.
     * @param doDrain  If false, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction doDrain) {
        return drain(resource.getAmount(), doDrain);
    }
}
