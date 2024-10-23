package top.alwaysready.anchorengine.fabric.server;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import top.alwaysready.anchorengine.common.server.ServerChannelHandler;
import top.alwaysready.anchorengine.common.server.ServerChannelManager;
import top.alwaysready.anchorengine.common.service.ExecutorScheduleService;
import top.alwaysready.anchorengine.common.service.schedule.ScheduleService;
import top.alwaysready.anchorengine.common.util.AnchorUtils;
import top.alwaysready.anchorengine.fabric.net.PacketPayload;

public class AnchorEngineFabricServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        AnchorUtils.registerService(ScheduleService.class, new ExecutorScheduleService(2) {
            @Override
            public void executeSync(Runnable run) {
                run.run();
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(PacketPayload.CONTROL_CHANNEL,(payload,context) -> {
            ServerPlayerEntity player = context.player();
            AnchorUtils.getService(ServerChannelManager.class)
                    .map(chMan -> chMan.getHandler(player.getUuid()))
                    .map(ServerChannelHandler::getControlChannel)
                    .ifPresent(channel -> {
                        if(!(channel.getOutput() instanceof FabricServerChannelOutput)){
                            channel.setOutput(new FabricServerChannelOutput(player,(PacketPayload.CONTROL_CHANNEL)));
                        }
                        channel.accept(payload.bytes());
                    });
        });
    }
}
