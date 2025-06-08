package io.github.tower_defense.screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.tower_defense.listener.LevelListener;
import io.github.tower_defense.prototype.GameArea;
import io.github.tower_defense.level.Level;


import io.github.tower_defense.prototype.*;
import io.github.tower_defense.Main;
import io.github.tower_defense.service.AssetLoaderService;

public class GameScreen implements Screen {
    private final Main game;

    private GameArea gameArea;
    private Stage uiStage;
    private Skin skin;

    private ConstructionMenu constructionMenu;
    private ConstructionController constructionController;
    private BuildSpot selectedSpot;
    private AssetLoaderService assetLoader;

    private Table rootTable;
    private Table sidebarTable;

    private TextButton pauseButton;
    private TextButton resumeButton;

    private Label goldLabel;
    private Label lifeLabel;

    private final int SIDEBAR_WIDTH = 200;

    public GameScreen(Main game, Level level) {
        this.game = game;
        this.gameArea = new GameArea();
        this.assetLoader = new AssetLoaderService();
        assetLoader.loadAllAssets();

        gameArea.setLevel(level);

        setupUI();
        setupConstruction();

        gameArea.resize(Gdx.graphics.getWidth() - SIDEBAR_WIDTH, Gdx.graphics.getHeight());

        gameArea.setLevelListener(new LevelListener() {
            @Override
            public void onGameOver() {
                Gdx.app.postRunnable(() ->
                        game.setScreen(new DefeatScreen(game))
                );
            }

            @Override
            public void onLevelComplete() {
                Gdx.app.postRunnable(() ->
                        game.setScreen(new VictoryScreen(game))
                );
            }
        });
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

        lifeLabel = new Label("Life: 20", skin);
        sidebarTable.add(lifeLabel).padBottom(10).row();

        goldLabel = new Label("Gold: "+ gameArea.getEconomyManager().getGold(), skin);
        sidebarTable.add(goldLabel).padBottom(8).row();

        constructionController = new ConstructionController(
                gameArea.getEconomyManager(),
                assetLoader
        );

        constructionController.setGoldListener(newGold -> {
            goldLabel.setText("Gold: " + newGold);
            constructionController.updateMenuButtons(constructionMenu);
        });

        sidebarTable.add(lifeLabel).padBottom(10).row();
        sidebarTable.add(goldLabel).padBottom(20).row();

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

    private void setupConstruction() {
        constructionController = new ConstructionController(gameArea.getEconomyManager(), assetLoader);

        constructionMenu = new ConstructionMenu(
                skin,
                gameArea.getEconomyManager(),
                assetLoader.getTowerFactory(),
                new ConstructionMenu.TowerSelectionListener() {
                    public void onTowerSelected(TowerType type) {
                        constructionController.handleSelection(type, constructionMenu);
                    }

                    public void onCancel() {
                        constructionController.cancel(constructionMenu);
                    }
                }
        );

        constructionMenu.defaults().pad(4).fillX().growX();

        sidebarTable.add(constructionMenu)
                .expandY()
                .fillX()
                .bottom()
                .pad(10)
                .row();

    }

    private void updateButton() {

        sidebarTable.getCells().get(0).clearActor(); // First cell
        if (gameArea.isPaused()) {
            sidebarTable.getCells().get(0).setActor(resumeButton);
        } else {
            sidebarTable.getCells().get(0).setActor(pauseButton);
        }
    }

    private void checkBuildSpotClick() {
        if (Gdx.input.justTouched()) {
            Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            uiStage.screenToStageCoordinates(mouse);
            Vector2 logical = gameArea.pixelToLogical(mouse);

            for (BuildSpot spot : gameArea.getBuildSpots()) {
                if (!spot.isUsed() && spot.getLogicalPos().dst(logical) < 0.5f) {
                    selectedSpot = spot;
                    constructionController.showMenu(constructionMenu, spot);
                    break;
                }
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameArea.update(delta);
        gameArea.render();

        lifeLabel.setText("Life: " + gameArea.getLife());

        checkBuildSpotClick();

        // mise à jour dynamique des boutons en fonction de l'argent
        constructionController.updateMenuButtons(constructionMenu);

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

    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
}
