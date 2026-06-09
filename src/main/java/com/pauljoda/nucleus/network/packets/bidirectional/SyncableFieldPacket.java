package com.pauljoda.nucleus.network.packets.bidirectional;

import com.pauljoda.nucleus.common.blocks.entity.Syncable;
import com.pauljoda.nucleus.network.PacketManager;
import com.pauljoda.nucleus.network.packets.ClientBoundPacket;
import com.pauljoda.nucleus.network.packets.ServerBoundPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import com.pauljoda.nucleus.Nucleus;

/**
 * A packet that syncs a field between client and server.
 */
public record SyncableFieldPacket(boolean returnValue, int fieldId, double value, BlockPos blockPosition)
        implements ClientBoundPacket, ServerBoundPacket {
    public static final Type<SyncableFieldPacket> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Nucleus.MODID, "syncable_field"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncableFieldPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SyncableFieldPacket::returnValue,
            ByteBufCodecs.INT, SyncableFieldPacket::fieldId,
            ByteBufCodecs.DOUBLE, SyncableFieldPacket::value,
            BlockPos.STREAM_CODEC, SyncableFieldPacket::blockPosition,
            SyncableFieldPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /*******************************************************************************************************************
     * Handle Packet                                                                                                   *
     *******************************************************************************************************************/


    /**
     * Handles the packet on the client side.
     *
     * @param player The player that received the packet.
     */
    @Override
    public void handleOnClient(Player player) {
        Level level = player.level();

        // Safety check
        if (blockPosition == null ||
                level.getBlockEntity(blockPosition) == null ||
                !(level.getBlockEntity(blockPosition) instanceof Syncable))
            return;

        // If wanting to ping back server for whatever reason
        if (returnValue)
            PacketManager.INSTANCE.sendToServer(new SyncableFieldPacket(false, fieldId,
                    ((Syncable) level.getBlockEntity(blockPosition)).getVariable(fieldId), blockPosition));
        else
            ((Syncable) level.getBlockEntity(blockPosition)).setVariable(fieldId, value);

    }

    /**
     * Handles the packet on the server side.
     *
     * @param player The server player that received the packet.
     */
    @Override
    public void handleOnServer(ServerPlayer player) {
        Level level = player.level();

        // Safety check for non syncable tiles
        if (level.getBlockEntity(blockPosition) == null ||
                !(level.getBlockEntity(blockPosition) instanceof Syncable))
            return;

        // If true, other client wanted all around to see value change
        if (returnValue)
            PacketManager.INSTANCE.sendToAllAround(
                    new SyncableFieldPacket(false, fieldId,
                            ((Syncable) level.getBlockEntity(blockPosition)).getVariable(fieldId), blockPosition),
                    (ServerLevel) level,
                    blockPosition.getX(),
                    blockPosition.getY(),
                    blockPosition.getZ(),
                    25);
        else // On server update
            ((Syncable) level.getBlockEntity(blockPosition)).setVariable(fieldId, value);
    }
}
