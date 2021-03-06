package me.minidigger.minicraft.protocol.server;

import com.google.common.base.MoreObjects;

import io.netty.buffer.ByteBuf;
import me.minidigger.minicraft.protocol.MiniPacket;

public class ServerPlayLook extends MiniPacket {

    private float pitch, yaw;
    private boolean onGround;

    @Override
    public void toWire(ByteBuf buf) {

    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("pitch", pitch)
                .add("yaw", yaw)
                .add("onGround", onGround)
                .toString();
    }

    @Override
    public void fromWire(ByteBuf buf) {
        this.pitch = buf.readFloat();
        this.yaw = buf.readFloat();
        this.onGround = buf.readBoolean();
    }

}
