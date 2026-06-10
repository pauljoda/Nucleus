package com.pauljoda.nucleus.testharness.registration;

import com.pauljoda.nucleus.connected.ConnectedTexture;
import com.pauljoda.nucleus.testharness.NucleusTestHarness;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos;
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

    public static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> CONNECTED_TEXTURE_PLUS_SHAPE =
            TEST_FUNCTIONS.register("connected_texture_plus_shape", () -> NucleusTestGameTests::connectedTexturePlusShape);

    private static final Identifier TEST_MARKER_BLOCK_PLACES_ID =
            Identifier.fromNamespaceAndPath(NucleusTestHarness.MODID, "test_marker_block_places");
    private static final Identifier CONNECTED_TEXTURE_PLUS_SHAPE_ID =
            Identifier.fromNamespaceAndPath(NucleusTestHarness.MODID, "connected_texture_plus_shape");

    private NucleusTestGameTests() {
    }

    public static void registerTests(RegisterGameTestsEvent event) {
        Holder<TestEnvironmentDefinition<?>> environment = event.registerEnvironment(
                Identifier.fromNamespaceAndPath(NucleusTestHarness.MODID, "default")
        );

        registerTest(event, environment, TEST_MARKER_BLOCK_PLACES_ID);
        registerTest(event, environment, CONNECTED_TEXTURE_PLUS_SHAPE_ID);
    }

    private static void registerTest(RegisterGameTestsEvent event,
                                     Holder<TestEnvironmentDefinition<?>> environment,
                                     Identifier id) {
        event.registerTest(
                id,
                new FunctionGameTestInstance(
                        ResourceKey.create(BuiltInRegistries.TEST_FUNCTION.key(), id),
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

    private static void connectedTexturePlusShape(GameTestHelper helper) {
        var block = NucleusTestBlocks.CONNECTED_TEXTURE_TEST_BLOCK.get();
        helper.setBlock(1, 1, 1, block);
        helper.setBlock(1, 1, 0, block);
        helper.setBlock(1, 1, 2, block);
        helper.setBlock(0, 1, 1, block);
        helper.setBlock(2, 1, 1, block);

        helper.runAfterDelay(2, () -> {
            var center = helper.getBlockState(new BlockPos(1, 1, 1));
            assertConnection(helper, center.getValue(ConnectedTexture.CONNECTED_NORTH), "north", true);
            assertConnection(helper, center.getValue(ConnectedTexture.CONNECTED_SOUTH), "south", true);
            assertConnection(helper, center.getValue(ConnectedTexture.CONNECTED_WEST), "west", true);
            assertConnection(helper, center.getValue(ConnectedTexture.CONNECTED_EAST), "east", true);
            assertConnection(helper, center.getValue(ConnectedTexture.CONNECTED_UP), "up", false);
            assertConnection(helper, center.getValue(ConnectedTexture.CONNECTED_DOWN), "down", false);
            helper.succeed();
        });
    }

    private static void assertConnection(GameTestHelper helper, boolean actual, String direction, boolean expected) {
        if (actual != expected) {
            helper.fail("Expected connected_" + direction + "=" + expected + " but was " + actual);
        }
    }
}
