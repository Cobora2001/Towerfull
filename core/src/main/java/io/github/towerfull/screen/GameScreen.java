package io.github.towerfull.screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import io.github.towerfull.entities.defenses.BuildSpot;
import io.github.towerfull.gameBoard.GameArea;
import io.github.towerfull.listener.LevelListener;
import io.github.towerfull.gameBoard.level.Level;
import io.github.towerfull.Main;
import io.github.towerfull.screen.accessories.ConstructionController;
import io.github.towerfull.screen.accessories.GameUI;
import io.github.towerfull.tools.GameRenderer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameScreen implements Screen {
    private final Main game;
    private final GameArea gameArea;
    private GameRenderer gameRenderer;
    private GameUI gameUI;

    private ConstructionController constructionController;

    public GameScreen(Main game, Level level) {
        this.game = game;
        this.gameArea = new GameArea(level);

        setupUI();
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

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

        setupUI();
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void setupUI() {
        constructionController = new ConstructionController(gameArea, game);

        // Create UI with fresh viewport and optional custom skin loader
        gameUI = new GameUI(
            new Skin(Gdx.files.internal("uiskin.json")),
            new ScreenViewport(),
            gameArea.getEconomyManager(),
            constructionController
        );

        gameUI.updateGold(gameArea.getEconomyManager().getGold());
        gameUI.updateLife(gameArea.getLife());

        Gdx.input.setInputProcessor(gameUI.getStage());

        constructionController.setGoldListener(newGold -> {
            constructionController.updateMenuButtons(gameUI.getConstructionMenu());
            gameUI.updateGold(newGold);
        });

        constructionController.setLifeListener(newLife -> gameUI.updateLife(newLife));


    }

    private void checkBuildSpotClick() {
        if (Gdx.input.justTouched()) {
            Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            gameUI.getStage().screenToStageCoordinates(mouse);

            Vector2 logical = gameRenderer.pixelToLogical(mouse);
            int clickX = (int) logical.x;
            int clickY = (int) logical.y;

            for (BuildSpot spot : gameArea.getBuildSpots()) {
                Vector2 spotPos = spot.getLogicalPos();
                int spotX = (int) spotPos.x;
                int spotY = (int) spotPos.y;

                if (clickX == spotX && clickY == spotY) {
                    if (!spot.isUsed()) {
                        constructionController.showMenu(gameUI.getConstructionMenu(), spot);
                        gameUI.hideDestructionMenu();
                        gameUI.showConstructionMenu(); // ✅ THIS LINE IS MISSING
                    } else {
                        gameUI.getDestructionMenu().showForTower(spot, null);
                        gameUI.hideConstructionMenu(); // ✅ RECOMMENDED
                        gameUI.showDestructionMenu();
                    }
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

        checkBuildSpotClick();
        constructionController.updateMenuButtons(gameUI.getConstructionMenu());

        gameUI.act(delta);
        gameUI.draw();
    }

    @Override
    public void resize(int width, int height) {
        gameUI.resize(width, height);

        int cols = gameArea.getCols();
        int rows = gameArea.getRows();

        // Use fixed sidebar width instead of ratio
        float uiWidth = GameUI.getSidebarWidth();
        float availableWidth = width - uiWidth;
        float availableHeight = height;

        float levelAspect = (float) cols / rows;
        float screenAspect = availableWidth / availableHeight;

        float cellSize = (screenAspect >= levelAspect)
            ? availableHeight / rows
            : availableWidth / cols;

        float totalGameWidth = cols * cellSize;
        float totalGameHeight = rows * cellSize;

        float gameStartX = (availableWidth - totalGameWidth) / 2f;
        float gameStartY = (availableHeight - totalGameHeight) / 2f;

        gameRenderer = new GameRenderer(
            gameArea,
            new Vector2(gameStartX, gameStartY),
            cellSize,
            cellSize
        );
    }

    @Override public void dispose() {
        gameUI.dispose();
    }

    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
}
