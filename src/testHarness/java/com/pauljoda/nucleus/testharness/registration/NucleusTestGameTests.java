package com.pauljoda.nucleus.testharness.registration;

import com.pauljoda.nucleus.testharness.NucleusTestHarness;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Rotation;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Game tests that prove the development-only Nucleus harness is loaded.
 */
public final class NucleusTestGameTests {
    public static final DeferredRegister<Consumer<GameTestHelper>> TEST_FUNCTIONS =
            DeferredRegister.create(BuiltInRegistries.TEST_FUNCTION, NucleusTestHarness.MODID);

    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> TEST_MARKER_BLOCK_PLACES =
            TEST_FUNCTIONS.register("test_marker_block_places", () -> NucleusTestGameTests::testMarkerBlockPlaces);

    private static final Identifier TEST_MARKER_BLOCK_PLACES_ID =
            Identifier.fromNamespaceAndPath(NucleusTestHarness.MODID, "test_marker_block_places");

    private NucleusTestGameTests() {
    }

    public static void registerTests(RegisterGameTestsEvent event) {
        Holder<TestEnvironmentDefinition<?>> environment = event.registerEnvironment(
                Identifier.fromNamespaceAndPath(NucleusTestHarness.MODID, "default")
        );

        event.registerTest(
                TEST_MARKER_BLOCK_PLACES_ID,
                new FunctionGameTestInstance(
                        ResourceKey.create(BuiltInRegistries.TEST_FUNCTION.key(), TEST_MARKER_BLOCK_PLACES_ID),
                        new TestData<>(
                                environment,
                                Identifier.withDefaultNamespace("empty"),
                                100,
                                0,
                                true,
                                Rotation.NONE
                        )
                )
        );
    }

    private static void testMarkerBlockPlaces(GameTestHelper helper) {
        helper.setBlock(1, 1, 1, NucleusTestBlocks.TEST_MARKER_BLOCK.get());
        helper.succeedWhenBlockPresent(NucleusTestBlocks.TEST_MARKER_BLOCK.get(), 1, 1, 1);
    }
}
