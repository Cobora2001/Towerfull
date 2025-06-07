package io.github.tower_defense;

import com.badlogic.gdx.Game;
import io.github.tower_defense.Loader.GameTextureAssets;
import io.github.tower_defense.Screen.MainMenuScreen;

public class Main extends Game {
    @Override
    public void create() {
        // Initialize the game assets
        GameTextureAssets.load();
        GameTextureAssets.finishLoading();
        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose() {
        // Dispose of the game assets when the game is closed
        GameTextureAssets.dispose();
        super.dispose();
    }
}
