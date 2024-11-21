package top.alwaysready.anchorengine.common.service;

import top.alwaysready.anchorengine.common.action.ActionManager;
import top.alwaysready.anchorengine.common.net.AResourceManager;
import top.alwaysready.anchorengine.common.string.StringParserManager;
import top.alwaysready.anchorengine.common.ui.element.UIElementManager;
import top.alwaysready.anchorengine.common.ui.layout.*;
import top.alwaysready.anchorengine.common.util.KeyedRegistry;
import top.alwaysready.anchorengine.common.util.Registry;

import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;

public class ServiceManager {
    private final Map<Class<?>,Object> serviceMap = new Hashtable<>();
    private final Map<Class<?>, Registry<?>> registryMap = new Hashtable<>();

    public ServiceManager(){
        registerService(StringParserManager.class,new StringParserManager());
        registerService(UIElementManager.class,new UIElementManager());
        registerService(AResourceManager.class,new AResourceManager());
        registerService(ActionManager.class,new ActionManager());
        setRegistry(LayoutFactory.class,new KeyedRegistry<LayoutFactory>()
                .register(Layout.BOARD, BoardLayout::new)
                .register(Layout.LINEAR_HORIZONTAL,()-> new LinearLayout(false))
                .register(Layout.LINEAR_VERTICAL, ()-> new LinearLayout(true))
                .register(Layout.FLOW_HORIZONTAL, ()-> new FlowLayout(false))
                .register(Layout.FLOW_VERTICAL, ()-> new FlowLayout(true)));
    }

    public <T> void registerService(Class<T> type,T service){
        registerService(type,service,true);
    }

    public <T> void registerService(Class<T> type,T service,boolean replace){
        if(replace) {
            serviceMap.put(type, service);
        } else {
            serviceMap.putIfAbsent(type,service);
        }
    }

    public <T> Optional<T> getService(Class<T> type){
        return Optional.ofNullable(serviceMap.get(type))
                .filter(type::isInstance)
                .map(type::cast);
    }

    public <T> void setRegistry(Class<T> type, Registry<T> registry){
        registryMap.put(type,registry);
    }

    public <T> Optional<Registry<T>> getRegistry(Class<T> type){
        return Optional.ofNullable(registryMap.get(type))
                .map(registry -> (Registry<T>)registry);
    }
}
