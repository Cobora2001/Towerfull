package io.github.tower_defense.Loader;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class GameAssets {
    private static final AssetManager manager = new AssetManager();

    public static void load() {
        // Load monster textures
        manager.load("monsters/goblin.PNG", Texture.class);
        manager.load("monsters/golem.png", Texture.class);
        manager.load("monsters/orc.png", Texture.class);
        manager.load("monsters/shaman.PNG", Texture.class);
        manager.load("monsters/skeleton.png", Texture.class);
        manager.load("monsters/wizard.png", Texture.class);
        manager.load("monsters/wolf.PNG", Texture.class);

        // Load tower textures
        manager.load("towers/castle.png", Texture.class);
        manager.load("towers/sniper.png", Texture.class);
        manager.load("towers/spot.png", Texture.class);
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
}
