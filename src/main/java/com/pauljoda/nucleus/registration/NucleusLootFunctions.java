package com.pauljoda.nucleus.registration;

import com.mojang.serialization.MapCodec;
import com.pauljoda.nucleus.Nucleus;
import com.pauljoda.nucleus.data.loot.CopyBlockEntityDataFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NucleusLootFunctions {
    public static final DeferredRegister<MapCodec<? extends LootItemFunction>> LOOT_FUNCTIONS =
            DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, Nucleus.MODID);

    public static final DeferredHolder<MapCodec<? extends LootItemFunction>, MapCodec<CopyBlockEntityDataFunction>> COPY_BLOCK_ENTITY_DATA =
            LOOT_FUNCTIONS.register("copy_block_entity_data", () -> CopyBlockEntityDataFunction.MAP_CODEC);
}
