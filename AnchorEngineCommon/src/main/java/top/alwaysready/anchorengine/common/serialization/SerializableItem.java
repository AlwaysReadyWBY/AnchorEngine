package top.alwaysready.anchorengine.common.serialization;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import top.alwaysready.anchorengine.common.AnchorEngine;

import java.lang.reflect.Type;
import java.util.stream.Stream;

@JsonAdapter(SerializableItem.Adapter.class)
public class SerializableItem {
    private String raw;
    private JsonElement json;
    private boolean replaceable;

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public String getRaw() {
        return raw;
    }

    public void setJson(JsonElement json) {
        this.json = json;
        if(isReplaceable()){
            setRaw(json.toString());
        }
    }

    protected JsonElement getJson() {
        return json;
    }

    public JsonElement getJson(StringReplacer replacer) {
        JsonElement json = getJson();
        if(isReplaceable() || json == null){
            json = AnchorEngine.getInstance().getCompactGson().fromJson(replacer.apply(getRaw()), JsonElement.class);
        }
        return json;
    }

    public void setReplaceable(boolean replaceable) {
        this.replaceable = replaceable;
    }

    public boolean isReplaceable() {
        return replaceable;
    }

    public static class Adapter implements JsonSerializer<SerializableItem>, JsonDeserializer<SerializableItem>{

        @Override
        public SerializableItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if(json == null) return null;
            if(json.isJsonObject()){
                SerializableItem item = new SerializableItem();
                JsonElement removed = json.getAsJsonObject().remove("replaceable");
                item.setReplaceable(removed!=null && removed.isJsonPrimitive() && !removed.getAsString().equals("0"));
                item.setJson(json.getAsJsonObject());
                return item;
            } else if(json.isJsonPrimitive()){
                SerializableItem item = new SerializableItem();
                item.setReplaceable(true);
                item.setRaw(json.getAsString());
                return item;
            }
            return null;
        }

        @Override
        public JsonElement serialize(SerializableItem src, Type typeOfSrc, JsonSerializationContext context) {
            if(src == null) return JsonNull.INSTANCE;
            if(src.isReplaceable()){
                return new JsonPrimitive(src.getRaw());
            } else {
                return src.getJson();
            }
        }
    }
}
