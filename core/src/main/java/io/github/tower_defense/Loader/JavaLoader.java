package io.github.tower_defense.Loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import io.github.tower_defense.Prototype.*;
import java.util.ArrayList;
import java.util.List;

public class JavaLoader {

    private static final JavaLoader instance = new JavaLoader();
    private final Json json;

    private JavaLoader() {
        json = new Json();
        json.setIgnoreUnknownFields(true);
        json.setEnumNames(true);
    }

    public static JavaLoader get() {
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

            KillableAppearance appearance;

            if (data.texture != null) {
                Texture region = GameAssets.getTexture(data.texture, Texture.class);
                appearance = new KillableAppearance(region, data.width, data.height);
            } else {
                throw new RuntimeException("Missing texture for monster!");
            }

            Monster m = new Monster(
                    data.hp, data.hp,
                    new Vector2(0, 0), // Logical position will be set later
                    data.speed, data.damage, 0, // Reward is not defined in prototype
                    appearance
            );
            factory.register(type, m);
        }
    }

    public static class MonsterPrototype {
        public int speed;
        public int hp;
        public int damage;
        public float width;
        public float height;
        public String texture;
    }
}
