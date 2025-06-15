// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: It's an abstract class for loading prototypes from JSON files.
// -------------------------------------------------------------------------------------

package io.github.towerfull.tools.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import io.github.towerfull.tools.Prototype;
import io.github.towerfull.tools.PrototypeFactory;

/**
 * Abstract class for loading prototypes from JSON files.
 *
 * @param <T> The type of Prototype being loaded.
 * @param <D> The data type used for loading the prototype.
 * @param <E> The enum type representing the prototype types.
 */
public abstract class AbstractPrototypeLoader<T extends Prototype, D, E extends Enum<E>> {
    // Json instance for parsing JSON files.
    private final Json json;

    /**
     * Constructor for AbstractPrototypeLoader.
     */
    public AbstractPrototypeLoader() {
        json = new Json();
        json.setIgnoreUnknownFields(true);
        json.setEnumNames(true);
    }

    /**
     * Loads prototypes from a JSON file.
     * @param path the path to the JSON file.
     * @param enumClass the class of the enum representing the prototype types.
     * @param dataClass the class of the data used for loading the prototype.
     * @param factory the factory to register the loaded prototypes.
     */
    public void load(String path, Class<E> enumClass, Class<D> dataClass, PrototypeFactory<E, T> factory) {
        try {
            FileHandle file = Gdx.files.internal(path);
            ObjectMap<String, D> dataMap = json.fromJson(ObjectMap.class, dataClass, file);

            for(ObjectMap.Entry<String, D> entry : dataMap.entries()) {
                E type = Enum.valueOf(enumClass, entry.key);
                T instance = createInstance(type, entry.value);
                factory.register(type, instance);
            }

            Gdx.app.log("PrototypeLoader", "✅ Chargement des prototypes depuis " + path);
        } catch (Exception e) {
            Gdx.app.error("PrototypeLoader", "❌ Erreur lors du chargement du fichier JSON : " + path, e);
        }
    }

    /**
     * Creates an instance of the prototype.
     * @param type the type of the prototype.
     * @param data the data used to create the prototype instance.
     * @return a new instance of the prototype.
     */
    protected abstract T createInstance(E type, D data);
}
