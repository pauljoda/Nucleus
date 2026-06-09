package com.pauljoda.nucleus.common.blocks.entity.energy;

import com.pauljoda.nucleus.capabilities.energy.EnergyBank;
import com.pauljoda.nucleus.common.blocks.entity.item.InventoryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

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
public abstract class EnergyAndItemHandler extends InventoryHandler {

    // Sync Values
    public static final int UPDATE_ENERGY_ID = 1000;
    public static final int UPDATE_DIFFERENCE_ID = 1001;

    // Energy Storage
    protected final EnergyBank energyStorage;

    // Energy Change Values
    public int lastEnergy, lastDifference, currentDifference = 0;

    /**
     * Main Constructor
     */
    public EnergyAndItemHandler(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
        energyStorage = initializeEnergyStorage();
    }

    /*******************************************************************************************************************
     * Abstract Methods                                                                                                *
     *******************************************************************************************************************/

    /**
     * Used to define the default size of this energy bank
     *
     * @return The default size of the energy bank
     */
    protected abstract int getDefaultEnergyStorageSize();

    /**
     * Initializes the energy storage for the EnergyHandler class.
     * <p>
     * Be sure to override methods for canExtract etc if only wanting to extract vs insert, default
     * implementation will do both
     *
     * @return The initialized EnergyBank object.
     */
    protected abstract EnergyBank initializeEnergyStorage();

    /*******************************************************************************************************************
     * Tile Methods                                                                                                    *
     *******************************************************************************************************************/

    @Override
    public void onServerTick() {
        super.onServerTick();

        // Handle Energy Difference
        currentDifference = energyStorage.getEnergy() - lastEnergy;

        // Update client
        if (currentDifference != lastDifference)
            sendValueToClient(UPDATE_DIFFERENCE_ID, currentDifference);

        // Store for next round
        lastDifference = currentDifference;
        lastEnergy = energyStorage.getEnergy();
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energyStorage.load(input);

        // Check for bad tags
        if (energyStorage.getCapacity() == 0)
            energyStorage.setCapacity(getDefaultEnergyStorageSize());
        if (energyStorage.getMaxReceive() == 0)
            energyStorage.setMaxReceive(getDefaultEnergyStorageSize());
        if (energyStorage.getMaxExtract() == 0)
            energyStorage.setMaxExtract(getDefaultEnergyStorageSize());
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        energyStorage.save(output);
    }

    /**
     * Retrieves the NeoForge 26.1 energy transfer handler for this block entity.
     *
     * @return The energy transfer handler.
     */
    public net.neoforged.neoforge.transfer.energy.EnergyHandler getEnergyResourceHandler() {
        return energyStorage;
    }

    /*******************************************************************************************************************
     * Syncable                                                                                                        *
     *******************************************************************************************************************/

    /**
     * Used to set the value of a field
     *
     * @param id    The field id
     * @param value The value of the field
     */
    @Override
    public void setVariable(int id, double value) {
        switch (id) {
            case UPDATE_ENERGY_ID:
                energyStorage.setEnergy((int) value);
                break;
            case UPDATE_DIFFERENCE_ID:
                currentDifference = (int) value;
                break;
            default:
        }
    }

    /**
     * Used to get the field on the server, this will fetch the server value and overwrite the current
     *
     * @param id The field id
     * @return The value on the server, now set to ourselves
     */
    @Override
    public Double getVariable(int id) {
        switch (id) {
            case UPDATE_ENERGY_ID:
                return (double) energyStorage.getEnergy();
            case UPDATE_DIFFERENCE_ID:
                return (double) currentDifference;
            default:
                return 0.0;
        }
    }
}
