package top.alwaysready.anchorengine.common.ui.element;

import top.alwaysready.anchorengine.common.action.ActionInfo;
import top.alwaysready.anchorengine.common.util.AnchorUtils;

import java.util.Optional;

public class AButton extends UIElement{
    private ActionInfo onClick;
    private ActionInfo onRelease;
    private String style;

    public void setOnClick(ActionInfo onClick) {
        this.onClick = onClick;
    }

    public void setOnRelease(ActionInfo onRelease) {
        this.onRelease = onRelease;
    }

    public Optional<ActionInfo> getOnClick() {
        return Optional.ofNullable(onClick);
    }

    public Optional<ActionInfo> getOnRelease() {
        return Optional.ofNullable(onRelease);
    }

    public String getStyle() {
        if(style == null) style = Styles.MODERN;
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public interface Styles{
        String VANILLA = AnchorUtils.toKey("vanilla");
        String MODERN = AnchorUtils.toKey("modern");
        String INVISIBLE = AnchorUtils.toKey("invisible");
    }
}
