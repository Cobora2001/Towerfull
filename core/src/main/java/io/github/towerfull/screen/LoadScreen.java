// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: The screen that allows the player to select a level to play.
// -------------------------------------------------------------------------------------

package io.github.towerfull.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.towerfull.Main;
import io.github.towerfull.gameBoard.GameArea;
import io.github.towerfull.tools.GameAssets;
import io.github.towerfull.tools.SaveManager;

/**
 * LoadScreen is the screen that allows the player to load a previously saved game.
 * It displays a list of saved games with options to load or delete each save.
 */
public class LoadScreen extends ScreenAdapter {
    // The main game instance
    private final Main game;

    // The stage for rendering UI elements
    private Stage stage;

    // The skin used for UI elements
    private Skin skin;

    /**
     * Constructor for LoadScreen.
     *
     * @param game The main game instance.
     */
    public LoadScreen(Main game) {
        this.game = game;
    }

    /**
     * Initializes the screen, setting up the stage and UI elements.
     */
    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = GameAssets.get().skin;

        Table root = new Table();
        root.setFillParent(true);
        root.pad(20);
        stage.addActor(root);

        Label title = new Label("Load Game", skin);
        title.setFontScale(2);
        root.add(title).colspan(3).padBottom(20).row();

        ScrollPane scrollPane = createSaveList();
        root.add(scrollPane).colspan(3).expand().fill().padBottom(20).row();

        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        root.add(backButton).colspan(3).center();
    }

    /**
     * Creates a scrollable list of saved games.
     *
     * @return A ScrollPane containing the list of saved games.
     */
    private ScrollPane createSaveList() {
        Table saveTable = new Table();
        saveTable.align(Align.top);

        for(String saveName : SaveManager.getInstance().getSortedSaveNamesNewestFirst()) {
            Label nameLabel = new Label(saveName, skin);
            TextButton loadButton = new TextButton("Load", skin);
            TextButton deleteButton = new TextButton("Delete", skin);

            loadButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    GameArea gameArea = SaveManager.getInstance().getGameArea(saveName);
                    game.setScreen(new GameScreen(game, gameArea));
                }
            });

            deleteButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    SaveManager.getInstance().removeSave(saveName);
                    refresh(); // Refresh the list after deletion
                }
            });

            saveTable.add(nameLabel).left().pad(5);
            saveTable.add(loadButton).pad(5);
            saveTable.add(deleteButton).pad(5).row();
        }

        ScrollPane scrollPane = new ScrollPane(saveTable, skin);
        scrollPane.setFadeScrollBars(false);
        return scrollPane;
    }

    /**
     * Refreshes the save list by rebuilding the screen.
     * This is called after a save is deleted to update the displayed list.
     */
    private void refresh() {
        show(); // Rebuild screen
    }

    /**
     * Renders the screen.
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
     * This allows us to dispose of the stage
     */
    @Override
    public void dispose() {
        stage.dispose();
    }
}
