// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: A factory for creating prototypes of a specific type, allowing
//      registration and retrieval of prototypes by enum keys.
// -------------------------------------------------------------------------------------

package io.github.towerfull.tools;

import java.util.HashMap;
import java.util.Map;

/**
 * A factory class for managing prototypes of a specific type, identified by an enum key.
 * It allows registration, creation, and management of prototypes.
 *
 * @param <M> The type of the enum used as keys for the prototypes.
 * @param <T> The type of the prototype that this factory manages.
 */
public class PrototypeFactory<M extends Enum<M>, T extends Prototype> extends Prototype {
    /**
     * A map that holds the prototypes, where the key is an enum constant and the value is the prototype instance.
     */
    private final Map<M, T> prototypes = new HashMap<>();

    /**
     * Registers a prototype with a specific key.
     *
     * @param key       The enum key associated with the prototype.
     * @param prototype The prototype instance to register.
     */
    public void register(M key, T prototype) {
        prototypes.put(key, prototype);
    }

    /**
     * Creates a new instance of the prototype associated with the given key.
     *
     * @param key The enum key for which to create a prototype instance.
     * @return A new instance of the prototype, or null if no prototype is registered for the key.
     */
    public T create(Enum<?> key) {
        T proto = prototypes.get(key);
        return proto != null ? (T) proto.clone() : null;
    }

    /**
     * Unregisters a prototype associated with the given key.
     *
     * @param key The enum key for which to unregister the prototype.
     */
    public void unregister(M key) {
        prototypes.remove(key);
    }

    /**
     * Checks if a prototype is registered for the given key.
     *
     * @param key The enum key to check.
     * @return true if a prototype is registered for the key, false otherwise.
     */
    public boolean contains(M key) {
        return prototypes.containsKey(key);
    }

    /**
     * Clears all registered prototypes.
     */
    public void clear() {
        prototypes.clear();
    }

    /**
     * Retrieves the prototype associated with the given key.
     *
     * @param key The enum key for which to retrieve the prototype.
     * @return The prototype instance associated with the key, or null if no prototype is registered for the key.
     */
    public T getPrototype(M key) {
        return prototypes.get(key);
    }

    /**
     * Gets the map of all registered prototypes.
     *
     * @return A map containing all registered prototypes.
     */
    @Override
    public PrototypeFactory<M, T> clone() {
        PrototypeFactory<M, T> clone = new PrototypeFactory<>();
        for(Map.Entry<M, T> entry : prototypes.entrySet()) {
            clone.register(entry.getKey(), (T) entry.getValue().clone());
        }
        return clone;
    }

    /**
     * Returns a random prototype from the registered prototypes.
     *
     * @return A random prototype instance, or null if no prototypes are registered.
     */
    public T getRandom() {
        if(prototypes.isEmpty()) {
            return null;
        }
        // Get a random key from the map
        M randomKey = prototypes.keySet().stream()
                .skip((int) (prototypes.size() * Math.random()))
                .findFirst()
                .orElse(null);
        return create(randomKey);
    }
}
