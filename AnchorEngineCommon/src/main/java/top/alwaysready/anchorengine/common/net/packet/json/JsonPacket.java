package top.alwaysready.anchorengine.common.net.packet.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import top.alwaysready.anchorengine.common.AnchorEngine;
import top.alwaysready.anchorengine.common.util.AnchorUtils;

import java.util.Optional;
import java.util.UUID;

public final class JsonPacket {
    private String type;
    private UUID context;
    private JsonElement payload;
    private transient boolean outdated = false;

    public JsonPacket(String type, JsonElement payload) {
        this.type = AnchorUtils.toKey(type);
        this.payload = payload;
    }

    public boolean isOutdated() {
        return outdated;
    }

    public void setOutdated(boolean outdated) {
        this.outdated = outdated;
    }

    public JsonPacket(String type, Object obj) {
        this(type, AnchorEngine.getInstance().getCompactGson().toJsonTree(obj));
    }

    public <T> Optional<T> read(Class<T> clazz) {
        try {
            return Optional.ofNullable(AnchorEngine.getInstance().getCompactGson().fromJson(getPayload(), clazz));
        } catch (JsonParseException e) {
            AnchorUtils.warn("Failed to read "+clazz.getName()+ " from:\n"+getPayload(),e);
            return Optional.empty();
        }
    }

    public String getType() {
        return type;
    }

    public UUID getContext() {
        return context;
    }

    public void setContext(UUID context) {
        this.context = context;
    }

    public JsonElement getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "JsonPacket[" +
                "type=" + type + ", " +
                "context=" + context + ", " +
                "payload=" + payload + ']';
    }

}
