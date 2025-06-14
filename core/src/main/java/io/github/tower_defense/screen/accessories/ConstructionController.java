package io.github.tower_defense.screen.accessories;

import io.github.tower_defense.Main;
import io.github.tower_defense.entities.defenses.BuildSpot;
import io.github.tower_defense.entities.defenses.Tower;
import io.github.tower_defense.enumElements.TowerType;
import io.github.tower_defense.gameBoard.GameArea;
import io.github.tower_defense.listener.GoldListener;
import io.github.tower_defense.listener.LifeListener;
import io.github.tower_defense.tools.GameAssets;

import java.util.function.Consumer;

import static java.lang.Math.max;

public class ConstructionController {
    private BuildSpot currentSpot;
    private Consumer<Integer> goldChangedCallback;
    private Consumer<Integer> lifeChangedCallback;

    private final GameArea gameArea;
    private final Main game;

    public ConstructionController(GameArea gameArea, Main game) {
        this.gameArea = gameArea;
        this.game = game;

        gameArea.getEconomyManager().addListener(new GoldListener() {
            @Override
            public void onGoldChanged(int newGold) {
                if (goldChangedCallback != null) {
                    goldChangedCallback.accept(newGold);
                }
            }
        });

        gameArea.addLifeListener(new LifeListener() {
            @Override
            public void onLifeChanged(int newLife) {
                if (lifeChangedCallback != null) {
                    lifeChangedCallback.accept(newLife);
                }
            }
        });
    }

    public void setGoldListener(Consumer<Integer> callback) {
        this.goldChangedCallback = callback;
    }

    public void setLifeListener(Consumer<Integer> callback) {
        this.lifeChangedCallback = callback;
    }

    public void showMenu(ConstructionMenu menu, BuildSpot spot) {
        this.currentSpot = spot;
        updateMenuButtons(menu);
        menu.setVisible(true);
    }

    public void handleSelection(TowerType type, ConstructionMenu menu) {
        if (currentSpot != null && !currentSpot.isUsed()) {
            Tower t = GameAssets.get().towerFactory.create(type);
            if (t != null && gameArea.getEconomyManager().spendGold(t.getCost())) {
                currentSpot.setTower(t);
                currentSpot.setUsed(true);
            }
        }
        currentSpot = null;
        menu.setVisible(false);
    }

    public void cancel(ConstructionMenu menu) {
        currentSpot = null;
        menu.setVisible(false);
    }

    public void setPaused(boolean paused) {
        gameArea.setPaused(paused);
    }

    public boolean isPaused() {
        return gameArea.isPaused();
    }

    public GameArea getGameArea() {
        return gameArea;
    }

    public void exitToMenu() {
        game.setScreen(new io.github.tower_defense.screen.MainMenuScreen(game));
    }


    public void updateMenuButtons(ConstructionMenu menu) {
        for (TowerType type : TowerType.values()) {
            Tower t = GameAssets.get().towerFactory.getPrototype(type);
            menu.setEnabled(type, gameArea.getEconomyManager().canAfford(t.getCost()));
        }
    }

    public void sellTower(BuildSpot spot) {
        Tower tower = spot.getTower();
        if (tower != null) {
            int refund = max(tower.getCost() / 2,0);
            gameArea.getEconomyManager().earnGold(refund);
            spot.setUsed(false);
        }
    }
}
