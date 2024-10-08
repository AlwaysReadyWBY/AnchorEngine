package top.alwaysready.anchorengine.common.net.packet.json;

import top.alwaysready.anchorengine.common.net.channel.ChannelListener;

import java.util.Optional;
import java.util.function.BiConsumer;

public class JsonPacketListener<T> implements ChannelListener<JsonPacket> {
    private final BiConsumer<JsonPacket,T> onReceive;
    private final Class<T> clazz;

    public JsonPacketListener(Class<T> clazz, BiConsumer<JsonPacket,T> onReceive) {
        this.onReceive = onReceive;
        this.clazz = clazz;
    }

    @Override
    public boolean handle(JsonPacket packet) {
        Optional<T> opt = packet.read(clazz);
        opt.ifPresent(obj -> onReceive.accept(packet,obj));
        return opt.isPresent();
    }
}
