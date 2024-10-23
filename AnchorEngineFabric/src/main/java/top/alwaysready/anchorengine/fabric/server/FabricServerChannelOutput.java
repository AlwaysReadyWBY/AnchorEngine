package top.alwaysready.anchorengine.fabric.server;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import top.alwaysready.anchorengine.common.net.channel.ChannelOutput;
import top.alwaysready.anchorengine.fabric.net.PacketPayload;

@Environment(EnvType.SERVER)
public class FabricServerChannelOutput implements ChannelOutput {
    private final ServerPlayerEntity player;
    private final CustomPayload.Id<PacketPayload> channelId;

    public FabricServerChannelOutput(ServerPlayerEntity player, CustomPayload.Id<PacketPayload> channelId) {
        this.player = player;
        this.channelId = channelId;
    }

    @Override
    public void send(byte[] bytes) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBytes(bytes);
        ServerPlayNetworking.send(player, new PacketPayload(channelId ,bytes));
    }
}
