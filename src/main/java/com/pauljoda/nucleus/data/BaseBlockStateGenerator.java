package com.pauljoda.nucleus.data;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Data provider for Nucleus connected-texture blockstates.
 */
public abstract class BaseBlockStateGenerator implements DataProvider {
    protected final PackOutput output;
    protected final String modid;
    private final Set<Block> connectedTextureBlocks = new LinkedHashSet<>();

    public BaseBlockStateGenerator(PackOutput output, String modid, Object exFileHelper) {
        this.output = output;
        this.modid = modid;
    }

    public void addConnectedTextureModels(Block block, String modID) {
        connectedTextureBlocks.add(block);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        CompletableFuture<?> future = CompletableFuture.completedFuture(null);
        for (Block block : connectedTextureBlocks) {
            Identifier blockId = BuiltInRegistries.BLOCK.getKey(block);
            Identifier modelId = Identifier.fromNamespaceAndPath(blockId.getNamespace(), "block/" + blockId.getPath());
            future = CompletableFuture.allOf(future,
                    DataProvider.saveStable(cache, connectedBlockState(modelId), output.getOutputFolder()
                            .resolve("assets/" + blockId.getNamespace() + "/blockstates/" + blockId.getPath() + ".json")),
                    DataProvider.saveStable(cache, cubeAllModel(blockId), output.getOutputFolder()
                            .resolve("assets/" + modelId.getNamespace() + "/models/" + modelId.getPath() + ".json")));
        }
        return future;
    }

    @Override
    public String getName() {
        return modid + " connected texture blockstates";
    }

    protected JsonObject connectedBlockState(Identifier modelId) {
        JsonObject root = new JsonObject();
        JsonObject variants = new JsonObject();
        for (boolean down : BOOLEANS) {
            for (boolean east : BOOLEANS) {
                for (boolean north : BOOLEANS) {
                    for (boolean south : BOOLEANS) {
                        for (boolean up : BOOLEANS) {
                            for (boolean west : BOOLEANS) {
                                JsonObject variant = new JsonObject();
                                variant.addProperty("model", modelId.toString());
                                variants.add(variantKey(down, east, north, south, up, west), variant);
                            }
                        }
                    }
                }
            }
        }
        root.add("variants", variants);
        return root;
    }

    protected JsonObject cubeAllModel(Identifier blockId) {
        JsonObject root = new JsonObject();
        root.addProperty("parent", "minecraft:block/cube_all");
        JsonObject textures = new JsonObject();
        textures.addProperty("all", blockId.getNamespace() + ":block/" + blockId.getPath());
        root.add("textures", textures);
        return root;
    }

    private static final boolean[] BOOLEANS = {false, true};

    private static String variantKey(boolean down, boolean east, boolean north, boolean south, boolean up, boolean west) {
        return "connected_down=" + down +
                ",connected_east=" + east +
                ",connected_north=" + north +
                ",connected_south=" + south +
                ",connected_up=" + up +
                ",connected_west=" + west;
    }
}
