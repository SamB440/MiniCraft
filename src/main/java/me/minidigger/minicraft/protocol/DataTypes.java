package me.minidigger.minicraft.protocol;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import net.kyori.nbt.CompoundTag;
import net.kyori.nbt.ListTag;
import net.kyori.nbt.Tag;
import net.kyori.nbt.TagIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import me.minidigger.minicraft.model.BlockPosition;
import me.minidigger.minicraft.model.Position;

public class DataTypes {

    private static final Logger log = LoggerFactory.getLogger(DataTypes.class);

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static int readVarInt(ByteBuf buf) {
        int numRead = 0;
        int result = 0;
        byte read;
        do {
            read = buf.readByte();
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 5) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((read & 0b10000000) != 0);

        return result;
    }

    public static void writeVarInt(int value, ByteBuf buf) {
        do {
            byte temp = (byte) (value & 0b01111111);
            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            buf.writeByte(temp);
        } while (value != 0);
    }

    public static long readVarLong(ByteBuf buf) {
        int numRead = 0;
        long result = 0;
        byte read;
        do {
            read = buf.readByte();
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 10) {
                throw new RuntimeException("VarLong is too big");
            }
        } while ((read & 0b10000000) != 0);

        return result;
    }

    public static void writeVarLong(long value, ByteBuf buf) {
        do {
            byte temp = (byte) (value & 0b01111111);
            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            buf.writeByte(temp);
        } while (value != 0);
    }

    public static String readString(ByteBuf buf) {
        int length = readVarInt(buf);

        byte[] bytes = new byte[length];
        buf.readBytes(bytes);

        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static void writeString(String string, ByteBuf buf) {
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        writeVarInt(bytes.length, buf);
        buf.writeBytes(bytes);
    }

    public static short readShort(ByteBuf buf) {
        return buf.readShort();
    }

    public static void writeShort(short val, ByteBuf buf) {
        buf.writeShort(val);
    }

    public static <T> T readJSON(ByteBuf buf, Class<T> clazz) {
        String string = readString(buf);

        try {
            return gson.fromJson(string, clazz);
        } catch (JsonSyntaxException e) {
            log.error("Error while parsing json {}: {}", string, e);
            return null;
        }
    }

    public static void writeJSON(Object object, ByteBuf buf) {
        writeString(gson.toJson(object), buf);
    }

    public static byte[] readByteArray(ByteBuf buf) {
        int len = readVarInt(buf);
        byte[] data = new byte[len];
        buf.readBytes(data);
        return data;
    }

    public static void writeByteArray(byte[] arr, ByteBuf buf) {
        writeVarInt(arr.length, buf);
        buf.writeBytes(arr);
    }

    public static long[] readLongArray(ByteBuf buf) {
        int len = readVarInt(buf);
        long[] data = new long[len];
        for (int i = 0; i < data.length; i++) {
            data[i] = buf.readLong();
        }
        return data;
    }

    public static void writeLongArray(long[] arr, ByteBuf buf) {
        writeVarInt(arr.length, buf);
        for (long l : arr) {
            buf.writeLong(l);
        }
    }

    public static int[] readIntArray(ByteBuf buf) {
        int len = readVarInt(buf);
        int[] data = new int[len];
        for (int i = 0; i < data.length; i++) {
            data[i] = buf.readInt();
        }
        return data;
    }

    public static void writeIntArray(int[] arr, ByteBuf buf) {
        writeVarInt(arr.length, buf);
        for (int l : arr) {
            buf.writeInt(l);
        }
    }

    public static Position readPosition(boolean full, ByteBuf buf) {
        if (full) {
            return new Position(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readFloat(), buf.readFloat());
        } else {
            return new Position(buf.readDouble(), buf.readDouble(), buf.readDouble());
        }
    }

    public static void writePosition(boolean full, Position position, ByteBuf buf) {
        buf.writeDouble(position.getX());
        buf.writeDouble(position.getY());
        buf.writeDouble(position.getZ());
        if (full) {
            buf.writeFloat(position.getYaw());
            buf.writeFloat(position.getPitch());
        }
    }

    public static BlockPosition readBlockPosition(ByteBuf buf) {
        long val = buf.readLong();
        return new BlockPosition((int) (val >> 38), (int) (val & 0xFFF), (int) (val << 26 >> 38));
    }

    public static void writeBlockPosition(BlockPosition position, ByteBuf buf) {
        buf.writeLong((((long) (position.getX()) & 0x3FFFFFF) << 38) | ((position.getZ() & 0x3FFFFFF) << 12) | (position.getY() & 0xFFF));
    }

    public static void writeNBT(CompoundTag tag, ByteBuf buf) {
        @SuppressWarnings("UnstableApiUsage") ByteArrayDataOutput out = ByteStreams.newDataOutput();
        try {
            TagIO.writeDataOutput(tag, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        buf.writeBytes(out.toByteArray());
    }

    public static void writeNBTList(ListTag listTag, ByteBuf buf) {
        DataTypes.writeVarInt(listTag.size(), buf);
        for (Tag tag : listTag) {
            DataTypes.writeNBT((CompoundTag) tag, buf);
        }
    }
}
