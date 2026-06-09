package com.pauljoda.nucleus.network.packets;

import com.pauljoda.nucleus.Nucleus;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Represents a packet sent from server to client.
 */
public interface ClientBoundPacket extends CustomPacketPayload {

    /**
     * Performs an action related to this packet on the client side, specific to a particular player.
     *
     * @param player The player entity where the packet is used.
     */
    void handleOnClient(Player player);

    /**
     * Performs an action related to this packet on the client side.
     *
     * @param context The context of the client where the packet is used.
     */
    default void handleOnClient(IPayloadContext context) {
        context.enqueueWork(() -> handleOnClient(context.player()));
    }
}
