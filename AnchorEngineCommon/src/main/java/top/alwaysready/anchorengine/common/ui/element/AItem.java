package top.alwaysready.anchorengine.common.ui.element;

import top.alwaysready.anchorengine.common.serialization.SerializableItem;

import java.util.Optional;

public class AItem extends UIElement{
    private SerializableItem item;
    private String iconOnly;

    public void setItem(SerializableItem item) {
        this.item = item;
    }

    public Optional<SerializableItem> getItem() {
        return Optional.ofNullable(item);
    }
    public void setIconOnly(String iconOnly) {
        this.iconOnly = iconOnly;
    }

    public String getIconOnly() {
        if(iconOnly == null) iconOnly = "0";
        return iconOnly;
    }
}
