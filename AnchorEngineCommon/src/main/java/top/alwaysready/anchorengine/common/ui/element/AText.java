package top.alwaysready.anchorengine.common.ui.element;

import com.google.gson.JsonObject;
import top.alwaysready.anchorengine.common.serialization.StringReplacer;
import top.alwaysready.anchorengine.common.serialization.SerializableText;

public class AText extends UIElement{
    private String lineHeight;
    private String color;
    private SerializableText text;

    public void setText(SerializableText text) {
        this.text = text;
    }

    public SerializableText getText() {
        return text;
    }

    public JsonObject getText(StringReplacer replacer) {
        return getText().getJson(replacer);
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