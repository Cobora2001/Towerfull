package io.github.tower_defense.screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.tower_defense.listener.LevelListener;
import io.github.tower_defense.prototype.*;
import io.github.tower_defense.level.Level;
import io.github.tower_defense.Main;
import io.github.tower_defense.service.GameRenderer;
import io.github.tower_defense.service.AssetLoaderService;
import io.github.tower_defense.service.SaveManager;

import java.util.Vector;

public class GameScreen implements Screen {
    private final Main game;

    private GameArea gameArea;
    private GameRenderer gameRenderer;
    private Stage uiStage;
    private Skin skin;

    private Vector<GameArea> gameAreas = new Vector<>();

    private ConstructionMenu constructionMenu;
    private ConstructionController constructionController;
    private AssetLoaderService assetLoader;

    private Table rootTable;
    private Table sidebarTable;

    private TextButton pauseButton;
    private TextButton resumeButton;

    private Cell<TextButton> pauseResumeCell;

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

        gameArea.setLevelListener(new LevelListener() {
            @Override
            public void onGameOver() {
                Gdx.app.postRunnable(() -> game.setScreen(new DefeatScreen(game)));
            }

            @Override
            public void onLevelComplete() {
                Gdx.app.postRunnable(() -> game.setScreen(new VictoryScreen(game)));
            }
        });
    }

    public GameScreen(Main game, GameArea gameArea) {
        this.game = game;
        this.gameArea = gameArea;
        this.assetLoader = new AssetLoaderService();
        assetLoader.loadAllAssets();

        setupUI();
        setupConstruction();

        int width = Gdx.graphics.getWidth() - SIDEBAR_WIDTH;
        int height = Gdx.graphics.getHeight();

        gameRenderer = new GameRenderer(
            gameArea,
            new Vector2(0, 0),
            (float) width / gameArea.getCols(),
            (float) height / gameArea.getRows()
        );

        gameArea.setLevelListener(new LevelListener() {
            @Override
            public void onGameOver() {
                Gdx.app.postRunnable(() -> game.setScreen(new DefeatScreen(game)));
            }

            @Override
            public void onLevelComplete() {
                Gdx.app.postRunnable(() -> game.setScreen(new VictoryScreen(game)));
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
        goldLabel = new Label("Gold: " + gameArea.getEconomyManager().getGold(), skin);

        sidebarTable.add(lifeLabel).padBottom(10).row();
        sidebarTable.add(goldLabel).padBottom(20).row();

        constructionController = new ConstructionController(
            gameArea.getEconomyManager(),
            assetLoader
        );

        constructionController.setGoldListener(newGold -> {
            goldLabel.setText("Gold: " + newGold);
            constructionController.updateMenuButtons(constructionMenu);
        });

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

        saveButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                saveGame();
                Gdx.app.log("GameScreen", "Game saved successfully!");
            }
        });

        quitButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        // ✅ Store reference to the pause/resume button cell
        pauseResumeCell = sidebarTable.add(pauseButton).padBottom(10);
        sidebarTable.row();

        sidebarTable.add(saveButton).padBottom(10).row();
        sidebarTable.add(quitButton).padBottom(10).row();

        rootTable.add().expand().fill();
        rootTable.add(sidebarTable).width(SIDEBAR_WIDTH).fillY();
    }

    private void updateButton() {
        if (pauseResumeCell != null) {
            pauseResumeCell.setActor(gameArea.isPaused() ? resumeButton : pauseButton);
        }
    }

    private void saveGame() {
        SaveManager.getInstance().saveGameWithTimestamp(gameArea);
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

    private void checkBuildSpotClick() {
        if (Gdx.input.justTouched()) {
            Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            uiStage.screenToStageCoordinates(mouse);

            Vector2 logical = gameRenderer.pixelToLogical(mouse);

            for (BuildSpot spot : gameArea.getBuildSpots()) {
                if (spot.isUsed()) continue;

                Vector2 spotPos = spot.getLogicalPos();
                int spotX = (int) spotPos.x;
                int spotY = (int) spotPos.y;

                int clickX = (int) logical.x;
                int clickY = (int) logical.y;

                if (clickX == spotX && clickY == spotY) {
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
        gameRenderer.render();

        lifeLabel.setText("Life: " + gameArea.getLife());

        checkBuildSpotClick();
        constructionController.updateMenuButtons(constructionMenu);

        uiStage.act(delta);
        uiStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        uiStage.getViewport().update(width, height, true);

        int cols = gameArea.getCols();
        int rows = gameArea.getRows();

        float availableWidth = width - SIDEBAR_WIDTH;
        float availableHeight = height;

        float levelAspect = (float) cols / rows;
        float screenAspect = availableWidth / availableHeight;

        float cellSize = (screenAspect >= levelAspect)
            ? availableHeight / rows
            : availableWidth / cols;

        float totalGameWidth = cols * cellSize;
        float totalGameHeight = rows * cellSize;

        float gameStartX = (availableWidth - totalGameWidth) / 2f;
        float gameStartY = (height - totalGameHeight) / 2f;

        gameRenderer = new GameRenderer(
            gameArea,
            new Vector2(gameStartX, gameStartY),
            cellSize,
            cellSize
        );
    }

    @Override public void dispose() {
        uiStage.dispose();
        skin.dispose();
    }

    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
}
