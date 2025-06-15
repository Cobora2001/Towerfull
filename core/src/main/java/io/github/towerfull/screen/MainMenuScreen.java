// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: The main screen of the game, where players can start a new game, load a saved game,
//      mute/unmute music, or quit the game.
// -------------------------------------------------------------------------------------

package io.github.towerfull.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.towerfull.Main;
import io.github.towerfull.tools.GameAssets;

/**
 * The main menu screen of the game.
 * This screen allows players to start a new game, load a saved game,
 * mute/unmute music, or quit the game.
 */
public class MainMenuScreen implements Screen {
    // The stage for rendering UI elements
    private final Stage stage;

    /**
     * Constructor for the MainMenuScreen.
     * Initializes the stage and sets up the UI elements.
     *
     * @param game The main game instance.
     */
    public MainMenuScreen(Main game) {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Skin skin = GameAssets.get().skin;

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        // Title Label
        Label titleLabel = new Label("Tower Defense: Towerfull", skin);
        titleLabel.setFontScale(2.0f);
        table.add(titleLabel).padBottom(40).center().row();

        // Buttons
        TextButton startButton = new TextButton("Start", skin);
        TextButton loadButton = new TextButton("Load", skin);
        TextButton muteButton = new TextButton(GameAssets.get().isMusicMuted() ? "Unmute" : "Mute", skin);
        TextButton quitButton = new TextButton("Quit", skin);

        startButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LevelSelectScreen(game));
            }
        });

        loadButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LoadScreen(game));
            }
        });

        muteButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                GameAssets.get().toggleMusic();
                muteButton.setText(GameAssets.get().isMusicMuted() ? "Unmute" : "Mute");
            }
        });

        quitButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // Add buttons to table
        table.add(startButton).width(200).pad(10).row();
        table.add(loadButton).width(200).pad(10).row();
        table.add(muteButton).width(200).pad(10).row();
        table.add(quitButton).width(200).pad(10).row();

        stage.addActor(table);
    }

    /**
     * Called when the screen is first shown.
     * This method is empty as we don't need to perform any actions on show.
     */
    @Override
    public void show() {}

    /**
     * Renders the screen.
     * Clears the screen and draws the stage.
     *
     * @param delta The time since the last render.
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    /**
     * Called when the screen is resized.
     * Updates the viewport of the stage.
     *
     * @param width  The new width of the screen.
     * @param height The new height of the screen.
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /**
     * Called when we close the screen.
     */
    @Override
    public void dispose() {
        stage.dispose();
    }

    /**
     * Called when the screen is paused.
     * This method is empty as we don't need to perform any actions on pause.
     */
    @Override public void pause() {}

    /**
     * Called when the screen is resumed.
     * This method is empty as we don't need to perform any actions on resume.
     */
    @Override public void resume() {}

    /**
     * Called when the screen is hidden.
     * This method is empty as we don't need to perform any actions on hide.
     */
    @Override public void hide() {}
}
