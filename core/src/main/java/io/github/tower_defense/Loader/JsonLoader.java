package io.github.tower_defense.Loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import io.github.tower_defense.Prototype.*;
import java.util.ArrayList;
import java.util.List;

public class JsonLoader {

    private static final JsonLoader instance = new JsonLoader();
    private final Json json;

    private JsonLoader() {
        json = new Json();
        json.setIgnoreUnknownFields(true);
        json.setEnumNames(true);
    }

    public static JsonLoader get() {
        return instance;
    }

    public <T> T loadJson(String path, Class<T> type) {
        FileHandle file = Gdx.files.internal(path);
        return json.fromJson(type, file);
    }

    public <T> List<T> loadJsonList(String path, Class<T> elementType) {
        FileHandle file = Gdx.files.internal(path);
        return json.fromJson(ArrayList.class, elementType, file);
    }

    public List<WaveEntry> getWaveEntries(String waveId) {
        return loadJsonList("waves/" + waveId + ".json", WaveEntry.class);
    }

    public void loadAppearancePrototypes(String path) {
        FileHandle file = Gdx.files.internal(path);
        ObjectMap<String, AppearancePrototype> dataMap = json.fromJson(ObjectMap.class, AppearancePrototype.class, file);

        for (ObjectMap.Entry<String, AppearancePrototype> entry : dataMap.entries()) {
            String key = entry.key;
            AppearancePrototype data = entry.value;

            if (data.appearance == null) {
                throw new RuntimeException("Missing appearance for: " + key);
            }

            // Load the texture and create an appearance object
            AppearanceAssets.getInstance().registerAppearance(key, new KillableAppearance(
                GameAssets.getTexture(data.appearance, Texture.class),
                data.width,
                data.height
            ));
        }
    }

    public void loadMonsterPrototypes(String path, PrototypeFactory<MonsterType, Monster> factory) {
        FileHandle file = Gdx.files.internal(path);
        ObjectMap<String, MonsterPrototype> dataMap = json.fromJson(ObjectMap.class, MonsterPrototype.class, file);

        for (ObjectMap.Entry<String, MonsterPrototype> entry : dataMap.entries()) {
            MonsterType type = MonsterType.valueOf(entry.key);
            MonsterPrototype data = entry.value;

            // Optional: Preload texture or appearance for later use if you still want centralized loading.
            if (data.appearance == null) {
                throw new RuntimeException("Missing appearance for monster: " + type);
            }

            // Possibly store appearance in a map elsewhere if needed, or skip entirely here

            Monster monster = new Monster(
                data.hp, data.hp, null, data.speed, data.damage, data.reward,
                AppearanceAssets.getInstance().getAppearance(data.appearance));

            factory.register(type, monster);
        }
    }

    public void loadTowerPrototypes(String path, PrototypeFactory<TowerType, Tower> factory) {
        FileHandle file = Gdx.files.internal(path);
        ObjectMap<String, TowerPrototype> dataMap = json.fromJson(ObjectMap.class, TowerPrototype.class, file);

        for (ObjectMap.Entry<String, TowerPrototype> entry : dataMap.entries()) {
            TowerType type = TowerType.valueOf(entry.key);
            TowerPrototype data = entry.value;

            // Optional: Preload texture or appearance for later use if you still want centralized loading.
            if (data.appearance == null) {
                throw new RuntimeException("Missing appearance for tower: " + type);
            }

            // Possibly store appearance in a map elsewhere if needed, or skip entirely here

            Tower tower = new Tower(data.range, data.damage, data.cooldown, data.cost,
                AppearanceAssets.getInstance().getAppearance(data.appearance));
            factory.register(type, tower);
        }
    }

    public static class MonsterPrototype {
        public int speed;
        public int hp;
        public int damage;
        public int reward;
        public String appearance;
    }

    public static class TowerPrototype {
        public int damage;
        public int range;
        public int cost;
        public float cooldown;
        public String appearance;
    }

    public static class AppearancePrototype {
        public String appearance;
        public float width;
        public float height;
    }
}
