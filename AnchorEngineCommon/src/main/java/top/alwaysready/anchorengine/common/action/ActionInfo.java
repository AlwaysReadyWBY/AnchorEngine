package top.alwaysready.anchorengine.common.action;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import top.alwaysready.anchorengine.common.string.StringReplacer;

import java.lang.reflect.Type;
import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;

@JsonAdapter(ActionInfo.Adapter.class)
public class ActionInfo {
    private final String id;
    private final Map<String,String> params;

    public ActionInfo(String id){
        this.id = id;
        params = new Hashtable<>();
    }

    public void setParam(String key,String value){
        params.put(key,value);
    }

    public Optional<String> getParam(String key){
        return Optional.ofNullable(params.get(key));
    }

    public boolean hasParam(){
        return !params.isEmpty();
    }

    public String getId() {
        return id;
    }

    protected Map<String, String> getParams() {
        return params;
    }

    public ActionInfo getReplaced(StringReplacer replacer){
        ActionInfo replaced = new ActionInfo(replacer.apply(getId()));
        getParams().forEach((key,value)-> replaced.setParam(key,replacer.apply(value)));
        return replaced;
    }

    public StringReplacer getReplacer(StringReplacer parent){
        StringReplacer ret = parent.createChild();
        getParams().forEach(ret::map);
        return ret;
    }

    public static class Adapter implements JsonSerializer<ActionInfo>, JsonDeserializer<ActionInfo>{

        @Override
        public ActionInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if(json == null) return null;
            if(json.isJsonPrimitive()){
                JsonPrimitive prim = json.getAsJsonPrimitive();
                return prim.isString()? new ActionInfo(prim.getAsString()):null;
            }
            if(json.isJsonObject()){
                JsonObject obj = json.getAsJsonObject();
                ActionInfo ret = new ActionInfo(obj.get("id").getAsString());
                obj.keySet().forEach(key -> ret.setParam(key,obj.get(key).getAsString()));
                return ret;
            }
            return null;
        }

        @Override
        public JsonElement serialize(ActionInfo src, Type typeOfSrc, JsonSerializationContext context) {
            if(src == null) return JsonNull.INSTANCE;
            if(src.hasParam()){
                JsonObject obj = new JsonObject();
                src.getParams().forEach(obj::addProperty);
                obj.addProperty("id",src.getId());
                return obj;
            }
            return new JsonPrimitive(src.getId());
        }
    }
}
