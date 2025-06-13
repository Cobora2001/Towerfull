package io.github.tower_defense;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import io.github.tower_defense.screen.MainMenuScreen;
import io.github.tower_defense.tools.GameAssets;

public class Main extends Game {
    private Music bgMusic;

    public void setVolume(float volume) {
        if (bgMusic != null) {
            bgMusic.setVolume(volume);
        }
    }

    @Override
    public void create() {
        // Initialize the game assets
        GameAssets.get().loadAll();

        // Start background music
        bgMusic = Gdx.audio.newMusic(Gdx.files.internal("music/Age_of_war_theme.mp3"));
        bgMusic.setLooping(true);
        bgMusic.play();

        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose() {
        // Dispose of game assets
        GameAssets.get().dispose();

        // Stop and dispose of background music
        if (bgMusic != null) {
            bgMusic.stop();
            bgMusic.dispose();
        }

        // Call the super dispose method
        super.dispose();
    }
}
