package io.github.tower_defense.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.tower_defense.Main;
import io.github.tower_defense.tools.GameAssets;

public class MainMenuScreen implements Screen {

    private final Stage stage;
    private final Skin skin;

    public MainMenuScreen(Main game) {

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

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

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
