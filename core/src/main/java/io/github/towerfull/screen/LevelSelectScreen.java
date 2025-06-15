// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: The screen that allows the player to select a level to play.
// -------------------------------------------------------------------------------------

package io.github.towerfull.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.towerfull.Main;
import io.github.towerfull.enumElements.BackgroundId;
import io.github.towerfull.gameBoard.level.Level;
import io.github.towerfull.enumElements.LevelId;
import io.github.towerfull.gameBoard.level.generators.PathGenerator;
import io.github.towerfull.entities.ennemies.Scenario;
import io.github.towerfull.gameBoard.level.generators.TowerPlacementGenerator;
import io.github.towerfull.tools.GameAssets;

import java.util.*;

/**
 * Screen that allows the player to select a level to play.
 * It displays a list of available levels sorted by their display names,
 * including a button for a procedurally generated level.
 */
public class LevelSelectScreen implements Screen {
    // The main game instance
    private final Main game;

    // Stage for rendering UI elements
    private final Stage stage;

    // Skin for UI styling
    private final Skin skin;

    // Table to organize UI elements
    private final Table table;

    /**
     * Constructor for LevelSelectScreen.
     * @param game the main game instance
     */
    public LevelSelectScreen(Main game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        this.skin = GameAssets.get().skin;
        this.table = new Table();
        table.setFillParent(true);
        table.center();

        Label title = new Label("Select a Level", skin);
        table.add(title).colspan(2).padBottom(20).row();

        // Prepare and sort levels by display label
        Map<String, Level> displayNameToLevel = new TreeMap<>();
        for (LevelId levelId : GameAssets.get().levels.keySet()) {
            Level level = GameAssets.get().levels.get(levelId);
            String displayName = formatLevelName(levelId.name());
            displayNameToLevel.put(displayName, level);
        }

        // Add sorted buttons
        for (Map.Entry<String, Level> entry : displayNameToLevel.entrySet()) {
            addLevelButton(entry.getKey(), entry.getValue());
        }

        // Add procedural "Generated Level"
        addLevelButton("Generated level", createGeneratedLevel());

        // Back button
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        table.row().padTop(40);
        table.add(backButton).colspan(2).width(200);
        stage.addActor(table);
    }

    /**
     * Adds a button for a level to the table.
     * @param label the label for the button
     * @param level the level to start when the button is clicked
     */
    private void addLevelButton(String label, Level level) {
        TextButton button = new TextButton(label, skin);
        button.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game, level));
            }
        });
        table.row().pad(10);
        table.add(button).width(200).colspan(2);
    }

    /**
     * Creates a procedurally generated level with random parameters.
     * The level will have a random background and a path generated using the PathGenerator.
     * @return a new Level instance with generated parameters
     */
    private Level createGeneratedLevel() {
        int cols = 16;
        int rows = 16;
        Array<Vector2> path = PathGenerator.generatePath(cols, rows);
        Scenario scenario = GameAssets.get().scenarioFactory.getRandom();
        Array<Vector2> buildableTiles = TowerPlacementGenerator.generate(cols, rows, path);

        // Get a random value from the BackgroundId enum
        BackgroundId randomBackground = BackgroundId.values()[new Random().nextInt(BackgroundId.values().length)];

        return new Level(cols, rows, path, scenario, buildableTiles, 100, 20, GameAssets.get().backgrounds.get(randomBackground));
    }

    /**
     * Formats the level name by replacing underscores with spaces and capitalizing the first letter.
     * @param rawName the raw level name from the LevelId enum
     * @return a formatted string suitable for display
     */
    private String formatLevelName(String rawName) {
        String withSpaces = rawName.replace('_', ' ').toLowerCase();
        return withSpaces.substring(0, 1).toUpperCase() + withSpaces.substring(1);
    }

    /**
     * Called when the screen is shown.
     * Initializes the stage and sets the input processor.
     */
    @Override public void show() {}

    /**
     * Called when the screen is hidden.
     * Cleans up resources if necessary.
     */
    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    /**
     * Resizes the stage viewport to match the new width and height.
     * @param width the new width of the screen
     * @param height the new height of the screen
     */
    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /**
     * Disposes of the stage and skin resources.
     */
    @Override public void dispose() {
        stage.dispose();
    }

    /**
     * Called when the screen is paused.
     */
    @Override public void pause() {}

    /**
     * Called when the screen is resumed.
     * This method can be used to refresh the screen or resume any paused processes.
     */
    @Override public void resume() {}

    /**
     * Called when the screen is hidden.
     */
    @Override public void hide() {}
}
