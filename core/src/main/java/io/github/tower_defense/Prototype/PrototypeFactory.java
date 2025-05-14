package io.github.tower_defense.Prototype;

import java.util.HashMap;
import java.util.Map;

public class PrototypeFactory<T extends Prototype> {
    private final Map<String, T> prototypes = new HashMap<>();

    public void register(String key, T prototype) {
        prototypes.put(key, prototype);
    }

    public T create(String key) {
        T proto = prototypes.get(key);
        return proto != null ? (T) proto.clone() : null;
    }

    public void unregister(String key) {
        prototypes.remove(key);
    }

    public boolean contains(String key) {
        return prototypes.containsKey(key);
    }

    public void clear() {
        prototypes.clear();
    }
}
