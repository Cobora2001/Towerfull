package io.github.tower_defense.Loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import io.github.tower_defense.Prototype.*;

public abstract class AbstractPrototypeLoader<T extends Prototype, E extends Enum<E>> {

    private final Json json;

    public AbstractPrototypeLoader() {
        json = new Json();
        json.setIgnoreUnknownFields(true);
        json.setEnumNames(true);
    }

    public void load(String path, Class<E> enumClass, Class<P> prototypeClass, PrototypeFactory<E, T> factory) {
        try {
            FileHandle file = Gdx.files.internal(path);
            ObjectMap<String, T> dataMap = json.fromJson(ObjectMap.class, prototypeClass, file);

            for (ObjectMap.Entry<String, T> entry : dataMap.entries()) {
                E type = Enum.valueOf(enumClass, entry.key);
                T instance = createInstance(type, entry.value);
                factory.register(type, instance);
            }

            Gdx.app.log("PrototypeLoader", "✅ Chargement des prototypes depuis " + path);
        } catch (Exception e) {
            Gdx.app.error("PrototypeLoader", "❌ Erreur lors du chargement du fichier JSON : " + path, e);
        }
    }

    protected abstract T createInstance(E type, T prototypeData);
}