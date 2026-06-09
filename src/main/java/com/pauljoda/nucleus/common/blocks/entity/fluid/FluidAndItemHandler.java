package com.pauljoda.nucleus.common.blocks.entity.fluid;

import com.pauljoda.nucleus.common.blocks.entity.item.InventoryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import javax.annotation.Nonnull;

/**
 * This file was created for Nucleus
 * <p>
 * Nucleus is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 8/30/20
 */
public abstract class FluidAndItemHandler extends InventoryHandler {

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
    public FluidAndItemHandler(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
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

}
