package com.pauljoda.nucleus.testharness.common;

import com.mojang.serialization.MapCodec;
import com.pauljoda.nucleus.connected.ConnectedTextureBlock;
import net.minecraft.world.level.block.Block;

/**
 * Development-only connected-texture block used to validate Nucleus connection
 * state updates and model/blockstate rendering in a real client.
 */
public class NucleusConnectedTextureTestBlock extends ConnectedTextureBlock {
    public static final MapCodec<NucleusConnectedTextureTestBlock> CODEC = simpleCodec(NucleusConnectedTextureTestBlock::new);

    public NucleusConnectedTextureTestBlock(Block.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends NucleusConnectedTextureTestBlock> codec() {
        return CODEC;
    }
}
