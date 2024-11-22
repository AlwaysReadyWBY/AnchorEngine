package top.alwaysready.anchorengine.fabric.client.ui.drawable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.alwaysready.anchorengine.common.ui.element.*;

import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class ADrawableManager {
    private final Map<Class<? extends UIElement>, Function<UIElement, AnchorDrawable<?>>> factoryMap = new Hashtable<>();

    public ADrawableManager(){
        registerFactory(AGroup.class, AGroupDrawable::new);
        registerFactory(AButtonWidget.class,AGroupDrawable::new);
        registerFactory(AImage.class, AImageDrawable::new);
        registerFactory(AText.class,ATextDrawable::new);
        registerFactory(AInput.class,AInputDrawable::new);
        registerFactory(AScroll.class,AScrollDrawable::new);
        registerFactory(AButton.class, AButtonDrawable::new);
    }

    public <T extends UIElement> void registerFactory(Class<T> type,Function<T, AnchorDrawable<?>> fun){
        factoryMap.put(type,elem -> fun.apply((T)elem));
    }

    public Optional<AnchorDrawable<?>> newRenderer(UIElement elem){
        if(elem == null) return Optional.empty();
        return Optional.ofNullable(factoryMap.get(elem.getClass())).map(factory -> factory.apply(elem));
    }
}
