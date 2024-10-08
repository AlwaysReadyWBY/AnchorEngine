package top.alwaysready.anchorengine.common.net.channel;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonParseException;
import top.alwaysready.anchorengine.common.AnchorEngine;
import top.alwaysready.anchorengine.common.net.packet.json.JsonPacket;
import top.alwaysready.anchorengine.common.net.packet.json.JsonPacketTypes;
import top.alwaysready.anchorengine.common.net.packet.json.Push;
import top.alwaysready.anchorengine.common.util.AnchorUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.Predicate;

public class AControlChannel extends AbstractAChannel<JsonPacket> {
    private final Map<String,Runnable> actionMap = new Hashtable<>();

    public AControlChannel(boolean inCharge) {
        super(inCharge);
        registerListener(JsonPacketTypes.S2C.ACTION, packet -> {
            if(packet.isOutdated()) return true;
            packet.read(String.class)
                    .map(actionMap::get)
                    .ifPresent(Runnable::run);
            return true;
        });
    }

    @Override
    public ByteArrayDataOutput encode(JsonPacket packet) {
        packet.setContext(getContext());
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(AnchorEngine.getInstance().getCompactGson().toJson(packet));
        return out;
    }

    @Override
    public JsonPacket decode(ByteArrayDataInput in) {
        try {
            JsonPacket packet = AnchorEngine.getInstance().getCompactGson().fromJson(in.readUTF(), JsonPacket.class);
            if (packet == null) {
                AnchorUtils.info("Received corrupted json packet!");
                return null;
            }
            if (isInCharge()) {
                if (!getContext().equals(packet.getContext())) {
                    packet.setOutdated(true);
                }
            } else {
                setContext(packet.getContext());
            }
            return packet;
        } catch (JsonParseException e){
            AnchorUtils.warn("Failed to decode json packet",e);
            return null;
        }
    }

    @Override
    public void accept(JsonPacket payload) {
        if(payload == null) return;
        getListener(payload.getType()).ifPresentOrElse(listener -> listener.handle(payload),
                ()-> AnchorUtils.info("Failed to handle payload "+payload));
    }

    protected Map<String, Runnable> getActionMap() {
        return actionMap;
    }

    public void registerAction(String key,Runnable run){
        getActionMap().put(key,run);
    }

    public void clearActions(){
        getActionMap().clear();
    }

    public void sendPush(Push push){
        send(new JsonPacket(JsonPacketTypes.S2C.PUSH,push));
    }

    public void sendPush(File file){
        if(!file.exists()) return;
        try(FileInputStream in = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(in)){
            sendPush(AnchorEngine.getInstance().getCompactGson().fromJson(reader,Push.class));
        }catch (IOException | JsonParseException e){
            AnchorUtils.warn("Failed to resolve json file "+file.getName(),e);
        }
    }

    public void removeAction(String action) {
        actionMap.remove(action);
    }

    public void removeActions(Predicate<String> removeIf){
        actionMap.keySet().removeIf(removeIf);
    }
}
