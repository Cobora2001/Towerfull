// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: A central controller for managing the construction and destruction of towers,
//      as well as labeling in the game.
// -------------------------------------------------------------------------------------

package io.github.towerfull.screen.accessories;

import io.github.towerfull.Main;
import io.github.towerfull.entities.defenses.BuildSpot;
import io.github.towerfull.entities.defenses.Tower;
import io.github.towerfull.enumElements.TowerType;
import io.github.towerfull.gameBoard.GameArea;
import io.github.towerfull.listener.GoldListener;
import io.github.towerfull.listener.LifeListener;
import io.github.towerfull.tools.GameAssets;

import java.util.function.Consumer;

import static java.lang.Math.max;

/**
 * A central controller for managing the construction and destruction of towers,
 * as well as handling game state changes like gold and life updates.
 */
public class UiController {
    // The spot where the current tower is being built or managed.
    private BuildSpot currentSpot;

    // Callbacks for gold and life changes, allowing external components to react to these changes.
    private Consumer<Integer> goldChangedCallback;
    private Consumer<Integer> lifeChangedCallback;

    // The game area where the construction takes place, providing access to economy and game state.
    private final GameArea gameArea;

    // Reference to the main game instance, used for screen transitions and other game-wide actions.
    private final Main game;

    /**
     * Constructs a ConstructionController for managing tower construction in the specified game area.
     *
     * @param gameArea The game area where towers can be constructed.
     * @param game The main game instance for screen management and other actions.
     */
    public UiController(GameArea gameArea, Main game) {
        this.gameArea = gameArea;
        this.game = game;

        gameArea.getEconomyManager().addListener(new GoldListener() {
            @Override
            public void onGoldChanged(int newGold) {
                if(goldChangedCallback != null) {
                    goldChangedCallback.accept(newGold);
                }
            }
        });

        gameArea.addLifeListener(new LifeListener() {
            @Override
            public void onLifeChanged(int newLife) {
                if(lifeChangedCallback != null) {
                    lifeChangedCallback.accept(newLife);
                }
            }
        });
    }

    /**
     * Sets a callback to be invoked when the gold amount changes.
     *
     * @param callback The callback to invoke with the new gold amount.
     */
    public void setGoldListener(Consumer<Integer> callback) {
        this.goldChangedCallback = callback;
    }

    /**
     * Sets a callback to be invoked when the life amount changes.
     *
     * @param callback The callback to invoke with the new life amount.
     */
    public void setLifeListener(Consumer<Integer> callback) {
        this.lifeChangedCallback = callback;
    }

    /**
     * Displays the construction menu for a specific build spot.
     *
     * @param menu The construction menu to display.
     * @param spot The build spot where the tower will be constructed.
     */
    public void showMenu(ConstructionMenu menu, BuildSpot spot) {
        this.currentSpot = spot;
        updateMenuButtons(menu);
        menu.setVisible(true);
    }

    /**
     * Handles the selection of a tower type from the construction menu.
     * If the current spot is available, it creates a tower of the selected type
     * and deducts the cost from the economy.
     *
     * @param type The type of tower to construct.
     * @param menu The construction menu that was used for selection.
     */
    public void handleSelection(TowerType type, ConstructionMenu menu) {
        if(currentSpot != null && !currentSpot.isUsed()) {
            Tower t = GameAssets.get().towerFactory.create(type);
            if(t != null && gameArea.getEconomyManager().spendGold(t.getCost())) {
                currentSpot.setTower(t);
                currentSpot.setUsed(true);
            }
        }
        currentSpot = null;
        menu.setVisible(false);
    }

    /**
     * Cancels the current construction operation and hides the menu.
     *
     * @param menu The construction menu to hide.
     */
    public void cancel(ConstructionMenu menu) {
        currentSpot = null;
        menu.setVisible(false);
    }

    /**
     * Sets the paused state of the game area.
     *
     * @param paused True to pause the game, false to resume.
     */
    public void setPaused(boolean paused) {
        gameArea.setPaused(paused);
    }

    /**
     * Checks if the game area is currently paused.
     *
     * @return True if the game area is paused, false otherwise.
     */
    public boolean isPaused() {
        return gameArea.isPaused();
    }

    /**
     * Gets the current build spot being managed by this controller.
     *
     * @return The current build spot, or null if none is selected.
     */
    public GameArea getGameArea() {
        return gameArea;
    }

    /**
     * Exits to the main menu of the game.
     */
    public void exitToMenu() {
        game.setScreen(new io.github.towerfull.screen.MainMenuScreen(game));
    }

    /**
     * Updates the enabled state of buttons in the construction menu based on the current economy.
     *
     * @param menu The construction menu to update.
     */
    public void updateMenuButtons(ConstructionMenu menu) {
        for(TowerType type : TowerType.values()) {
            Tower t = GameAssets.get().towerFactory.getPrototype(type);
            menu.setEnabled(type, gameArea.getEconomyManager().canAfford(t.getCost()));
        }
    }

    /**
     * Sells the tower at the specified build spot, refunding half of its cost to the economy.
     *
     * @param spot The build spot where the tower is located.
     */
    public void sellTower(BuildSpot spot) {
        Tower tower = spot.getTower();
        if(tower != null) {
            int refund = max(tower.getCost() / 2,0);
            gameArea.getEconomyManager().earnGold(refund);
            spot.setUsed(false);
        }
    }
}
