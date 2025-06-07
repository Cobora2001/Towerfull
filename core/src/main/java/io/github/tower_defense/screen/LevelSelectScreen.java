package io.github.tower_defense.screen;

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
import io.github.tower_defense.level.Level;
import io.github.tower_defense.level.LevelFactory;
import io.github.tower_defense.Main;

public class LevelSelectScreen implements Screen {
    private final Main game;
    private Stage stage;
    private Skin skin;
    private Table table;

    public LevelSelectScreen(Main game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));
        table = new Table();
        table.setFillParent(true);
        table.center();

        Label title = new Label("Select a Level", skin);
        table.add(title).colspan(2).padBottom(20).row();

        // Level buttons
        addLevelButton("Level 1", LevelFactory.makeLevel1());
        addLevelButton("Level 2", LevelFactory.makeLevel2());
        addLevelButton("Generated Level", LevelFactory.createGeneratedLevel(20, 20));

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
