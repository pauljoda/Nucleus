package com.pauljoda.nucleus.common.items;

import com.pauljoda.nucleus.capabilities.energy.EnergyBank;
import com.pauljoda.nucleus.common.components.EnergyStorageComponent;
import com.pauljoda.nucleus.registration.NucleusDataComponents;
import net.minecraft.world.item.ItemStack;
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
public abstract class EnergyContainingItem implements EnergyHandler {
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
     * EnergyHandler                                                                                                   *
     *******************************************************************************************************************/

    @Override
    public long getAmountAsLong() {
        loadLocalEnergy();
        return localEnergy.getAmountAsLong();
    }

    @Override
    public long getCapacityAsLong() {
        loadLocalEnergy();
        return localEnergy.getCapacityAsLong();
    }

    @Override
    public int insert(int amount, TransactionContext transaction) {
        if (amount <= 0 || !canReceive())
            return 0;

        loadLocalEnergy();
        int inserted = Math.min(amount, Math.min(localEnergy.getMaxReceive(), localEnergy.getCapacity() - localEnergy.getEnergy()));
        if (inserted > 0) {
            energyJournal.updateSnapshots(transaction);
            localEnergy.setEnergy(localEnergy.getEnergy() + inserted);
            saveLocalEnergy();
        }
        return inserted;
    }

    @Override
    public int extract(int amount, TransactionContext transaction) {
        if (amount <= 0 || !canExtract())
            return 0;

        loadLocalEnergy();
        int extracted = Math.min(amount, Math.min(localEnergy.getMaxExtract(), localEnergy.getEnergy()));
        if (extracted > 0) {
            energyJournal.updateSnapshots(transaction);
            localEnergy.setEnergy(localEnergy.getEnergy() - extracted);
            saveLocalEnergy();
        }
        return extracted;
    }

    /**
     * Returns if this storage can have energy extracted.
     * If this is false, then any calls to extractEnergy will return 0.
     */
    public boolean canExtract() {
        return localEnergy.canExtract();
    }

    /**
     * Used to determine if this storage can receive energy.
     * If this is false, then any calls to receiveEnergy will return 0.
     */
    public boolean canReceive() {
        return localEnergy.canReceive();
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

}
