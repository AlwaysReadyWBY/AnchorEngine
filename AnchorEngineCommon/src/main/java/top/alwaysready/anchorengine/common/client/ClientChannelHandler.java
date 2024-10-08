package top.alwaysready.anchorengine.common.client;

import top.alwaysready.anchorengine.common.net.channel.AControlChannel;

public interface ClientChannelHandler {
    AControlChannel getControlChannel();
}
