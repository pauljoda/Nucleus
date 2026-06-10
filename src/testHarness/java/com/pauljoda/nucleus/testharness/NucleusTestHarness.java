package com.pauljoda.nucleus.testharness;

import com.pauljoda.nucleus.testharness.registration.NucleusTestBlocks;
import com.pauljoda.nucleus.testharness.registration.NucleusTestGameTests;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

/**
 * Development-only Nucleus test harness mod.
 *
 * <p>This mod lives in the {@code testHarness} Gradle source set so its blocks,
 * items, and game tests are available to local dev runs but are not packaged in
 * the production Nucleus jar.</p>
 */
@Mod(NucleusTestHarness.MODID)
public final class NucleusTestHarness {
    public static final String MODID = "nucleus_test";

    public NucleusTestHarness(IEventBus modEventBus) {
        NucleusTestBlocks.BLOCKS.register(modEventBus);
        NucleusTestBlocks.ITEMS.register(modEventBus);
        NucleusTestGameTests.TEST_FUNCTIONS.register(modEventBus);
        modEventBus.addListener(NucleusTestBlocks::buildCreativeTabContents);
        modEventBus.addListener(NucleusTestGameTests::registerTests);
    }
}
