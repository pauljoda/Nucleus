package com.pauljoda.nucleus.common.blocks.entity.fluid;

import com.pauljoda.nucleus.common.blocks.entity.Syncable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

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
    private final ResourceHandler<FluidResource> fluidResourceHandler =
            new NucleusFluidResourceHandler(() -> tanks, this::getInputTanks, this::getOutputTanks, this::onTankChanged);

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
                        (!tanks[x].getFluid().isEmpty() && tanks[x].getFluid().getFluid() == fluid))
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
                if (!tanks[x].getFluid().isEmpty() && tanks[x].getFluid().getFluid() == fluid)
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

    /**
     * Returns the NeoForge 26.1 transfer handler for this block entity's tanks.
     *
     * @return The first-class fluid resource handler.
     */
    public ResourceHandler<FluidResource> getFluidResourceHandler() {
        return fluidResourceHandler;
    }

    /*******************************************************************************************************************
     * Tile Methods                                                                                                    *
     *******************************************************************************************************************/

    /**
     * Used to read tanks from saved Value I/O data.
     *
     * @param input The saved input data
     */
    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        int size = input.getIntOr(SIZE_NBT_TAG, tanks.length);
        if (size != tanks.length) tanks = new FluidTank[size];
        for (ValueInput tankInput : input.childrenListOrEmpty(TANKS_NBT_TAG)) {
            byte position = tankInput.getByteOr(TANK_ID_NBT_TAG, (byte) -1);
            if (position >= 0 && position < tanks.length && tanks[position] != null)
                tanks[position].deserialize(tankInput);
        }
    }

    /**
     * Used to save tanks to Value I/O data.
     *
     * @param output The output data
     */
    @Override
    protected void saveAdditional(@Nonnull ValueOutput output) {
        super.saveAdditional(output);
        output.putInt(SIZE_NBT_TAG, tanks.length);
        ValueOutput.ValueOutputList tankList = output.childrenList(TANKS_NBT_TAG);
        for (int id = 0; id < tanks.length; id++) {
            FluidTank tank = tanks[id];
            if (tank != null) {
                ValueOutput tankOutput = tankList.addChild();
                tankOutput.putByte(TANK_ID_NBT_TAG, (byte) id);
                tank.serialize(tankOutput);
            }
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
            try (Transaction transaction = Transaction.openRoot()) {
                int inserted = fluidResourceHandler.insert(FluidResource.of(resource), resource.getAmount(), transaction);
                if (action.execute())
                    transaction.commit();
                return inserted;
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
        for (Integer x : getOutputTanks()) {
            if (x < tanks.length) {
                FluidResource resource = fluidResourceHandler.getResource(x);
                if (!resource.isEmpty()) {
                    try (Transaction transaction = Transaction.openRoot()) {
                        int extracted = fluidResourceHandler.extract(x, resource, maxDrain, transaction);
                        if (doDrain.execute())
                            transaction.commit();
                        return extracted == 0 ? FluidStack.EMPTY : resource.toStack(extracted);
                    }
                }
            }
        }
        return FluidStack.EMPTY;
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
        if (resource.isEmpty() || resource.getFluid() == Fluids.EMPTY || !canDrain(resource.getFluid()))
            return FluidStack.EMPTY;

        FluidResource fluidResource = FluidResource.of(resource);
        for (Integer x : getOutputTanks()) {
            if (x < tanks.length && fluidResource.matches(tanks[x].getFluid())) {
                try (Transaction transaction = Transaction.openRoot()) {
                    int extracted = fluidResourceHandler.extract(x, fluidResource, resource.getAmount(), transaction);
                    if (doDrain.execute())
                        transaction.commit();
                    return extracted == 0 ? FluidStack.EMPTY : resource.copyWithAmount(extracted);
                }
            }
        }
        return FluidStack.EMPTY;
    }
}
