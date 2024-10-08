package top.alwaysready.anchorengine.common.ui.element;

import java.util.Optional;

public class AReference extends UIElement{
    private String key;

    public void setKey(String key) {
        this.key = key;
    }

    public Optional<String> getKey() {
        return Optional.ofNullable(key);
    }
}
