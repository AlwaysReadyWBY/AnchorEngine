package top.alwaysready.anchorengine.common.ui.element;

import java.util.Optional;

public class ARect extends UIElement{
    private String fill;
    private String border;

    public Optional<String> getFill() {
        return Optional.ofNullable(fill);
    }

    public Optional<String> getBorder() {
        return Optional.ofNullable(border);
    }
}
