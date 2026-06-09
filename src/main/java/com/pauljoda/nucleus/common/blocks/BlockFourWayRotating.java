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
public class BlockFourWayRotating extends Block {

    // Instance of the property for rotation
    public static EnumProperty<Direction> FOUR_WAY =
            EnumProperty.create("facing", Direction.class, Arrays.asList(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST));


    /**
     * Main constructor for the block
     */
    public BlockFourWayRotating(Properties props) {
        super(props);
        registerDefaultState(getStateDefinition().any().setValue(FOUR_WAY, Direction.NORTH));
    }

    /*******************************************************************************************************************
     * Block Methods                                                                                                   *
     *******************************************************************************************************************/

    /**
     * Called when placing block to determine direction
     * @param context Item use
     * @return State for placement
     */
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FOUR_WAY, context.getHorizontalDirection().getOpposite());
    }


    /*******************************************************************************************************************
     * Blockstate properties                                                                                           *
     *******************************************************************************************************************/

    /**
     * Add properties to our block on load
     * @param builder Property holder
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FOUR_WAY); // Add rotation
    }
}
