package com.pauljoda.nucleus.network.packets.bidirectional;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SyncableFieldPacketTest {
    @Test
    void streamCodecRoundTripsPacketFields() {
        SyncableFieldPacket packet = new SyncableFieldPacket(true, 7, 12.5D, new BlockPos(1, 2, 3));
        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.EMPTY);

        SyncableFieldPacket.STREAM_CODEC.encode(buffer, packet);
        SyncableFieldPacket decoded = SyncableFieldPacket.STREAM_CODEC.decode(buffer);

        assertEquals(packet, decoded);
    }
}
