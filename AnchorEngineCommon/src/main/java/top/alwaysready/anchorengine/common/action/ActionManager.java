package top.alwaysready.anchorengine.common.action;

import com.google.gson.*;
import top.alwaysready.anchorengine.common.util.AnchorUtils;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ActionManager implements JsonSerializer<Action>,JsonDeserializer<Action> {
    private final Map<Class<?>,String> typeNameMap = new ConcurrentHashMap<>();
    private final Map<String,Class<? extends Action>> typeMap = new ConcurrentHashMap<>();

    public void registerType(String typeName,Class<? extends Action> type){
        typeNameMap.put(type,typeName);
        typeMap.put(typeName,type);
    }

    public Optional<String> getTypeName(Class<?> type){
        return Optional.ofNullable(typeNameMap.get(type));
    }

    public Optional<Class<? extends Action>> getType(String typeName){
        if(typeName==null) return Optional.empty();
        return Optional.ofNullable(typeMap.get(AnchorUtils.toKey(typeName)));
    }

    @Override
    public Action deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(json == null || !json.isJsonObject()) return null;
        JsonElement type = json.getAsJsonObject().get("type");
        if(type==null || !type.isJsonPrimitive()) return null;
        return getType(type.getAsString()).map(t-> {
            try {
                return (Action)context.deserialize(json,t);
            }catch (NullPointerException | JsonParseException e){
                AnchorUtils.warn("Failed to parse action "+json.toString(),e);
                return null;
            }
        }).orElse(null);
    }

    @Override
    public JsonElement serialize(Action src, Type typeOfSrc, JsonSerializationContext context) {
        if(src==null) return JsonNull.INSTANCE;
        Class<? extends Action> type = src.getClass();
        return getTypeName(type).map(typeName-> {
            JsonObject obj = context.serialize(src,type).getAsJsonObject();
            obj.addProperty("type",typeName);
            return obj;
        }).orElse(null);
    }
}
