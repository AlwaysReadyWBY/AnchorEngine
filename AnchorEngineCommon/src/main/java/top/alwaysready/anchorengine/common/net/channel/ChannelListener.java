package top.alwaysready.anchorengine.common.net.channel;

@FunctionalInterface
public interface ChannelListener<T> {
    /**
     * @param payload
     * @return If the payload should be consumed
     */
    boolean handle(T payload);
}
