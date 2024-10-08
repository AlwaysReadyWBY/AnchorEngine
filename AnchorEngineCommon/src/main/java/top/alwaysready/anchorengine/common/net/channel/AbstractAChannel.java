package top.alwaysready.anchorengine.common.net.channel;

import top.alwaysready.anchorengine.common.util.AnchorUtils;

import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractAChannel<T> implements AChannel<T>{
    private UUID context;
    private final Map<String,ChannelListener<T>> listenerMap = new Hashtable<>();
    private ChannelOutput output;
    private final boolean inCharge;

    protected AbstractAChannel(boolean inCharge){
        this.inCharge = inCharge;
    }

    public boolean isInCharge() {
        return inCharge;
    }

    public void setOutput(ChannelOutput output) {
        this.output = output;
    }

    public ChannelOutput getOutput() {
        return output;
    }

    protected void registerListener(String key, ChannelListener<T> listener){
        if(key == null) return;
        listenerMap.put(AnchorUtils.toKey(key),listener);
    }

    public Optional<ChannelListener<T>> getListener(String key) {
        if(key == null) return Optional.empty();
        return Optional.ofNullable(listenerMap.get(AnchorUtils.toKey(key)));
    }

    @Override
    public void setContext(UUID context) {
        this.context = context;
    }

    @Override
    public UUID getContext() {
        return context;
    }

    public void send(T payload){
        if(payload == null) {
            AnchorUtils.info("Trying to send corrupted payload!");
            return;
        }
        ChannelOutput out = getOutput();
        if(out == null) return;
        send(payload,out);
    }

    @Override
    public void accept(T payload) {
        if(payload == null) return;
        if(listenerMap.values().stream().noneMatch(listener -> listener.handle(payload))){
            AnchorUtils.debug("Failed to handle payload "+payload);
        }
    }
}
