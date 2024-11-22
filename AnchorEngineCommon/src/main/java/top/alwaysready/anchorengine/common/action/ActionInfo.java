package top.alwaysready.anchorengine.common.action;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import top.alwaysready.anchorengine.common.string.StringReplacer;

import java.lang.reflect.Type;
import java.util.*;

@JsonAdapter(ActionInfo.Adapter.class)
public class ActionInfo {
    private final String id;
    private final Map<String,String> params;
    private final List<ActionInfo> infoList = new ArrayList<>();

    public ActionInfo(String id){
        this.id = id;
        params = new Hashtable<>();
    }

    public List<ActionInfo> getInfoList() {
        return infoList;
    }

    public void setParam(String key, String value){
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

    public boolean isList(){
        return getId() == null;
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
            if(json.isJsonArray()){
                JsonArray arr = json.getAsJsonArray();
                ActionInfo ret = new ActionInfo(null);
                List<ActionInfo> list = ret.getInfoList();
                arr.forEach(elem -> list.add(context.deserialize(elem,ActionInfo.class)));
                return ret;
            }
            return null;
        }

        @Override
        public JsonElement serialize(ActionInfo src, Type typeOfSrc, JsonSerializationContext context) {
            if(src == null) return JsonNull.INSTANCE;
            if(src.isList()){
                JsonArray arr = new JsonArray();
                src.getInfoList().forEach(info -> arr.add(context.serialize(info)));
                return arr;
            }
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
