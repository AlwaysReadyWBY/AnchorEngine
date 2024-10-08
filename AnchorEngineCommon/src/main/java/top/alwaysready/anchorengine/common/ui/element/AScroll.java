package top.alwaysready.anchorengine.common.ui.element;

import java.util.Optional;

public class AScroll extends UIElement{
    private String vBarWidth;
    private String hBarHeight;
    private String hScrollSpeed;
    private String vScrollSpeed;
    private UIElement child;

    public void sethScrollSpeed(String hScrollSpeed) {
        this.hScrollSpeed = hScrollSpeed;
    }

    public String getHScrollSpeed() {
        if(hScrollSpeed == null) hScrollSpeed = "4";
        return hScrollSpeed;
    }

    public void setVScrollSpeed(String vScrollSpeed) {
        this.vScrollSpeed = vScrollSpeed;
    }

    public String getVScrollSpeed() {
        if(vScrollSpeed == null) vScrollSpeed = "4";
        return vScrollSpeed;
    }

    public String getVBarWidth() {
        if(vBarWidth == null) vBarWidth = "6";
        return vBarWidth;
    }

    public void setVBarWidth(String vBarWidth) {
        this.vBarWidth = vBarWidth;
    }

    public String getHBarHeight() {
        if(hBarHeight == null) hBarHeight = "0";
        return hBarHeight;
    }

    public void setHBarHeight(String hBarHeight) {
        this.hBarHeight = hBarHeight;
    }

    public void setChild(UIElement child) {
        this.child = child;
    }

    public Optional<UIElement> getChild() {
        return Optional.ofNullable(child);
    }
}
