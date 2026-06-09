package com.pauljoda.nucleus.common.items;

import com.pauljoda.nucleus.capabilities.energy.EnergyBank;
import com.pauljoda.nucleus.common.components.EnergyStorageComponent;
import com.pauljoda.nucleus.registration.NucleusDataComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;

/**
 * This file was created for Nucleus
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 3/1/2017
 */
public abstract class EnergyContainingItem implements IEnergyStorage, EnergyHandler {
    // Variables
    private final ItemStack heldStack;
    private final EnergyBank localEnergy;
    private final StackEnergyJournal energyJournal = new StackEnergyJournal();

    /**
     * Simplest constructor of EnergyBank
     */
    public EnergyContainingItem(@NotNull ItemStack stack) {
        heldStack = stack;
        localEnergy = initializeEnergyStorage();

        checkStackTag();
    }

    /**
     * Makes sure we always have a valid tag
     */
    @SuppressWarnings("DataFlowIssue")
    protected void checkStackTag() {
        EnergyStorageComponent component = heldStack.get(NucleusDataComponents.ITEM_ENERGY.get());
        if (component != null) {
            component.loadInto(localEnergy);
            return;
        }

        if (heldStack.has(DataComponents.CUSTOM_DATA)) {
            CompoundTag legacyTag = heldStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            localEnergy.load(legacyTag);
            saveLocalEnergy();
            removeLegacyEnergyData(legacyTag);
            return;
        }

        saveLocalEnergy();
    }

    protected void loadLocalEnergy() {
        checkStackTag();
    }

    protected void saveLocalEnergy() {
        heldStack.set(NucleusDataComponents.ITEM_ENERGY.get(), EnergyStorageComponent.from(localEnergy));
    }

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
     * IEnergyStorage                                                                                                  *
     *******************************************************************************************************************/

    /**
     * Adds energy to the storage. Returns quantity of energy that was accepted.
     *
     * @param maxReceive Maximum amount of energy to be inserted.
     * @param simulate   If TRUE, the insertion will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) accepted by the storage.
     */
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        loadLocalEnergy();
        int energyReceived = localEnergy.receiveEnergy(maxReceive, simulate);
        if (!simulate)
            saveLocalEnergy();
        return energyReceived;
    }

    /**
     * Removes energy from the storage. Returns quantity of energy that was removed.
     *
     * @param maxExtract Maximum amount of energy to be extracted.
     * @param simulate   If TRUE, the extraction will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) extracted from the storage.
     */
    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        loadLocalEnergy();
        int extractedEnergy = localEnergy.extractEnergy(maxExtract, simulate);
        if (!simulate)
            saveLocalEnergy();
        return extractedEnergy;
    }

    /**
     * Returns the amount of energy currently stored.
     */
    @Override
    public int getEnergyStored() {
        loadLocalEnergy();
        return localEnergy.getEnergyStored();
    }

    /**
     * Returns the maximum amount of energy that can be stored.
     */
    @Override
    public int getMaxEnergyStored() {
        loadLocalEnergy();
        return localEnergy.getMaxEnergyStored();
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

        loadLocalEnergy();
        int inserted = localEnergy.receiveEnergy(amount, true);
        if (inserted > 0) {
            energyJournal.updateSnapshots(transaction);
            localEnergy.receiveEnergy(inserted, false);
            saveLocalEnergy();
        }
        return inserted;
    }

    @Override
    public int extract(int amount, TransactionContext transaction) {
        if (amount <= 0 || !canExtract())
            return 0;

        loadLocalEnergy();
        int extracted = localEnergy.extractEnergy(amount, true);
        if (extracted > 0) {
            energyJournal.updateSnapshots(transaction);
            localEnergy.extractEnergy(extracted, false);
            saveLocalEnergy();
        }
        return extracted;
    }

    /**
     * Returns if this storage can have energy extracted.
     * If this is false, then any calls to extractEnergy will return 0.
     */
    @Override
    public boolean canExtract() {
        return true;
    }

    /**
     * Used to determine if this storage can receive energy.
     * If this is false, then any calls to receiveEnergy will return 0.
     */
    @Override
    public boolean canReceive() {
        return true;
    }

    private class StackEnergyJournal extends SnapshotJournal<EnergyStorageComponent> {
        @Override
        protected EnergyStorageComponent createSnapshot() {
            return EnergyStorageComponent.from(localEnergy);
        }

        @Override
        protected void revertToSnapshot(EnergyStorageComponent snapshot) {
            snapshot.loadInto(localEnergy);
            saveLocalEnergy();
        }
    }

    private void removeLegacyEnergyData(CompoundTag legacyTag) {
        legacyTag.remove("EnergyStored");
        legacyTag.remove("Capacity");
        legacyTag.remove("MaxInsert");
        legacyTag.remove("MaxExtract");
        if (legacyTag.isEmpty()) {
            heldStack.remove(DataComponents.CUSTOM_DATA);
        } else {
            CustomData.set(DataComponents.CUSTOM_DATA, heldStack, legacyTag);
        }
    }
}
