// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: Is the screen displayed when the player loses a level.
// -------------------------------------------------------------------------------------

package io.github.towerfull.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.towerfull.Main;
import io.github.towerfull.tools.GameAssets;

/**
 * DefeatScreen is displayed when the player loses a level.
 * It provides options to retry, return to the main menu, or quit the game.
 */
public class DefeatScreen implements Screen {
    // The screen displayed when the player loses a level.
    private final Stage stage;

    // The skin used for UI elements.
    private final Skin skin;

    /**
     * Constructor for DefeatScreen.
     * Initializes the stage, input processor, and UI elements.
     *
     * @param game The main game instance to switch screens.
     */
    public DefeatScreen(Main game){
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = GameAssets.get().skin;

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        Label title = new Label("Defeat...", skin);
        title.setFontScale(2f);
        table.add(title).padBottom(40).row();

        TextButton retry = new TextButton("Retry", skin);
        retry.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LevelSelectScreen(game));
            }
        });
        table.add(retry).width(250).pad(10).row();

        TextButton menu = new TextButton("Main Menu", skin);
        menu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        table.add(menu).width(250).pad(10).row();

        TextButton quit = new TextButton("Quit", skin);
        quit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        table.add(quit).width(250).pad(10).row();

        stage.addActor(table);
    }

    /**
     * This method is called when the screen is first shown.
     */
    @Override
    public void show() {}

    /**
     * The render method is called every frame to update and draw the screen.
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.6f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    /**
     * This method is called when the screen is resized.
     * @param width The new width of the screen.
     * @param height The new height of the screen.
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /**
     * This method is called when the screen is paused.
     */
    @Override
    public void pause() {}

    /**
     * This method is called when the screen is resumed.
     */
    @Override
    public void resume() {}

    /**
     * This method is called when the screen is hidden.
     * It can be used to clean up resources or stop animations.
     */
    @Override
    public void hide() {}

    /**
     * This method is called to dispose of the screen's resources.
     * It should be called when the screen is no longer needed.
     */
    @Override
    public void dispose() {
        stage.dispose();
    }
}
