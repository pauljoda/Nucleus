package com.pauljoda.nucleus.testharness.registration;

import com.pauljoda.nucleus.testharness.NucleusTestHarness;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Development-only blocks used by the Nucleus test harness.
 */
public final class NucleusTestBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(NucleusTestHarness.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(NucleusTestHarness.MODID);

    public static final DeferredBlock<Block> TEST_MARKER_BLOCK = BLOCKS.registerSimpleBlock(
            "test_marker_block",
            properties -> properties.strength(1.5F, 6.0F).sound(SoundType.METAL)
    );

    public static final DeferredItem<BlockItem> TEST_MARKER_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(TEST_MARKER_BLOCK);

    public static void buildCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(TEST_MARKER_BLOCK_ITEM.get(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

    private NucleusTestBlocks() {
    }
}
