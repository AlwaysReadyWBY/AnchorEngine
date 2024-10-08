package top.alwaysready.anchorengine.common.util;

import java.util.Optional;
import java.util.function.Supplier;

public class KeyedRegistry<T> extends Registry<T> {

    @Override
    public KeyedRegistry<T> register(String key, T value) {
        super.register(AnchorUtils.toKey(key), value);
        return this;
    }

    @Override
    public Optional<T> get(String key) {
        return super.get(AnchorUtils.toKey(key));
    }

    @Override
    public T getOrCreate(String key, Supplier<T> create) {
        return super.getOrCreate(AnchorUtils.toKey(key), create);
    }
}
