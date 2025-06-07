package io.github.tower_defense.Loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import io.github.tower_defense.Prototype.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
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
        return JsonListLoader.loadJsonList(path, clazz);
    }

    public void loadMonsterPrototypes(String path, PrototypeFactory<MonsterType, Monster> factory) {
        new MonsterPrototypeLoader().load(path, factory);
    }

    public void loadTowerPrototypes(String path, PrototypeFactory<TowerType, Tower> factory) {
        new TowerPrototypeLoader().load(path, factory);
    }

    public List<WaveEntry> getWaveEntries(String waveId) {
        return loadJsonList("waves/" + waveId + ".json", WaveEntry.class);
    }

    private FileHandle resolve(String path) {
        return com.badlogic.gdx.Gdx.files.internal(path);
    }
}

