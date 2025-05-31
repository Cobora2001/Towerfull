package io.github.tower_defense;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import io.github.tower_defense.Loader.GameAssets;
import io.github.tower_defense.Screen.MainMenuScreen;

public class Main extends Game {
    @Override
    public void create() {
        // Initialize the game assets
        GameAssets.load();
        GameAssets.finishLoading();
        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose() {
        // Dispose of the game assets when the game is closed
        GameAssets.dispose();
        super.dispose();
    }
}
