package top.alwaysready.anchorengine.common.ui.layout.board;

import top.alwaysready.anchorengine.common.string.StringReplacer;

public class PinPoint {
    private String xOffset;
    private String yOffset;
    private String xGrow;
    private String yGrow;

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
}
