package io.github.tower_defense.screen.accessories;

import io.github.tower_defense.entities.defenses.BuildSpot;
import io.github.tower_defense.entities.defenses.Tower;
import io.github.tower_defense.enumElements.TowerType;
import io.github.tower_defense.gameBoard.EconomyManager;
import io.github.tower_defense.listener.GoldListener;
import io.github.tower_defense.tools.GameAssets;

import java.util.function.Consumer;

public class ConstructionController {
    private final EconomyManager economy;
    private BuildSpot currentSpot;
    private Consumer<Integer> goldChangedCallback;

    public ConstructionController(EconomyManager economy) {
        this.economy = economy;

        economy.addListener(new GoldListener() {
            @Override
            public void onGoldChanged(int newGold) {
                if (goldChangedCallback != null) {
                    goldChangedCallback.accept(newGold);
                }
            }
        });
    }

    public void setGoldListener(Consumer<Integer> callback) {
        this.goldChangedCallback = callback;
    }

    public void showMenu(ConstructionMenu menu, BuildSpot spot) {
        this.currentSpot = spot;
        updateMenuButtons(menu);
        menu.setVisible(true);
    }

    public void handleSelection(TowerType type, ConstructionMenu menu) {
        if (currentSpot != null && !currentSpot.isUsed()) {
            Tower t = GameAssets.get().towerFactory.create(type);
            if (t != null && economy.spendGold(t.getCost())) {
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

    public void updateMenuButtons(ConstructionMenu menu) {
        for (TowerType type : TowerType.values()) {
            Tower t = GameAssets.get().towerFactory.getPrototype(type);
            menu.setEnabled(type, economy.canAfford(t.getCost()));
        }
    }
}
