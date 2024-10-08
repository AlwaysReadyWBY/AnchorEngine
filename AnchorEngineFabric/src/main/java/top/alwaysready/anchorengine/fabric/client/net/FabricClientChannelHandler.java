package top.alwaysready.anchorengine.fabric.client.net;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import top.alwaysready.anchorengine.common.client.ClientChannelHandler;
import top.alwaysready.anchorengine.common.net.channel.AControlChannel;
import top.alwaysready.anchorengine.common.service.schedule.ScheduleService;
import top.alwaysready.anchorengine.common.util.AnchorUtils;
import top.alwaysready.anchorengine.fabric.net.PacketPayload;

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
        ClientPlayNetworking.send(new PacketPayload(PacketPayload.CONTROL_CHANNEL,bytes));
    }

    public void register() {
        AnchorUtils.registerService(ClientChannelHandler.class,this);
        ClientPlayNetworking.registerGlobalReceiver(PacketPayload.CONTROL_CHANNEL, (payload, context) -> {
            AnchorUtils.getService(ScheduleService.class).ifPresent(sv -> sv.scheduleAsync(()->
                    getControlChannel().accept(payload.bytes())));
        });
    }
}
