package io.github.tower_defense.tools;

import io.github.tower_defense.entities.Prototype;

import java.util.HashMap;
import java.util.Map;

public class PrototypeFactory<M extends Enum<M>, T extends Prototype> extends Prototype {

    private final Map<M, T> prototypes = new HashMap<>();

    public void register(M key, T prototype) {
        prototypes.put(key, prototype);
    }

    public T create(Enum<?> key) {
        T proto = prototypes.get(key);
        return proto != null ? (T) proto.clone() : null;
    }

    public void unregister(M key) {
        prototypes.remove(key);
    }

    public boolean contains(M key) {
        return prototypes.containsKey(key);
    }

    public void clear() {
        prototypes.clear();
    }

    public T getPrototype(M key) {
        return prototypes.get(key);
    }

    @Override
    public PrototypeFactory<M, T> clone() {
        PrototypeFactory<M, T> clone = new PrototypeFactory<>();
        for (Map.Entry<M, T> entry : prototypes.entrySet()) {
            clone.register(entry.getKey(), (T) entry.getValue().clone());
        }
        return clone;
    }
}
