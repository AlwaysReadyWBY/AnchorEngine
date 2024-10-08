package top.alwaysready.anchorengine.common.server;

import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

public abstract class ServerChannelManager {
    private final Map<UUID, ServerChannelHandler> handlerMap = new Hashtable<>();

    public ServerChannelHandler getHandler(UUID id){
        return handlerMap.computeIfAbsent(id,this::newHandler);
    }

    public void unloadHandler(UUID id){
        handlerMap.remove(id);
    }

    public abstract ServerChannelHandler newHandler(UUID id);
}
