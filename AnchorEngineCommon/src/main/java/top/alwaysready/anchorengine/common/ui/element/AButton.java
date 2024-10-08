package top.alwaysready.anchorengine.common.ui.element;

import top.alwaysready.anchorengine.common.util.AnchorUtils;

import java.util.Optional;

public class AButton extends UIElement{
    private String onClick;
    private String onRelease;
    private String style;

    public void setOnClick(String onClick) {
        this.onClick = onClick;
    }

    public void setOnRelease(String onRelease) {
        this.onRelease = onRelease;
    }

    public Optional<String> getOnClick() {
        return Optional.ofNullable(onClick);
    }

    public Optional<String> getOnRelease() {
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
