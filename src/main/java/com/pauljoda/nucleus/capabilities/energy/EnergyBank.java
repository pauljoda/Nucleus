package com.pauljoda.nucleus.capabilities.energy;

import com.pauljoda.nucleus.common.Savable;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/**
 * This class represents a storage bank for energy.
 */
public class EnergyBank implements IEnergyStorage, net.neoforged.neoforge.transfer.energy.EnergyHandler, Savable {
    private static final String ENERGY_STORED = "EnergyStored";
    private static final String CAPACITY = "Capacity";
    private static final String MAX_EXTRACT = "MaxExtract";
    private static final String MAX_INSERT = "MaxInsert";

    protected int energy;
    protected int capacity;
    protected int maxReceive;
    protected int maxExtract;
    private final EnergyJournal energyJournal = new EnergyJournal();

    /**
     * Constructs a new EnergyBank object with the given capacity.
     *
     * @param capacity the maximum capacity of the energy bank
     */
    public EnergyBank(int capacity) {
        this(capacity, capacity, capacity, 0);
    }

    /**
     * Creates a new instance of EnergyBank with the given capacity and maximum transfer amount.
     * The energy bank will have the maximum transfer amount set for both receiving and extracting energy.
     *
     * @param capacity    the maximum capacity of the energy bank
     * @param maxTransfer the maximum amount of energy that can be transferred in a single operation
     */
    public EnergyBank(int capacity, int maxTransfer) {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    /**
     * Constructs a new EnergyBank with the specified capacity, maximum energy receive, and maximum energy extract.
     * If the initial amount of energy is not specified, it will be set to 0.
     *
     * @param capacity   the maximum capacity of the energy bank
     * @param maxReceive the maximum amount of energy the bank can receive at a time
     * @param maxExtract the maximum amount of energy the bank can extract at a time
     */
    public EnergyBank(int capacity, int maxReceive, int maxExtract) {
        this(capacity, maxReceive, maxExtract, 0);
    }

    /**
     * Constructs an instance of EnergyBank with the given capacity, maximum receive amount, maximum extract amount, and initial energy.
     *
     * @param capacity   the maximum capacity of the energy bank.
     * @param maxReceive the maximum amount of energy the bank can receive at a time.
     * @param maxExtract the maximum amount of energy the bank can extract at a time.
     * @param energy     the initial amount of energy in the bank.
     */
    public EnergyBank(int capacity, int maxReceive, int maxExtract, int energy) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = Math.max(0, Math.min(capacity, energy));
    }

    /**
     * Set the energy value in the bank.
     *
     * @param energy the amount of energy to be set in the bank.
     */
    public void setEnergy(int energy) {
        this.energy = energy;
    }

    /**
     * Get current energy value in the bank.
     *
     * @return current amount of energy in the bank.
     */
    public int getEnergy() {
        return this.energy;
    }

    /**
     * Set the maximum capacity of the bank
     *
     * @param capacity the new maximum capacity
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Gets the current maximum capacity of the bank
     *
     * @return current maximum capacity
     */
    public int getCapacity() {
        return this.capacity;
    }

    /**
     * Set the maximum amount of energy the bank can receive at a time
     *
     * @param maxReceive the new maximum energy the bank can receive at a time
     */
    public void setMaxReceive(int maxReceive) {
        this.maxReceive = maxReceive;
    }

    /**
     * Get the current maximum amount of energy the bank can receive at a time
     *
     * @return current maximum energy the bank can receive at a time
     */
    public int getMaxReceive() {
        return this.maxReceive;
    }

    /**
     * Set the maximum amount of energy the bank can extract at a time
     *
     * @param maxExtract the new maximum energy the bank can extract at a time
     */
    public void setMaxExtract(int maxExtract) {
        this.maxExtract = maxExtract;
    }

    /**
     * Get the current maximum amount of energy the bank can extract at a time
     *
     * @return current maximum energy the bank can extract at a time
     */
    public int getMaxExtract() {
        return this.maxExtract;
    }

    /**
     * Adds energy to the storage. Returns the quantity of energy that was accepted.
     *
     * @param maxReceive the maximum amount of energy to be inserted
     * @param simulate   if true, the insertion will only be simulated
     * @return the amount of energy that was (or would have been, if simulated) accepted by the storage
     */

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive())
            return 0;

        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate)
            energy += energyReceived;
        return energyReceived;
    }

    /**
     * Removes energy from the storage.
     *
     * @param maxExtract Maximum amount of energy to be extracted.
     * @param simulate   If TRUE, the extraction will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) extracted from the storage.
     */
    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract())
            return 0;

        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate)
            energy -= energyExtracted;
        return energyExtracted;
    }

    /**
     * Retrieves the amount of energy currently stored in the EnergyBank object.
     *
     * @return The current amount of energy stored.
     */
    @Override
    public int getEnergyStored() {
        return energy;
    }

    /**
     * Returns the maximum amount of energy that can be stored.
     *
     * @return the maximum amount of energy that can be stored
     */
    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    /**
     * Determines if the energy bank is capable of extracting energy.
     *
     * @return true if the energy bank can extract energy, false otherwise.
     */
    @Override
    public boolean canExtract() {
        return this.maxExtract > 0;
    }

    /**
     * Checks if the energy bank can receive energy.
     *
     * @return true if the energy bank can receive energy, false otherwise
     */
    @Override
    public boolean canReceive() {
        return this.maxReceive > 0;
    }

    /*******************************************************************************************************************
     * EnergyHandler                                                                                                   *
     *******************************************************************************************************************/

    @Override
    public long getAmountAsLong() {
        return getEnergyStored();
    }

    @Override
    public long getCapacityAsLong() {
        return getMaxEnergyStored();
    }

    @Override
    public int insert(int amount, TransactionContext transaction) {
        if (amount <= 0 || !canReceive())
            return 0;

        int inserted = receiveEnergy(amount, true);
        if (inserted > 0) {
            energyJournal.updateSnapshots(transaction);
            receiveEnergy(inserted, false);
        }
        return inserted;
    }

    @Override
    public int extract(int amount, TransactionContext transaction) {
        if (amount <= 0 || !canExtract())
            return 0;

        int extracted = extractEnergy(amount, true);
        if (extracted > 0) {
            energyJournal.updateSnapshots(transaction);
            extractEnergy(extracted, false);
        }
        return extracted;
    }

    private class EnergyJournal extends SnapshotJournal<Integer> {
        @Override
        protected Integer createSnapshot() {
            return energy;
        }

        @Override
        protected void revertToSnapshot(Integer snapshot) {
            energy = snapshot;
        }
    }

    /*******************************************************************************************************************
     * Savable                                                                                                         *
     *******************************************************************************************************************/

    /**
     * Loads the data from the given ValueInput.
     *
     * @param input The input containing the data to be loaded.
     */
    @Override
    public void load(ValueInput input) {
        energy = input.getIntOr(ENERGY_STORED, 0);
        capacity = input.getIntOr(CAPACITY, 0);
        maxReceive = input.getIntOr(MAX_INSERT, 0);
        maxExtract = input.getIntOr(MAX_EXTRACT, 0);
    }

    /**
     * Saves the data of the object into the specified ValueOutput.
     *
     * @param output The output to store the data into.
     */
    @Override
    public void save(ValueOutput output) {
        output.putInt(ENERGY_STORED, energy);
        output.putInt(CAPACITY, capacity);
        output.putInt(MAX_INSERT, maxReceive);
        output.putInt(MAX_EXTRACT, maxExtract);
    }
}
