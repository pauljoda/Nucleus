package com.pauljoda.nucleus.capabilities.energy;

import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnergyBankTest {
    @Test
    void insertAndExtractRespectLimitsAndCommit() {
        EnergyBank bank = new EnergyBank(100, 30, 20, 10);

        try (Transaction transaction = Transaction.openRoot()) {
            assertEquals(30, bank.insert(50, transaction));
            assertEquals(20, bank.extract(20, transaction));
            transaction.commit();
        }

        assertEquals(20, bank.getEnergy());
    }

    @Test
    void uncommittedRootTransactionRollsBackChanges() {
        EnergyBank bank = new EnergyBank(100, 30, 20, 10);

        try (Transaction transaction = Transaction.openRoot()) {
            assertEquals(30, bank.insert(50, transaction));
        }

        assertEquals(10, bank.getEnergy());
    }

    @Test
    void committedChildStillRollsBackWhenRootRollsBack() {
        EnergyBank bank = new EnergyBank(100, 30, 20, 10);

        try (Transaction root = Transaction.openRoot()) {
            try (Transaction child = Transaction.open(root)) {
                assertEquals(30, bank.insert(30, child));
                child.commit();
            }
        }

        assertEquals(10, bank.getEnergy());
    }

    @Test
    void constructorClampsInitialEnergyToCapacity() {
        assertEquals(100, new EnergyBank(100, 30, 20, 500).getEnergy());
        assertEquals(0, new EnergyBank(100, 30, 20, -5).getEnergy());
    }

    @Test
    void insertAndExtractRejectNonPositiveAmounts() {
        EnergyBank bank = new EnergyBank(100, 30, 20, 10);

        try (Transaction transaction = Transaction.openRoot()) {
            assertEquals(0, bank.insert(0, transaction));
            assertEquals(0, bank.insert(-1, transaction));
            assertEquals(0, bank.extract(0, transaction));
            assertEquals(0, bank.extract(-1, transaction));
            transaction.commit();
        }

        assertEquals(10, bank.getEnergy());
    }
}
