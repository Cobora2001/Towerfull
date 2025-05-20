package io.github.tower_defense.Prototype;

import java.util.HashMap;
import java.util.Map;

public class PrototypeFactory<M, T extends Prototype> {
    private final Map<Enum<?>, T> prototypes = new HashMap<>();

    public void register(Enum<?> key, T prototype) {
        prototypes.put(key, prototype);
    }

    public T create(Enum<?> key) {
        T proto = prototypes.get(key);
        return proto != null ? (T) proto.clone() : null;
    }

    public void unregister(Enum<?> key) {
        prototypes.remove(key);
    }

    public boolean contains(Enum<?> key) {
        return prototypes.containsKey(key);
    }

    public void clear() {
        prototypes.clear();
    }
}
