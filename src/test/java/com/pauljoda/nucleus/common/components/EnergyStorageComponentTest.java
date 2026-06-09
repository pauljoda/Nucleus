package com.pauljoda.nucleus.common.components;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.pauljoda.nucleus.capabilities.energy.EnergyBank;
import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnergyStorageComponentTest {
    @Test
    void codecRoundTripsAllFields() {
        EnergyStorageComponent component = new EnergyStorageComponent(42, 100, 12, 8);

        JsonElement encoded = EnergyStorageComponent.CODEC.encodeStart(JsonOps.INSTANCE, component)
                .getOrThrow();
        EnergyStorageComponent decoded = EnergyStorageComponent.CODEC.parse(JsonOps.INSTANCE, encoded)
                .getOrThrow();

        assertEquals(component, decoded);
    }

    @Test
    void streamCodecRoundTripsAllFields() {
        EnergyStorageComponent component = new EnergyStorageComponent(42, 100, 12, 8);
        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.EMPTY);

        EnergyStorageComponent.STREAM_CODEC.encode(buffer, component);
        EnergyStorageComponent decoded = EnergyStorageComponent.STREAM_CODEC.decode(buffer);

        assertEquals(component, decoded);
    }

    @Test
    void componentCopiesBankStateBothDirections() {
        EnergyBank bank = new EnergyBank(100, 20, 15, 40);

        EnergyStorageComponent component = EnergyStorageComponent.from(bank);
        EnergyBank target = new EnergyBank(1);
        component.loadInto(target);

        assertEquals(40, target.getEnergy());
        assertEquals(100, target.getCapacity());
        assertEquals(20, target.getMaxReceive());
        assertEquals(15, target.getMaxExtract());
    }
}
