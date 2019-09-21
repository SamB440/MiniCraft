package me.minidigger.miniserver.test.protocol.server;

import com.google.common.base.MoreObjects;

import io.netty.buffer.ByteBuf;
import me.minidigger.miniserver.test.protocol.DataTypes;
import me.minidigger.miniserver.test.protocol.Packet;
import me.minidigger.miniserver.test.protocol.PacketHandler;
import me.minidigger.miniserver.test.server.MiniConnection;

public class ServerLoginEncryptionResponse extends Packet {

    private byte[] sharedSecret;
    private byte[] token;

    @Override
    public void handle(MiniConnection connection, PacketHandler handler) {
        handler.handle(connection, this);
    }

    @Override
    public void toWire(ByteBuf buf) {

    }

    @Override
    public void fromWire(ByteBuf buf) {
        sharedSecret = DataTypes.readByteArray(buf);
        token = DataTypes.readByteArray(buf);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("sharedSecret", sharedSecret)
                .add("token", token)
                .toString();
    }
}