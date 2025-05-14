package io.github.tower_defense;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class Assets {
    public static AssetManager manager;

    public static void load() {
        manager = new AssetManager();
    }

    public static void finishLoading() {
        manager.finishLoading();
    }

    public static void dispose() {
        manager.dispose();
    }
}
