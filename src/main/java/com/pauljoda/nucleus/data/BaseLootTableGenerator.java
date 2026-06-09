package com.pauljoda.nucleus.data;


import com.pauljoda.nucleus.data.loot.CopyBlockEntityDataFunction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.ContainerComponentManipulators;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetContainerContents;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

/**
 * This class is responsible for generating loot tables for different types of blocks
 * in the game.
 * <p>
 * The class extends the functionality provided by VanillaBlockLoot to define custom
 * loot tables for certain blocks.
 *
 * @author Paul Davis - pauljoda
 * @since 6/7/2022
 */
public abstract class BaseLootTableGenerator extends VanillaBlockLoot {

    public BaseLootTableGenerator(HolderLookup.Provider registries) {
        super(registries);
    }

    /**
     * This method creates a standard loot table for a given block with a specific block entity type.
     *
     * @param block The block for which the loot table is to be created.
     */
    protected void createSimpleTable(Block block) {
        LootPoolSingletonContainer.Builder<?> lti = LootItem.lootTableItem(block);

        // Create a loot pool that rolls once and add the loot item to it.
        LootPool.Builder builder = LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(lti);

        // Add the loot pool to the loot table of the block.
        add(block, LootTable.lootTable().withPool(builder));
    }

    /**
     * This method creates a standard loot table for a given block with a specific block entity type.
     *
     * @param block The block for which the loot table is to be created.
     * @param type  The type of the block entity.
     * @param tags  An array of tags that is copy data from the block entity NBT data.
     */
    protected void createStandardTable(Block block, BlockEntityType<?> type, String... tags) {
        LootPoolSingletonContainer.Builder<?> lti = LootItem.lootTableItem(block);

        lti.apply(CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
                .include(DataComponents.CUSTOM_NAME)
                .include(DataComponents.LOCK)
                .include(DataComponents.CONTAINER_LOOT));

        if (tags.length > 0) {
            lti.apply(CopyBlockEntityDataFunction.copyBlockEntityData(type, tags));
        }

        // Add contents to the loot item from the block entity.
        lti.apply(SetContainerContents.setContents(ContainerComponentManipulators.CONTAINER).withEntry(DynamicLoot.dynamicEntry(Identifier.fromNamespaceAndPath("minecraft", "contents"))));

        // Create a loot pool that rolls once and add the loot item to it.
        LootPool.Builder builder = LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(lti);

        // Add the loot pool to the loot table of the block.
        add(block, LootTable.lootTable().withPool(builder));
    }
}
