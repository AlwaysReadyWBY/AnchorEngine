package top.alwaysready.anchorengine.common.util;

import top.alwaysready.anchorengine.common.AnchorEngine;
import top.alwaysready.anchorengine.common.service.LogService;

import java.util.Optional;
import java.util.function.Consumer;

public interface AnchorUtils {

    static void info(Consumer<String> send, Object text, Object... args){
        send.accept(AnchorEngine.getInstance().getStringReplacer().applyColored(text,args));
    }

    static void info(Object text,Object... args){
        getService(LogService.class).ifPresent(sv -> info(sv::info,text,args));
    }

    static void debug(String msg){
        getService(LogService.class).ifPresent(sv -> sv.debug(msg));
    }

    static void warn(String msg,Throwable thr){
        getService(LogService.class).ifPresent(sv -> sv.warn(msg,thr));
    }

    static <T> Optional<T> getService(Class<T> type){
        return AnchorEngine.getInstance().getServiceManager().getService(type);
    }

    static <T> Optional<Registry<T>> getRegistry(Class<T> type){
        return AnchorEngine.getInstance().getServiceManager().getRegistry(type);
    }

    static <T> void registerService(Class<T> type,T service){
        AnchorEngine.getInstance().getServiceManager().registerService(type,service);
    }

    static <T> void setRegistry(Class<T> type, Registry<T> registry){
        AnchorEngine.getInstance().getServiceManager().setRegistry(type,registry);
    }

    static String toKey(String key){
        return toKey("anchor_engine",key);
    }

    static String toKey(String namespace,String key){
        if(key == null) return null;
        return key.indexOf(':')<0? namespace+":"+key:key;
    }
}
