package io.github.tower_defense;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import io.github.tower_defense.screen.MainMenuScreen;
import io.github.tower_defense.tools.GameAssets;

public class Main extends Game {
    @Override
    public void create() {
        GameAssets.get().loadAll();
        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose() {
        GameAssets.get().dispose(); // handles music cleanup too
        super.dispose();
    }
}
