package top.alwaysready.anchorengine.common.client.ui;

import top.alwaysready.anchorengine.common.ui.element.UIElement;
import top.alwaysready.anchorengine.common.ui.element.UIElementManager;
import top.alwaysready.anchorengine.common.util.AnchorUtils;

public interface UIRoot {
    String UI_HUD = AnchorUtils.toKey("hud");

    default void setScreen(String key){
        AnchorUtils.getService(UIElementManager.class)
                .flatMap(man -> man.getElement(AnchorUtils.toKey(key)))
                .ifPresent(this::setScreen);
    }
    void setScreen(UIElement elem);
    void setHud(UIElement elem);
    void closeScreen();
}
