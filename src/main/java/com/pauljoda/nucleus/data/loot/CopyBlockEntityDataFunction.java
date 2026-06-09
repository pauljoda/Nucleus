package com.pauljoda.nucleus.data.loot;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.Set;

/**
 * Copies selected block-entity save-data paths into an item stack's typed block-entity data.
 */
public class CopyBlockEntityDataFunction extends LootItemConditionalFunction {
    /**
     * Codec for the registered loot function.
     */
    public static final MapCodec<CopyBlockEntityDataFunction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance)
            .and(instance.group(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE.holderByNameCodec().fieldOf("type").forGetter(function -> function.type),
                    NbtPathArgument.NbtPath.CODEC.listOf().fieldOf("tags").forGetter(function -> function.tags)))
            .apply(instance, CopyBlockEntityDataFunction::new));

    private final Holder<BlockEntityType<?>> type;
    private final List<NbtPathArgument.NbtPath> tags;

    /**
     * Creates a loot function instance from decoded data.
     *
     * @param predicates loot conditions guarding this function
     * @param type       expected block entity type for the dropped block item
     * @param tags       block-entity save-data paths to copy
     */
    public CopyBlockEntityDataFunction(List<LootItemCondition> predicates, Holder<BlockEntityType<?>> type, List<NbtPathArgument.NbtPath> tags) {
        super(predicates);
        this.type = type;
        this.tags = List.copyOf(tags);
    }

    @Override
    public MapCodec<CopyBlockEntityDataFunction> codec() {
        return MAP_CODEC;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.BLOCK_ENTITY);
    }

    @Override
    protected ItemStack run(ItemStack itemStack, LootContext context) {
        BlockEntity blockEntity = context.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity == null || blockEntity.getLevel() == null || this.tags.isEmpty()) {
            return itemStack;
        }

        CompoundTag source = blockEntity.saveWithFullMetadata(blockEntity.getLevel().registryAccess());
        TypedEntityData<BlockEntityType<?>> existingData = itemStack.get(DataComponents.BLOCK_ENTITY_DATA);
        CompoundTag target = existingData != null && existingData.type() == this.type.value()
                ? existingData.copyTagWithoutId()
                : new CompoundTag();
        boolean changed = false;

        for (NbtPathArgument.NbtPath tag : this.tags) {
            try {
                List<Tag> values = tag.get(source);
                if (!values.isEmpty()) {
                    tag.set(target, values.getLast().copy());
                    changed = true;
                }
            } catch (CommandSyntaxException ignored) {
                // Match vanilla copy_custom_data behavior: invalid/missing paths do not fail loot generation.
            }
        }

        if (changed) {
            itemStack.set(DataComponents.BLOCK_ENTITY_DATA, TypedEntityData.of(this.type.value(), target));
        }

        return itemStack;
    }

    /**
     * Creates a builder that copies block-entity save-data paths into stack block-entity data.
     *
     * @param type block entity type for the dropped block item
     * @param tags block-entity save-data paths to copy
     * @return the loot function builder
     */
    public static Builder copyBlockEntityData(BlockEntityType<?> type, String... tags) {
        return new Builder(type, tags);
    }

    /**
     * Builder for {@link CopyBlockEntityDataFunction}.
     */
    public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
        private final Holder<BlockEntityType<?>> type;
        private final List<NbtPathArgument.NbtPath> tags;

        private Builder(BlockEntityType<?> type, String... tags) {
            this.type = BuiltInRegistries.BLOCK_ENTITY_TYPE.wrapAsHolder(type);
            this.tags = List.of(tags).stream()
                    .map(Builder::parsePath)
                    .toList();
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new CopyBlockEntityDataFunction(this.getConditions(), this.type, this.tags);
        }

        private static NbtPathArgument.NbtPath parsePath(String path) {
            try {
                return NbtPathArgument.NbtPath.of(path);
            } catch (CommandSyntaxException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
}
