package top.alwaysready.anchorengine.common.net.channel;

@FunctionalInterface
public interface ChannelOutput {
    void send(byte[] bytes);
}
