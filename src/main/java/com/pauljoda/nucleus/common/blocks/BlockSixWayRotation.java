package com.pauljoda.nucleus.common.blocks;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import java.util.Arrays;

/**
 * This file was created for Nucleus - Java
 *
 * Nucleus - Java is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/9/2017
 */
public class BlockSixWayRotation extends Block {

    // Instance of the property for rotation
    public static EnumProperty<Direction> SIX_WAY =
            EnumProperty.create("facing", Direction.class, Arrays.asList(Direction.NORTH, Direction.EAST, Direction.SOUTH,
                    Direction.WEST, Direction.DOWN, Direction.UP));


    /**
     * Main constructor for the block
     */
    public BlockSixWayRotation(Properties props) {
        super(props);
        registerDefaultState(getStateDefinition().any().setValue(SIX_WAY, Direction.NORTH));
    }

    /*******************************************************************************************************************
     * Block Methods                                                                                                   *
     *******************************************************************************************************************/

    /**
     * Set the state for placement
     * @param context Use context
     * @return State to place
     */
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(SIX_WAY, context.getNearestLookingDirection().getOpposite());
    }

    /*******************************************************************************************************************
     * BlockState Methods                                                                                              *
     *******************************************************************************************************************/

    /**
     * Add properties to our block on load
     * @param builder Property holder
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SIX_WAY); // Add rotation
    }
}
