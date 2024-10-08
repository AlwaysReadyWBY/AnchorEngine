package top.alwaysready.anchorengine.common.ui.element;

import com.google.gson.*;
import top.alwaysready.anchorengine.common.util.AnchorUtils;

import java.lang.reflect.Type;
import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;

public class UIElementManager implements JsonSerializer<UIElement>, JsonDeserializer<UIElement> {
    private final Map<String,UIElement> elemMap = new Hashtable<>();
    private final Map<String,Class<? extends UIElement>> typeMap = new Hashtable<>();
    private final Map<Class<? extends UIElement>,String> typeKeyMap = new Hashtable<>();

    public UIElementManager(){
        registerType("text",AText.class);
        registerType("image", AImage.class);
        registerType("group", AGroup.class);
        registerType("button", AButton.class);
        registerType("input", AInput.class);
        registerType("slot", ASlot.class);
        registerType("scroll", AScroll.class);
        registerType("button_widget", AButtonWidget.class);
    }

    public void registerElement(String key,UIElement elem){
        if(key == null || elem == null) return;
        key = AnchorUtils.toKey(key);
        elemMap.put(key,elem);
    }

    public Optional<UIElement> getElement(String key){
        if(key == null) return Optional.empty();
        key = AnchorUtils.toKey(key);
        return Optional.ofNullable(elemMap.get(key));
    }

    public void registerType(String key,Class<? extends UIElement> type){
        if(key == null || type == null) return;
        key = AnchorUtils.toKey(key);
        typeMap.put(key,type);
        typeKeyMap.put(type,key);
    }

    @Override
    public UIElement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(json == null || !json.isJsonObject()) return null;
        try {
            String key = AnchorUtils.toKey(json.getAsJsonObject().get("type").getAsString());
            Class<? extends UIElement> type = typeMap.get(key);
            if(type == null) return null;
            return context.deserialize(json,type);
        }catch (RuntimeException e){
            AnchorUtils.warn("Failed to parse ui element from json "+json,e);
            return null;
        }
    }

    @Override
    public JsonElement serialize(UIElement src, Type typeOfSrc, JsonSerializationContext context) {
        if(src == null) return JsonNull.INSTANCE;
        String key = typeKeyMap.get(src.getClass());
        if(key == null) return JsonNull.INSTANCE;
        JsonObject json = context.serialize(src,src.getClass()).getAsJsonObject();
        json.addProperty("type",key);
        return json;
    }
}
