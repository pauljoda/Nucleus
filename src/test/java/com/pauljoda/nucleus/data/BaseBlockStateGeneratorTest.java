package com.pauljoda.nucleus.data;

import com.google.gson.JsonObject;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BaseBlockStateGeneratorTest {
    @Test
    void connectedBlockStateContainsEverySixWayBooleanVariant() {
        TestGenerator generator = new TestGenerator();
        JsonObject blockState = generator.blockState(Identifier.fromNamespaceAndPath("nucleus_pauljoda", "block/test_block"));
        JsonObject variants = blockState.getAsJsonObject("variants");

        assertEquals(64, variants.size());
        assertTrue(variants.has("connected_down=false,connected_east=false,connected_north=false,connected_south=false,connected_up=false,connected_west=false"));
        assertTrue(variants.has("connected_down=true,connected_east=true,connected_north=true,connected_south=true,connected_up=true,connected_west=true"));
    }

    @Test
    void cubeAllModelUsesBlockTexture() {
        TestGenerator generator = new TestGenerator();
        JsonObject model = generator.model(Identifier.fromNamespaceAndPath("example", "machine"));

        assertEquals("minecraft:block/cube_all", model.get("parent").getAsString());
        assertEquals("example:block/machine", model.getAsJsonObject("textures").get("all").getAsString());
    }

    private static class TestGenerator extends BaseBlockStateGenerator {
        TestGenerator() {
            super(new PackOutput(Path.of("build/test-data")), "nucleus_pauljoda", null);
        }

        JsonObject blockState(Identifier modelId) {
            return connectedBlockState(modelId);
        }

        JsonObject model(Identifier blockId) {
            return cubeAllModel(blockId);
        }
    }
}
