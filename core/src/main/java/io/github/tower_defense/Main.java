package io.github.tower_defense;

import com.badlogic.gdx.Game;
import io.github.tower_defense.screen.MainMenuScreen;
import io.github.tower_defense.tools.GameAssets;

public class Main extends Game {
    @Override
    public void create() {
        // Initialize the game assets
        GameAssets.get().loadAll();

        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose() {
        // Dispose of game assets
        GameAssets.get().dispose();

        // Call the super dispose method
        super.dispose();
    }
}
