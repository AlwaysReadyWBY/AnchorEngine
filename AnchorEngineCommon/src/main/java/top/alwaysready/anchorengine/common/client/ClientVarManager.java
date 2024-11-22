package top.alwaysready.anchorengine.common.client;

import top.alwaysready.anchorengine.common.net.packet.json.JsonPacket;
import top.alwaysready.anchorengine.common.net.packet.json.JsonPacketTypes;
import top.alwaysready.anchorengine.common.net.packet.json.PacketQuery;
import top.alwaysready.anchorengine.common.string.StringReplacer;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ClientVarManager extends StringReplacer {
    //Keep variables for 30 seconds by default.
    private long keepMillis = 30000;
    private final Map<String,Long> varRequestMap = new Hashtable<>();
    private final Map<String, Supplier<String>> clientVarMap = new Hashtable<>();

    public void setKeepMillis(long keepMillis) {
        this.keepMillis = keepMillis;
    }

    public long getKeepMillis() {
        return keepMillis;
    }

    public void registerClientVar(String key,Supplier<String> supplier){
        if(key == null || supplier==null) return;
        clientVarMap.put(key,supplier);
    }

    public boolean hasClientVar(String key){
        if(key == null) return false;
        return clientVarMap.containsKey(key);
    }

    public Optional<String> getClientVar(String key){
        if(key == null) return Optional.empty();
        return Optional.ofNullable(clientVarMap.get(key)).map(Supplier::get);
    }

    public void removeClientVar(String key){
        if(key==null) return;
        clientVarMap.remove(key);
    }

    @Override
    protected Optional<String> getMapped(String from) {
        varRequestMap.put(from,System.currentTimeMillis()+getKeepMillis());
        Supplier<String> var = clientVarMap.get(from);
        if(var != null){
            return Optional.ofNullable(var.get());
        }
        return super.getMapped(from);
    }

    public CompletableFuture<Boolean> purge(){
        long now = System.currentTimeMillis();
        varRequestMap.entrySet().removeIf(entry-> {
            if(now > entry.getValue()){
                remove(entry.getKey());
                removeClientVar(entry.getKey());
                return true;
            }
            return false;
        });
        return CompletableFuture.completedFuture(true);
    }

    public CompletableFuture<Boolean> query(ClientChannelHandler handler){
        PacketQuery query = new PacketQuery();
        query.setVarList(new ArrayList<>(varRequestMap.keySet()));
        handler.getControlChannel().send(new JsonPacket(JsonPacketTypes.C2S.QUERY,query));
        return CompletableFuture.completedFuture(true);
    }
}
