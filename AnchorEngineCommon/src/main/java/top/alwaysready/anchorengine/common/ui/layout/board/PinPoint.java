package top.alwaysready.anchorengine.common.ui.layout.board;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import top.alwaysready.anchorengine.common.serialization.StringReplacer;

import java.lang.reflect.Type;
import java.util.Optional;

@JsonAdapter(PinPoint.Adapter.class)
public class PinPoint {
    private String ref;
    private String xOffset;
    private String yOffset;
    private String xGrow;
    private String yGrow;

    public PinPoint(){
        this(null);
    }

    public PinPoint(String ref){
        this.ref = ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public Optional<String> getRef() {
        return Optional.ofNullable(ref);
    }

    public void setXGrow(String xGrow) {
        this.xGrow = xGrow;
    }

    public String getXGrow() {
        if(xGrow == null) xGrow = "0";
        return xGrow;
    }

    public double getXGrow(StringReplacer replacer){
        return replacer.getAsDouble(getXGrow()).orElse(0d);
    }

    public void setYGrow(String yGrow) {
        this.yGrow = yGrow;
    }

    public String getYGrow() {
        if(yGrow == null) yGrow = "0";
        return yGrow;
    }

    public double getYGrow(StringReplacer replacer){
        return replacer.getAsDouble(getYGrow()).orElse(0d);
    }

    public void setXOffset(String xOffset) {
        this.xOffset = xOffset;
    }

    public String getXOffset() {
        if(xOffset == null) xOffset = "0";
        return xOffset;
    }

    public double getXOffset(StringReplacer replacer){
        return replacer.getAsDouble(getXOffset()).orElse(0d);
    }

    public void setYOffset(String yOffset) {
        this.yOffset = yOffset;
    }

    public double getYOffset(StringReplacer replacer){
        return replacer.getAsDouble(getYOffset()).orElse(0d);
    }

    public String getYOffset() {
        if(yOffset == null) yOffset = "0";
        return yOffset;
    }

    public static class Adapter implements JsonSerializer<PinPoint>, JsonDeserializer<PinPoint>{

        @Override
        public PinPoint deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if(json == null) return null;
            if(json.isJsonPrimitive()){
                return new PinPoint(json.getAsString());
            } else if(json.isJsonObject()){
                PinPoint pin = new PinPoint();
                JsonObject map = json.getAsJsonObject();
                if(map.has("ref")) pin.setRef(map.get("ref").getAsString());
                if(map.has("xOffset")) pin.setXOffset(map.get("xOffset").getAsString());
                if(map.has("xGrow")) pin.setXGrow(map.get("xGrow").getAsString());
                if(map.has("yOffset")) pin.setYOffset(map.get("yOffset").getAsString());
                if(map.has("yGrow")) pin.setYGrow(map.get("yGrow").getAsString());
                return pin;
            }
            return null;
        }

        @Override
        public JsonElement serialize(PinPoint src, Type typeOfSrc, JsonSerializationContext context) {
            if(src == null) return JsonNull.INSTANCE;
            JsonObject json = new JsonObject();
            src.getRef().ifPresent(ref -> json.addProperty("ref",ref));
            Optional.ofNullable(src.xGrow).ifPresent(xGrow->json.addProperty("xGrow",xGrow));
            Optional.ofNullable(src.xOffset).ifPresent(xOffset->json.addProperty("xOffset",xOffset));
            Optional.ofNullable(src.yGrow).ifPresent(yGrow->json.addProperty("yGrow",yGrow));
            Optional.ofNullable(src.yOffset).ifPresent(yOffset->json.addProperty("yOffset",yOffset));
            return json;
        }
    }
}
