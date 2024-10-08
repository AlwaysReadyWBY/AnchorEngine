package top.alwaysready.anchorengine.common.net.packet.json;

import com.google.gson.JsonObject;
import top.alwaysready.anchorengine.common.client.ClientChannelHandler;
import top.alwaysready.anchorengine.common.net.channel.AControlChannel;
import top.alwaysready.anchorengine.common.server.ServerChannelHandler;
import top.alwaysready.anchorengine.common.server.ServerChannelManager;
import top.alwaysready.anchorengine.common.util.AnchorUtils;

import java.util.UUID;
import java.util.function.Consumer;

public interface JsonPacketUtils {
    interface C2S {
        String ACTION_CLOSE = AnchorUtils.toKey("close");

        static void sendAction(String action){
            AnchorUtils.getService(ClientChannelHandler.class)
                    .map(ClientChannelHandler::getControlChannel)
                    .ifPresent(channel -> channel.send(new JsonPacket(JsonPacketTypes.C2S.ACTION,action)));
        }

        static void sendClose() {
            sendAction(ACTION_CLOSE);
        }
    }
    interface S2C {
        static void setScreen(UUID playerId, String key, Consumer<AControlChannel> init){
            AnchorUtils.getService(ServerChannelManager.class)
                    .map(chMan -> chMan.getHandler(playerId))
                    .map(ServerChannelHandler::getControlChannel)
                    .ifPresent(channel -> {
                        channel.newContext();
                        init.accept(channel);
                        if (key == null){
                            channel.send(new JsonPacket(JsonPacketTypes.S2C.CLOSE_SCREEN,new JsonObject()));
                        } else {
                            channel.send(new JsonPacket(JsonPacketTypes.S2C.SET_SCREEN, AnchorUtils.toKey(key)));
                        }
                    });
        }

        static void setScreen(UUID playerId, String key){
            setScreen(playerId,key,channel->{});
        }
    }
}
