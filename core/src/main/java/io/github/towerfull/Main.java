package io.github.towerfull;

import com.badlogic.gdx.Game;
import io.github.towerfull.screen.MainMenuScreen;
import io.github.towerfull.tools.GameAssets;

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
