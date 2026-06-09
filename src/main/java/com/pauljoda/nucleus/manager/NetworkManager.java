package com.pauljoda.nucleus.manager;

import com.pauljoda.nucleus.Nucleus;
import com.pauljoda.nucleus.network.packets.ClientBoundPacket;
import com.pauljoda.nucleus.network.packets.ServerBoundPacket;
import com.pauljoda.nucleus.network.packets.bidirectional.SyncableFieldPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Manages the network communication for the Nucleus mod.
 * <p>
 * Based on https://github.com/AppliedEnergistics/Applied-Energistics-2 for new network handling
 */
public class NetworkManager {

    /**
     * Initializes the NetworkManager.
     *
     * @param event the event to handle payload registration
     */
    @SubscribeEvent
    public static void init(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(Nucleus.MODID);

        bidirectional(registrar, SyncableFieldPacket.TYPE, SyncableFieldPacket.STREAM_CODEC);
    }

    /**
     * Processes a client-bound packet.
     *
     * @param registrar   the registrar to register packet payload
     * @param packetClass the class of the packet
     * @param reader      the reader to read packet data
     * @param <T>         the type of the packet
     */
    private static <T extends ClientBoundPacket> void clientbound(PayloadRegistrar registrar, CustomPacketPayload.Type<T> type,
                                                                  StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        registrar.playToClient(type, codec, ClientBoundPacket::handleOnClient);
    }

    /**
     * Processes a server-bound packet.
     *
     * @param registrar   the registrar to register packet payload
     * @param packetClass the class of the packet
     * @param reader      the reader to read packet data
     * @param <T>         the type of the packet
     */
    private static <T extends ServerBoundPacket> void serverbound(PayloadRegistrar registrar, CustomPacketPayload.Type<T> type,
                                                                  StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        registrar.playToServer(type, codec, ServerBoundPacket::handleOnServer);
    }

    /**
     * Processes a bidirectional packet.
     *
     * @param registrar   the registrar to register packet payload
     * @param packetClass the class of the packet
     * @param reader      the reader to read packet data
     * @param <T>         the type of the packet
     */
    private static <T extends ServerBoundPacket & ClientBoundPacket> void bidirectional(PayloadRegistrar registrar,
                                                                                        CustomPacketPayload.Type<T> type,
                                                                                        StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        registrar.playBidirectional(type, codec, ClientBoundPacket::handleOnClient, ServerBoundPacket::handleOnServer);
    }
}
