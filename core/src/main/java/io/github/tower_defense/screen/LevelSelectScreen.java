package io.github.tower_defense.screen;

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
import io.github.tower_defense.Main;
import io.github.tower_defense.gameBoard.level.Level;
import io.github.tower_defense.enumElements.LevelId;
import io.github.tower_defense.gameBoard.level.generators.PathGenerator;
import io.github.tower_defense.entities.ennemies.Scenario;
import io.github.tower_defense.gameBoard.level.generators.TowerPlacementGenerator;
import io.github.tower_defense.tools.GameAssets;

import java.util.*;

public class LevelSelectScreen implements Screen {
    private final Main game;
    private final Stage stage;
    private final Skin skin;
    private final Table table;

    public LevelSelectScreen(Main game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        this.skin = new Skin(Gdx.files.internal("uiskin.json"));
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

    private Level createGeneratedLevel() {
        int cols = 16;
        int rows = 16;
        Array<Vector2> path = PathGenerator.generatePath(cols, rows);
        Scenario scenario = GameAssets.get().scenarioFactory.getRandom();
        Array<Vector2> buildableTiles = TowerPlacementGenerator.generate(cols, rows, path);
        return new Level(cols, rows, path, scenario, buildableTiles, 100, 20);
    }

    private String formatLevelName(String rawName) {
        String withSpaces = rawName.replace('_', ' ').toLowerCase();
        return withSpaces.substring(0, 1).toUpperCase() + withSpaces.substring(1);
    }

    @Override public void show() {}
    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }
    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    @Override public void dispose() {
        stage.dispose();
        skin.dispose();
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
