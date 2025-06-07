package io.github.tower_defense.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import io.github.tower_defense.prototype.*;

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
        FileHandle file = resolve(path);
        return json.fromJson(type, file);
    }

    public <T> List<T> loadJsonList(String path, Class<T> clazz) {
        FileHandle file = resolve(path);
        T[] array = json.fromJson((Class<T[]>) java.lang.reflect.Array.newInstance(clazz, 0).getClass(), file);
        List<T> list = new ArrayList<>();
        if (array != null) {
            for (T item : array) list.add(item);
        }
        return list;
    }

    public void loadMonsterPrototypes(String path, PrototypeFactory<MonsterType, Monster> factory) {
        new MonsterPrototypeLoader().load(path, MonsterType.class, MonsterData.class, factory);
    }

    public void loadTowerPrototypes(String path, PrototypeFactory<TowerType, Tower> factory) {
        new TowerPrototypeLoader().load(path, TowerType.class, TowerData.class, factory);
    }

    public void loadAppearancePrototypes(String path) {
        FileHandle file = Gdx.files.internal(path);
        Json json = new Json();

        ObjectMap<String, AppearanceData> rawData = json.fromJson(ObjectMap.class, AppearanceData.class, file);
        for (ObjectMap.Entry<String, AppearanceData> entry : rawData.entries()) {
            String name = entry.key;
            AppearanceData data = entry.value;

            Texture texture = GameTextureAssets.getTexture(data.appearance, Texture.class);
            KillableAppearance appearance = new KillableAppearance(texture, data.width, data.height);
            AppearanceAssets.getInstance().registerAppearance(name, appearance);
        }

        Gdx.app.log("AppearanceLoader", "✅ Apparences chargées depuis " + path);
    }


    public List<WaveEntry> getWaveEntries(String waveId) {
        return loadJsonList("waves/" + waveId + ".json", WaveEntry.class);
    }

    private FileHandle resolve(String path) {
        return Gdx.files.internal(path);
    }

    public static class MonsterData {
        public int hp;
        public int speed;
        public int damage;
        public int reward;
        public String appearance;
    }

    public static class TowerData {
        public int range;
        public int damage;
        public float cooldown;
        public int cost;
        public String appearance;
    }

    public static class AppearanceData {
        public float width;
        public float height;
        public String appearance;
    }
}

