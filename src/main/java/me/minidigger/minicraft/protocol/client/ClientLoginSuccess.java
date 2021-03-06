package me.minidigger.minicraft.protocol.client;

import com.google.common.base.MoreObjects;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import me.minidigger.minicraft.protocol.MiniPacket;
import me.minidigger.minicraft.protocol.DataTypes;

public class ClientLoginSuccess extends MiniPacket {

    private String username;
    private UUID uuid;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("username", username)
                .add("uuid", uuid)
                .toString();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void toWire(ByteBuf buf) {
        DataTypes.writeString(uuid.toString(), buf);
        DataTypes.writeString(username, buf);
    }

    @Override
    public void fromWire(ByteBuf buf) {

    }

}
