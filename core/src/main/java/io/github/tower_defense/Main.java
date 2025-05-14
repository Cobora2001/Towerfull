package io.github.tower_defense;

import com.badlogic.gdx.Game;
import io.github.tower_defense.Screen.MainMenuScreen;

public class Main extends Game {
    @Override
    public void create() {
        this.setScreen(new MainMenuScreen(this));
    }
}
