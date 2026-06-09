package com.pauljoda.nucleus.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pauljoda.nucleus.capabilities.energy.EnergyBank;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record EnergyStorageComponent(int energyStored, int capacity, int maxInsert, int maxExtract) {
    public static final Codec<EnergyStorageComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("EnergyStored", 0).forGetter(EnergyStorageComponent::energyStored),
            Codec.INT.optionalFieldOf("Capacity", 0).forGetter(EnergyStorageComponent::capacity),
            Codec.INT.optionalFieldOf("MaxInsert", 0).forGetter(EnergyStorageComponent::maxInsert),
            Codec.INT.optionalFieldOf("MaxExtract", 0).forGetter(EnergyStorageComponent::maxExtract)
    ).apply(instance, EnergyStorageComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnergyStorageComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, EnergyStorageComponent::energyStored,
            ByteBufCodecs.VAR_INT, EnergyStorageComponent::capacity,
            ByteBufCodecs.VAR_INT, EnergyStorageComponent::maxInsert,
            ByteBufCodecs.VAR_INT, EnergyStorageComponent::maxExtract,
            EnergyStorageComponent::new);

    public static EnergyStorageComponent from(EnergyBank bank) {
        return new EnergyStorageComponent(bank.getEnergy(), bank.getCapacity(), bank.getMaxReceive(), bank.getMaxExtract());
    }

    public void loadInto(EnergyBank bank) {
        bank.setEnergy(energyStored);
        bank.setCapacity(capacity);
        bank.setMaxReceive(maxInsert);
        bank.setMaxExtract(maxExtract);
    }
}
