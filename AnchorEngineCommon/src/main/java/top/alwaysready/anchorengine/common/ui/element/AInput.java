package top.alwaysready.anchorengine.common.ui.element;

import top.alwaysready.anchorengine.common.action.ActionInfo;
import top.alwaysready.anchorengine.common.string.StringParser;
import top.alwaysready.anchorengine.common.string.StringReplacer;

import java.util.Optional;

public class AInput extends UIElement{
    private String var;
    private String multiline;
    private String lineHeight;
    private String color;
    private String autofill;
    private String focusRequired;
    private String obfuscated;
    private ActionInfo onEnter;

    public void setVar(String var) {
        this.var = var;
    }

    public String getVar() {
        return var;
    }

    public String getVar(StringReplacer replacer){
        String var = getVar();
        if(var == null) return null;
        return replacer.apply(var);
    }

    public void setMultiline(String multiline) {
        this.multiline = multiline;
    }

    public String getMultiline() {
        if(multiline == null) multiline = "0";
        return multiline;
    }

    public boolean isMultiline(StringReplacer replacer){
        return StringParser.INT.parseString(replacer.apply(getMultiline()), Integer.class)
                .map(i->i>0)
                .orElse(false);
    }

    public void setObfuscated(String obfuscated) {
        if(obfuscated == null) obfuscated = "0";
        this.obfuscated = obfuscated;
    }

    public String getObfuscated() {
        return obfuscated;
    }

    public boolean isObfuscated(StringReplacer replacer){
        return StringParser.INT.parseString(replacer.apply(getObfuscated()), Integer.class)
                .map(i->i>0)
                .orElse(false);
    }

    public String getLineHeight() {
        if(lineHeight == null) lineHeight = "12";
        return lineHeight;
    }

    public double getLineHeight(StringReplacer replacer){
        return StringParser.DOUBLE.parseString(replacer.apply(getLineHeight()),Double.class).orElse(12d);
    }

    public void setLineHeight(String lineHeight) {
        this.lineHeight = lineHeight;
    }

    public String getColor() {
        if(color == null) color = "ffffffff";
        return color;
    }

    public int getColor(StringReplacer replacer){
        return StringParser.HEX_INT.parseString(replacer.apply(getColor()),Integer.class).orElse(0xffffffff);
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setAutofill(String autofill) {
        this.autofill = autofill;
    }

    public String getAutofill() {
        if(autofill == null) autofill = "";
        return autofill;
    }

    public String getAutofill(StringReplacer replacer){
        return replacer.apply(getAutofill());
    }

    public void setOnEnter(ActionInfo onEnter) {
        this.onEnter = onEnter;
    }

    public Optional<ActionInfo> getOnEnter() {
        return Optional.ofNullable(onEnter);
    }

    public void setFocusRequired(String focusRequired) {
        this.focusRequired = focusRequired;
    }

    public String getFocusRequired() {
        if(focusRequired == null) focusRequired = "1";
        return focusRequired;
    }

    public boolean isFocusRequired(StringReplacer replacer){
        return StringParser.INT.parseString(replacer.apply(getFocusRequired()), Integer.class)
                .map(i->i>0)
                .orElse(true);
    }
}
