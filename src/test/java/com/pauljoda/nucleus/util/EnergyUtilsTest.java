package com.pauljoda.nucleus.util;

import com.pauljoda.nucleus.capabilities.energy.EnergyBank;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnergyUtilsTest {
    @Test
    void transferPowerSimulateRollsBackBothHandlers() {
        EnergyBank source = new EnergyBank(100, 50, 50, 80);
        EnergyBank destination = new EnergyBank(100, 50, 50, 10);

        assertEquals(40, EnergyUtils.transferPower(source, destination, 40, true));
        assertEquals(80, source.getEnergy());
        assertEquals(10, destination.getEnergy());
    }

    @Test
    void transferPowerCommitsWhenNotSimulated() {
        EnergyBank source = new EnergyBank(100, 50, 50, 80);
        EnergyBank destination = new EnergyBank(100, 50, 50, 10);

        assertEquals(40, EnergyUtils.transferPower(source, destination, 40, false));
        assertEquals(40, source.getEnergy());
        assertEquals(50, destination.getEnergy());
    }

    @Test
    void transferPowerHandlesNullHandlers() {
        EnergyBank source = new EnergyBank(100, 50, 50, 80);

        assertEquals(0, EnergyUtils.transferPower(source, null, 40, false));
        assertEquals(0, EnergyUtils.transferPower(null, source, 40, false));
        assertEquals(80, source.getEnergy());
    }

    @Test
    void transferPowerRejectsNonPositiveAmounts() {
        EnergyBank source = new EnergyBank(100, 50, 50, 80);
        EnergyBank destination = new EnergyBank(100, 50, 50, 10);

        assertEquals(0, EnergyUtils.transferPower(source, destination, 0, false));
        assertEquals(0, EnergyUtils.transferPower(source, destination, -1, false));
        assertEquals(80, source.getEnergy());
        assertEquals(10, destination.getEnergy());
    }
}
