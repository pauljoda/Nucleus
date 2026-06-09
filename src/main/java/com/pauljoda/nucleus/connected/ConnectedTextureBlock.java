package com.pauljoda.nucleus.connected;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class ConnectedTextureBlock extends Block implements ConnectedTexture {

    public ConnectedTextureBlock() {
        this(Properties.of().strength(2.0F));
    }

    public ConnectedTextureBlock(Properties props) {
        super(props);

        registerDefaultState(getStateDefinition().any()
                .setValue(CONNECTED_DOWN, false)
                .setValue(CONNECTED_UP, false)
                .setValue(CONNECTED_NORTH, false)
                .setValue(CONNECTED_SOUTH, false)
                .setValue(CONNECTED_EAST, false)
                .setValue(CONNECTED_WEST, false));
    }

    /*******************************************************************************************************************
     * Block                                                                                                           *
     *******************************************************************************************************************/


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(CONNECTED_UP).add(CONNECTED_DOWN).add(CONNECTED_NORTH).add(CONNECTED_SOUTH).add(CONNECTED_EAST).add(CONNECTED_WEST);
    }

    @Override
    protected BlockState updateShape(BlockState pState, LevelReader pLevel, ScheduledTickAccess ticks,
                                     BlockPos pPos, Direction pDirection, BlockPos pNeighborPos,
                                     BlockState pNeighborState, RandomSource random) {
        return pState.setValue(CONNECTED_UP, canConnect(pLevel, pPos, Direction.UP))
                .setValue(CONNECTED_DOWN, canConnect(pLevel, pPos, Direction.DOWN))
                .setValue(CONNECTED_NORTH, canConnect(pLevel, pPos, Direction.NORTH))
                .setValue(CONNECTED_SOUTH, canConnect(pLevel, pPos, Direction.SOUTH))
                .setValue(CONNECTED_EAST, canConnect(pLevel, pPos, Direction.EAST))
                .setValue(CONNECTED_WEST, canConnect(pLevel, pPos, Direction.WEST));
    }

    @Override
    public boolean canConnect(LevelReader level, BlockPos pos, Direction dir) {
        return level.getBlockState(pos.relative(dir)).getBlock() == this;
    }
}
