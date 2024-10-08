package top.alwaysready.anchorengine.common.server;

import top.alwaysready.anchorengine.common.net.channel.AControlChannel;

import java.util.UUID;

public interface ServerChannelHandler {
    UUID getPlayerId();
    AControlChannel getControlChannel();
}
