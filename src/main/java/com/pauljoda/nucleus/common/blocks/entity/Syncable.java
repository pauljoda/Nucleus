package com.pauljoda.nucleus.common.blocks.entity;

import com.pauljoda.nucleus.network.PacketManager;
import com.pauljoda.nucleus.network.packets.bidirectional.SyncableFieldPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This file was created for Nucleus - Java
 * <p>
 * Nucleus - Java is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/6/2017
 */
public abstract class Syncable extends UpdatingBlockEntity {

    public Syncable(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    /*******************************************************************************************************************
     * Abstract Methods                                                                                                *
     *******************************************************************************************************************/

    /**
     * Used to set the value of a field
     *
     * @param id    The field id
     * @param value The value of the field
     */
    public abstract void setVariable(int id, double value);

    /**
     * Used to get the field on the server, this will fetch the server value and overwrite the current
     *
     * @param id The field id
     * @return The value on the server, now set to ourselves
     */
    public abstract Double getVariable(int id);

    /*******************************************************************************************************************
     * Syncable                                                                                                        *
     *******************************************************************************************************************/

    /**
     * Sends the value to the server, you should probably only call this from the client
     */
    public void sendValueToServer(int id, double value) {
        PacketManager.INSTANCE.sendToServer(new SyncableFieldPacket(false, id, value, getBlockPos()));
    }

    /**
     * Will get the value from the server and set it to our current value, call from client
     * Only use if you lose data and want to update from server. Few cases for this
     */
    public void updateClientValueFromServer(int id) {
        PacketManager.INSTANCE.sendToServer(new SyncableFieldPacket(true, id, 0, getBlockPos()));
    }

    /**
     * Sends the value to the clients nearby
     */
    public void sendValueToClient(int id, double value) {
        PacketManager.INSTANCE.sendToAllAround(
                new SyncableFieldPacket(false, id, value, getBlockPos()),
                (ServerLevel) getLevel(),
                getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), 25);
    }
}
