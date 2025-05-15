package io.github.tower_defense.Screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.tower_defense.Prototype.GameArea;
import io.github.tower_defense.Level.Level;
import io.github.tower_defense.Main;

public class GameScreen implements Screen {
    private final Main game;

    private GameArea gameArea;
    private Stage uiStage;
    private Skin skin;

    private Table rootTable;
    private Table sidebarTable;

    private TextButton pauseButton;
    private TextButton resumeButton;

    private final int SIDEBAR_WIDTH = 200;

    public GameScreen(Main game, Level level) {
        this.game = game;
        this.gameArea = new GameArea();

        gameArea.setLevel(level);

        setupUI();
        gameArea.resize(Gdx.graphics.getWidth() - SIDEBAR_WIDTH, Gdx.graphics.getHeight());
    }

    private void setupUI() {
        uiStage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(uiStage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        rootTable = new Table();
        rootTable.setFillParent(true);
        uiStage.addActor(rootTable);

        sidebarTable = new Table();
        sidebarTable.top().pad(10);

        pauseButton = new TextButton("Pause", skin);
        resumeButton = new TextButton("Resume", skin);
        TextButton saveButton = new TextButton("Save", skin);
        TextButton quitButton = new TextButton("Quit", skin);

        pauseButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                gameArea.pause();
                updateButton();
            }
        });

        resumeButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                gameArea.resume();
                updateButton();
            }
        });

        quitButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        sidebarTable.add(pauseButton).padBottom(10).row();
        sidebarTable.add(saveButton).padBottom(10).row();
        sidebarTable.add(quitButton).padBottom(10).row();

        rootTable.add().expand().fill(); // Placeholder for game area
        rootTable.add(sidebarTable).width(SIDEBAR_WIDTH).fillY();
    }

    private void updateButton() {
        sidebarTable.getCells().get(0).clearActor(); // First cell
        if (gameArea.isPaused()) {
            sidebarTable.getCells().get(0).setActor(resumeButton);
        } else {
            sidebarTable.getCells().get(0).setActor(pauseButton);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameArea.update(delta);
        gameArea.render();

        uiStage.act(delta);
        uiStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        uiStage.getViewport().update(width, height, true);
        gameArea.resize(width - SIDEBAR_WIDTH, height);
    }

    @Override
    public void dispose() {
        gameArea.dispose();
        uiStage.dispose();
        skin.dispose();
    }

    @Override public void show() {
    }
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
}
