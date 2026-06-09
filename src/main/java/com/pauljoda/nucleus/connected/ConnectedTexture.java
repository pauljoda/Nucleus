package com.pauljoda.nucleus.connected;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public interface ConnectedTexture {

    BooleanProperty CONNECTED_DOWN = BooleanProperty.create("connected_down");
    BooleanProperty CONNECTED_UP = BooleanProperty.create("connected_up");
    BooleanProperty CONNECTED_NORTH = BooleanProperty.create("connected_north");
    BooleanProperty CONNECTED_SOUTH = BooleanProperty.create("connected_south");
    BooleanProperty CONNECTED_WEST = BooleanProperty.create("connected_west");
    BooleanProperty CONNECTED_EAST = BooleanProperty.create("connected_east");


    /**
     * Determines if the given direction can be connected in the level.
     *
     * @param level     the level accessor
     * @param direction the direction to check
     * @return {@code true} if the given direction can be connected, {@code false} otherwise
     */
    boolean canConnect(LevelReader level, BlockPos pos, Direction direction);
}
