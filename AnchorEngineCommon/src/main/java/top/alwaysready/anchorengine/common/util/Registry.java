package top.alwaysready.anchorengine.common.util;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class Registry<T> {
    private final Map<String,T> mapping = new Hashtable<>();

    public Registry<T> register(String key,T value){
        if(key == null || value == null) return this;
        mapping.put(key,value);
        return this;
    }

    public Optional<T> get(String key){
        return Optional.ofNullable(mapping.get(key));
    }

    public T getOrCreate(String key, Supplier<T> create){
        return mapping.computeIfAbsent(key,any->create.get());
    }

    public Collection<T> values(){
        return mapping.values();
    }

    public void forEach(BiConsumer<String,T> run){
        getMapping().forEach(run);
    }

    public void remap(BiFunction<String,T,T> compute){
        new ArrayList<>(getMapping().keySet()).forEach(key -> getMapping().compute(key,compute));
    }

    protected Map<String, T> getMapping() {
        return mapping;
    }

    public void clear(){
        mapping.clear();
    }
}
