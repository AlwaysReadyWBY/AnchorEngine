package top.alwaysready.anchorengine.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import top.alwaysready.anchorengine.common.action.Action;
import top.alwaysready.anchorengine.common.action.ActionManager;
import top.alwaysready.anchorengine.common.service.ServiceManager;
import top.alwaysready.anchorengine.common.string.StringReplacer;
import top.alwaysready.anchorengine.common.ui.element.UIElement;
import top.alwaysready.anchorengine.common.ui.element.UIElementManager;

public class AnchorEngine {
    private static AnchorEngine instance;

    public static AnchorEngine getInstance() {
        if(instance == null) instance = new AnchorEngine();
        return instance;
    }

    public static void unload(){
        instance = null;
    }

    private ServiceManager svMan;
    private StringReplacer replacer;
    private Gson compactGson;
    private Gson configGson;

    public ServiceManager getServiceManager() {
        if(svMan == null) svMan = new ServiceManager();
        return svMan;
    }

    public StringReplacer getStringReplacer() {
        if(replacer == null) replacer = new StringReplacer();
        return replacer;
    }

    public void buildGson(){
        GsonBuilder builder = new GsonBuilder();
        getServiceManager().getService(UIElementManager.class)
                .ifPresent(sv -> builder.registerTypeAdapter(UIElement.class,sv));
        getServiceManager().getService(ActionManager.class)
                .ifPresent(sv -> builder.registerTypeAdapter(Action.class,sv));
        compactGson = builder.create();
        configGson = builder.setPrettyPrinting().create();
    }

    public Gson getConfigGson() {
        if(configGson == null) buildGson();
        return configGson;
    }

    public Gson getCompactGson(){
        if(compactGson == null) buildGson();
        return compactGson;
    }
}
