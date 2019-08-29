package com.teambr.nucleus.network.packet;

import com.teambr.nucleus.client.gui.ISyncingTileScreen;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * This file was created for Nucleus
 * <p>
 * Nucleus is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 8/29/2019
 */
public class SyncTileScreenPacket implements INetworkMessage {

    // Variables
    public CompoundNBT tag;

    /**
     * Creates a packet with the given info
     * @param nbt The tag to write
     */
    public SyncTileScreenPacket(CompoundNBT nbt) {
        super();
        tag = nbt;
    }

    /*******************************************************************************************************************
     * IMessage                                                                                                        *
     *******************************************************************************************************************/

    @Override
    public void decode(PacketBuffer buf) {
        tag = new PacketBuffer(buf).readCompoundTag();
    }

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeCompoundTag(tag);
    }

    /*******************************************************************************************************************
     * IMessageHandler                                                                                                 *
     *******************************************************************************************************************/

    public static void process(SyncTileScreenPacket message, Supplier<NetworkEvent.Context> ctx) {
        if(ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            ctx.get().enqueueWork(() -> {
                if(message.tag != null && ctx.get().getSender() != null) {
                    ServerPlayerEntity player = ctx.get().getSender();
                    if(player.openContainer instanceof ISyncingTileScreen) {
                        ISyncingTileScreen syncingScreen = (ISyncingTileScreen) player.openContainer;
                        syncingScreen.acceptServerValues(message.tag);
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
