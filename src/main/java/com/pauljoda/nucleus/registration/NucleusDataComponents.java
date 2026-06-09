package com.pauljoda.nucleus.registration;

import com.pauljoda.nucleus.Nucleus;
import com.pauljoda.nucleus.common.components.EnergyStorageComponent;
import com.pauljoda.nucleus.common.components.ItemInventoryComponent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NucleusDataComponents {
    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Nucleus.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EnergyStorageComponent>> ITEM_ENERGY =
            COMPONENTS.registerComponentType("item_energy", builder -> builder
                    .persistent(EnergyStorageComponent.CODEC)
                    .networkSynchronized(EnergyStorageComponent.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemInventoryComponent>> ITEM_INVENTORY =
            COMPONENTS.registerComponentType("item_inventory", builder -> builder
                    .persistent(ItemInventoryComponent.CODEC)
                    .networkSynchronized(ItemInventoryComponent.STREAM_CODEC));
}
