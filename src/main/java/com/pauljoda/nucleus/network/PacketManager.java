package com.pauljoda.nucleus.network;

import com.pauljoda.nucleus.network.packets.ClientBoundPacket;
import com.pauljoda.nucleus.network.packets.ServerBoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * This is a class for managing packets. It provides methods for sending packets to different targets
 * including all clients, a specific server, a specific player, or all players around a certain point.
 * It follows the singleton design pattern.
 */
public class PacketManager {
    /**
     * The singleton instance of this class.
     */
    public static final PacketManager INSTANCE = new PacketManager();

    /**
     * Send a packet to all clients.
     *
     * @param packet the packet to send
     */
    public void sendToAll(ClientBoundPacket packet) {
        PacketDistributor.sendToAllPlayers(packet);
    }

    /**
     * Send a packet to a specific player.
     *
     * @param message the packet to send
     * @param player  the player to send the packet to
     */
    public void sendTo(ClientBoundPacket message, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, message);
    }

    /**
     * Send a packet to all players that are within a certain radius around a certain point.
     *
     * @param message the packet to send
     * @param level   the level to search for nearby players
     */
    public void sendToAllAround(ClientBoundPacket message, ServerLevel level, double x, double y, double z, double radius) {
        PacketDistributor.sendToPlayersNear(level, null, x, y, z, radius, message);
    }

    /**
     * Send a packet to the server.
     *
     * @param message the packet to send
     */
    public void sendToServer(ServerBoundPacket message) {
        ClientPacketDistributor.sendToServer(message);
    }
}
