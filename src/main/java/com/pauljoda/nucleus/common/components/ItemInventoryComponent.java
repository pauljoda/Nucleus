package com.pauljoda.nucleus.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ItemInventoryComponent(List<ItemStack> items) {
    public static final ItemInventoryComponent EMPTY = new ItemInventoryComponent(List.of());

    public static final Codec<ItemInventoryComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.OPTIONAL_CODEC.listOf().optionalFieldOf("items", List.of()).forGetter(ItemInventoryComponent::items)
    ).apply(instance, ItemInventoryComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemInventoryComponent> STREAM_CODEC = ItemStack.OPTIONAL_LIST_STREAM_CODEC.map(
            ItemInventoryComponent::new,
            ItemInventoryComponent::items);

    public ItemInventoryComponent {
        items = List.copyOf(items.stream().map(ItemStack::copy).toList());
    }

    public static ItemInventoryComponent from(NonNullList<ItemStack> inventory) {
        return new ItemInventoryComponent(inventory);
    }

    public void loadInto(NonNullList<ItemStack> inventory) {
        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = slot < items.size() ? items.get(slot).copy() : ItemStack.EMPTY;
            inventory.set(slot, stack);
        }
    }
}
