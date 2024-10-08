package top.alwaysready.anchorengine.common.ui.element;

public class ASlot extends UIElement{
    private String index;

    public void setIndex(String index) {
        this.index = index;
    }

    public String getIndex() {
        if(index == null) index = "-1";
        return index;
    }
}
