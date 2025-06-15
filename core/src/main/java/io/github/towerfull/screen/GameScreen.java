// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: Is the screen displayed when the player plays a level.
// -------------------------------------------------------------------------------------

package io.github.towerfull.screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;

import io.github.towerfull.entities.defenses.BuildSpot;
import io.github.towerfull.gameBoard.GameArea;
import io.github.towerfull.listener.LevelListener;
import io.github.towerfull.gameBoard.level.Level;
import io.github.towerfull.Main;
import io.github.towerfull.screen.accessories.UiController;
import io.github.towerfull.screen.accessories.GameUI;
import io.github.towerfull.tools.GameAssets;
import io.github.towerfull.tools.GameRenderer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * GameScreen is the main screen where the game is played.
 * It handles rendering the game area, user interface, and interactions.
 */
public class GameScreen implements Screen {
    // The main game instance
    private final Main game;

    // The game area containing the level, monsters, towers, etc.
    private final GameArea gameArea;

    // Renderer for the game area
    private GameRenderer gameRenderer;

    // The user interface for the game, including construction menus and HUD
    private GameUI gameUI;

    // Controller for handling construction actions like building towers
    private UiController constructionController;

    /**
     * Constructor for GameScreen with a specific level.
     * Initializes the game area and sets up the UI.
     *
     * @param game The main game instance
     * @param level The level to be played
     */
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

    /**
     * Constructor for GameScreen with an existing game area.
     * Initializes the UI and renderer based on the provided game area.
     *
     * @param game The main game instance
     * @param gameArea The existing game area to be displayed
     */
    public GameScreen(Main game, GameArea gameArea) {
        this.game = game;
        this.gameArea = gameArea;

        setupUI();
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    /**
     * Sets up the user interface for the game screen.
     * Initializes the construction controller and UI elements.
     */
    private void setupUI() {
        constructionController = new UiController(gameArea, game);

        // Create UI with fresh viewport and optional custom skin loader
        gameUI = new GameUI(
                GameAssets.get().skin,
            new ScreenViewport(),
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

    /**
     * Checks if a build spot was clicked and handles the interaction.
     * If a build spot is clicked, it shows the construction or destruction menu.
     */
    private void checkBuildSpotClick() {
        if(Gdx.input.justTouched()) {
            Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            gameUI.getStage().screenToStageCoordinates(mouse);

            Vector2 logical = gameRenderer.pixelToLogical(mouse);
            int clickX = (int) logical.x;
            int clickY = (int) logical.y;

            for(BuildSpot spot : gameArea.getBuildSpots()) {
                Vector2 spotPos = spot.getLogicalPos();
                int spotX = (int) spotPos.x;
                int spotY = (int) spotPos.y;

                if(clickX == spotX && clickY == spotY) {
                    if(!spot.isUsed()) {
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

    /**
     * Renders the game screen.
     * Clears the screen, updates the game area, and renders the game and UI.
     *
     * @param delta Time since the last frame
     */
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

    /**
     * This method is called when the screen is resized.
     * It recalculates the game area dimensions and updates the renderer accordingly.
     * @param width The new width of the screen
     * @param height The new height of the screen
     */
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

    /**
     * Disposes of the resources used by the game screen.
     * This method is called when the screen is no longer needed.
     */
    @Override public void dispose() {
        gameUI.dispose();
    }


    /**
     * This method is called when the screen is shown.
     */
    @Override public void show() {}

    /**
     * This method is called when the screen is hidden.
     * It can be used to pause the game or release resources.
     */
    @Override public void hide() {}

    /**
     * This method is called when the game is paused.
     */
    @Override public void pause() {}

    /**
     * This method is called when the game is resumed.
     * It can be used to restore the game state after a pause.
     */
    @Override public void resume() {}
}
