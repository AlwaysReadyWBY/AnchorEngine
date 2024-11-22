package top.alwaysready.anchorengine.common.ui.element;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import top.alwaysready.anchorengine.common.AnchorEngine;
import top.alwaysready.anchorengine.common.string.StringReplacer;

public class AText extends UIElement{
    private static final JsonObject EMPTY_TEXT = parseText("");

    public static JsonObject parseText(String text){
        if(text == null) return EMPTY_TEXT;
        if(text.startsWith("{")){
            try {
                return JsonParser.parseString(text).getAsJsonObject();
            } catch (JsonParseException e){
                JsonObject obj = new JsonObject();
                obj.addProperty("text", text);
                return obj;
            }
        } else {
            JsonObject obj = new JsonObject();
            obj.addProperty("text", text);
            return obj;
        }
    }

    private boolean replaceable = false;
    private String raw;
    private String lineHeight;
    private String color;
    private JsonObject text;

    public boolean isReplaceable() {
        return replaceable;
    }

    public void setReplaceable(boolean replaceable) {
        this.replaceable = replaceable;
    }

    public void setTextRaw(String raw){
        this.raw = raw;
        if(isReplaceable()){
            text = null;
        } else {
            setText(parseText(raw));
        }
    }

    public String getRaw() {
        if(raw == null) raw = AnchorEngine.getInstance().getCompactGson().toJson(getText());
        return raw;
    }

    public void setText(JsonObject text) {
        this.text = text;
        raw = null;
    }

    public JsonObject getText() {
        if(text == null) text = parseText(raw);
        return text;
    }

    public JsonObject getText(StringReplacer replacer) {
        return isReplaceable()?
                parseText(replacer.apply(getRaw())):
                getText();
    }

    public String getLineHeight() {
        if(lineHeight == null) lineHeight = "12";
        return lineHeight;
    }

    public void setLineHeight(String lineHeight) {
        this.lineHeight = lineHeight;
    }

    public String getColor() {
        if(color == null) color = "ffffffff";
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}