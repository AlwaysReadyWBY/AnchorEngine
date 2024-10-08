package top.alwaysready.anchorengine.fabric.net;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import top.alwaysready.anchorengine.common.net.channel.AChannel;

public class PacketPayload implements CustomPayload {
    public static final Id<PacketPayload> CONTROL_CHANNEL = new Id<>(Identifier.of(AChannel.CONTROL));
    public static final PacketCodec<RegistryByteBuf,byte[]> CODEC_RAW = new PacketCodec<>() {
        @Override
        public byte[] decode(RegistryByteBuf buf) {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            return bytes;
        }

        @Override
        public void encode(RegistryByteBuf buf, byte[] value) {
            buf.writeBytes(value);
        }
    };

    public static void register(){
        register(CONTROL_CHANNEL);
    }

    public static void register(Id<PacketPayload> id){
        PacketCodec<RegistryByteBuf, PacketPayload> codec = PacketPayload.codecFor(id);
        PayloadTypeRegistry.playS2C().register(id, codec);
        PayloadTypeRegistry.playC2S().register(id, codec);
    }

    public static PacketCodec<RegistryByteBuf, PacketPayload> codecFor(Id<PacketPayload> id){
        return PacketCodec.tuple(CODEC_RAW, PacketPayload::bytes, bytes -> new PacketPayload(id, bytes));
    }

    private final byte[] bytes;
    private final Id<PacketPayload> id;

    public PacketPayload(Id<PacketPayload> id,byte[] bytes) {
        this.id = id;
        this.bytes = bytes;
    }

    @Override
    public Id<PacketPayload> getId() {
        return id;
    }

    public byte[] bytes() {
        return bytes;
    }

}
