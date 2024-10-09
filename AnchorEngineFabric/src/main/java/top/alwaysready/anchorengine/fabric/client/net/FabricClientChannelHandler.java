package top.alwaysready.anchorengine.fabric.client.net;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import top.alwaysready.anchorengine.common.client.ClientChannelHandler;
import top.alwaysready.anchorengine.common.net.channel.AControlChannel;
import top.alwaysready.anchorengine.common.util.AnchorUtils;
import top.alwaysready.anchorengine.fabric.AnchorEngineFabric;

@Environment(EnvType.CLIENT)
public class FabricClientChannelHandler implements ClientChannelHandler {
    private final AControlChannel controlChannel = new ClientControlChannel();

    public FabricClientChannelHandler(){
        getControlChannel().setOutput(this::sendControlPacket);
    }

    @Override
    public AControlChannel getControlChannel() {
        return controlChannel;
    }

    private void sendControlPacket(byte[] bytes) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBytes(bytes);
        ClientPlayNetworking.send(AnchorEngineFabric.CONTROL_CHANNEL, buf);
    }

    public void register() {
        AnchorUtils.registerService(ClientChannelHandler.class,this);
        ClientPlayNetworking.registerGlobalReceiver(AnchorEngineFabric.CONTROL_CHANNEL, (client, handler, buf, responseSender) ->
                controlChannel.accept(buf.getWrittenBytes()));
    }
}
