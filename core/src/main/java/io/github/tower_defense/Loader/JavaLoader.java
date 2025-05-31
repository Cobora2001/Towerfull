package io.github.tower_defense.Loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import io.github.tower_defense.Prototype.*;
import java.util.ArrayList;
import java.util.List;

public class Assets {

    private static final Assets instance = new Assets();
    private final Json json;

    private Assets() {
        json = new Json();
        json.setIgnoreUnknownFields(true);
        json.setEnumNames(true);
    }

    public static Assets get() {
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

    public void loadMonsterPrototypes(String path, PrototypeFactory<MonsterType, Monster> factory) {
        FileHandle file = Gdx.files.internal(path);
        ObjectMap<String, MonsterPrototype> dataMap = json.fromJson(ObjectMap.class, MonsterPrototype.class, file);

        for (ObjectMap.Entry<String, MonsterPrototype> entry : dataMap.entries()) {
            MonsterType type = MonsterType.valueOf(entry.key);
            MonsterPrototype data = entry.value;
            Monster m = new Monster(data.width, data.height, new com.badlogic.gdx.math.Vector2(0, 0), data.speed, data.hp, data.damage);
            factory.register(type, m);
        }
    }

    public static class MonsterPrototype {
        public int width;
        public int height;
        public int speed;
        public int hp;
        public int damage;
    }
}
