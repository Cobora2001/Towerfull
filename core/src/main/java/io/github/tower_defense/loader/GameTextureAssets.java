package io.github.tower_defense.loader;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

public class GameTextureAssets {
    private static final AssetManager manager = new AssetManager();

    public static void loadFromAppearanceData(String appearanceJsonPath) {
        Json json = new Json();
        FileHandle file = resolve(appearanceJsonPath);
        ObjectMap<String, JsonLoader.AppearanceData> map =
                json.fromJson(ObjectMap.class, JsonLoader.AppearanceData.class, file);

        for (ObjectMap.Entry<String, JsonLoader.AppearanceData> entry : map.entries()) {
            String texturePath = entry.value.appearance;
            if (!manager.isLoaded(texturePath)) {
                manager.load(texturePath, Texture.class);
            }
        }
    }

    public static Texture getTexture(String path, Class<Texture> textureClass) {
        if (!manager.isLoaded(path)) {
            throw new IllegalArgumentException("Asset not loaded: " + path);
        }
        return manager.get(path, Texture.class);
    }

    public static void finishLoading() {
        manager.finishLoading();
    }

    public static void dispose() {
        manager.dispose();
    }

    private static FileHandle resolve(String path) {
        return com.badlogic.gdx.Gdx.files.internal(path);
    }
}
