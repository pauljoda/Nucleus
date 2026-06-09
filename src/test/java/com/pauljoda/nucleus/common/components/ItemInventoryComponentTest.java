package com.pauljoda.nucleus.common.components;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.Unpooled;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemInventoryComponentTest {
    @Test
    void constructorDefensivelyCopiesAndExposesImmutableList() {
        ItemInventoryComponent component = new ItemInventoryComponent(List.of(ItemStack.EMPTY));

        assertThrows(UnsupportedOperationException.class, () -> component.items().add(ItemStack.EMPTY));
        assertEquals(1, component.items().size());
        assertTrue(component.items().getFirst().isEmpty());
    }

    @Test
    void codecRoundTripsEmptyItems() {
        ItemInventoryComponent component = new ItemInventoryComponent(List.of(ItemStack.EMPTY));

        JsonElement encoded = ItemInventoryComponent.CODEC.encodeStart(JsonOps.INSTANCE, component)
                .getOrThrow();
        ItemInventoryComponent decoded = ItemInventoryComponent.CODEC.parse(JsonOps.INSTANCE, encoded)
                .getOrThrow();

        assertEquals(1, decoded.items().size());
        assertTrue(decoded.items().getFirst().isEmpty());
    }

    @Test
    void streamCodecRoundTripsEmptyItems() {
        ItemInventoryComponent component = new ItemInventoryComponent(List.of(ItemStack.EMPTY));
        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.EMPTY);

        ItemInventoryComponent.STREAM_CODEC.encode(buffer, component);
        ItemInventoryComponent decoded = ItemInventoryComponent.STREAM_CODEC.decode(buffer);

        assertEquals(1, decoded.items().size());
        assertTrue(decoded.items().getFirst().isEmpty());
    }

    @Test
    void loadIntoCopiesItemsAndClearsMissingSlots() {
        NonNullList<ItemStack> inventory = NonNullList.withSize(3, ItemStack.EMPTY);
        ItemInventoryComponent component = new ItemInventoryComponent(List.of(ItemStack.EMPTY));

        component.loadInto(inventory);

        assertEquals(3, inventory.size());
        assertTrue(inventory.get(0).isEmpty());
        assertTrue(inventory.get(1).isEmpty());
        assertTrue(inventory.get(2).isEmpty());
    }
}
