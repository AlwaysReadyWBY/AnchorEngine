package top.alwaysready.anchorengine.common.ui.element;

import com.google.gson.annotations.SerializedName;
import top.alwaysready.anchorengine.common.ui.layout.Layout;

import java.util.ArrayList;
import java.util.List;

public class AGroup extends UIElement{
    @SerializedName("child")
    private List<UIElement> children;

    private String layoutMode;

    public List<UIElement> getChildren() {
        if(children == null) children = new ArrayList<>();
        return children;
    }

    public void setLayoutMode(String layoutMode) {
        this.layoutMode = layoutMode;
    }

    public String getLayoutMode() {
        if(layoutMode == null) layoutMode = Layout.BOARD;
        return layoutMode;
    }
}
