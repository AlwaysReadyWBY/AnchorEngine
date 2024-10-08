package top.alwaysready.anchorengine.common.net.channel;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import top.alwaysready.anchorengine.common.service.schedule.ScheduleService;
import top.alwaysready.anchorengine.common.util.AnchorUtils;

import java.util.Arrays;
import java.util.UUID;

public interface AChannel<T> {
    String CONTROL = AnchorUtils.toKey("control");

    void setContext(UUID id);
    UUID getContext();
    default void newContext(){
        setContext(UUID.randomUUID());
    }
    ByteArrayDataOutput encode(T payload);
    T decode(ByteArrayDataInput in);
    void accept(T payload);

    default void send(T payload,ChannelOutput out){
        AnchorUtils.getService(ScheduleService.class).ifPresent(sv -> sv.scheduleAsync(()->{
            out.send(encode(payload).toByteArray());
        }));
    }
    default void accept(byte[] bytes){
        AnchorUtils.getService(ScheduleService.class).ifPresent(sv -> sv.scheduleAsync(()->{
            T decoded = decode(ByteStreams.newDataInput(bytes));
            if(decoded == null) return;
            accept(decoded);
        }));
    }

    default boolean isConnected(){
        return getContext()!=null;
    }
    default void disconnect(){
        setContext(null);
    }
}
