package top.alwaysready.anchorengine.common.serialization;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import top.alwaysready.anchorengine.common.AnchorEngine;

import java.lang.reflect.Type;
import java.util.stream.Stream;

@JsonAdapter(SerializableText.Adapter.class)
public class SerializableText {
    private String plain;
    private JsonObject json;
    private boolean replaceable;

    public void setPlain(String plain) {
        this.plain = plain;
    }

    public String getPlain() {
        return plain;
    }

    public void setJson(JsonObject json) {
        this.json = json;
        if(isReplaceable()){
            setPlain(json.toString());
        }
    }

    protected JsonObject getJson() {
        return json;
    }

    public JsonObject getJson(StringReplacer replacer) {
        JsonObject json = getJson();
        if(isReplaceable() || json == null){
            String plain = getPlain();
            if(plain.startsWith("{")) {
                json = AnchorEngine.getInstance().getCompactGson().fromJson(replacer.apply(getPlain()), JsonObject.class);
            } else {
                json = new JsonObject();
                json.addProperty("text",replacer.applyColored(plain));
            }
        }
        return json;
    }

    public void setReplaceable(boolean replaceable) {
        this.replaceable = replaceable;
    }

    public boolean isReplaceable() {
        return replaceable;
    }

    public static class Adapter implements JsonSerializer<SerializableText>, JsonDeserializer<SerializableText>{

        @Override
        public SerializableText deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if(json == null) return null;
            if(json.isJsonObject()){
                SerializableText text = new SerializableText();
                JsonElement removed = json.getAsJsonObject().remove("replaceable");
                text.setReplaceable(removed!=null && removed.isJsonPrimitive() && !removed.getAsString().equals("0"));
                text.setJson(json.getAsJsonObject());
                return text;
            } else if(json.isJsonPrimitive()){
                SerializableText text = new SerializableText();
                text.setReplaceable(true);
                text.setPlain(json.getAsString());
                return text;
            } else if(json.isJsonArray()){
                Stream.Builder<String> strings = Stream.builder();
                json.getAsJsonArray().forEach(elem -> strings.accept(elem.getAsString()));
                SerializableText text = new SerializableText();
                text.setReplaceable(true);
                text.setPlain(String.join("\n",strings.build().toList()));
                return text;
            }
            return null;
        }

        @Override
        public JsonElement serialize(SerializableText src, Type typeOfSrc, JsonSerializationContext context) {
            if(src == null) return JsonNull.INSTANCE;
            if(src.isReplaceable()){
                return new JsonPrimitive(src.getPlain());
            } else {
                return src.getJson();
            }
        }
    }
}
