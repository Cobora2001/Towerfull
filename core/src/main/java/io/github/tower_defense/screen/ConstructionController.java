package io.github.tower_defense.screen;


import io.github.tower_defense.listener.GoldListener;
import io.github.tower_defense.prototype.*;
import io.github.tower_defense.service.AssetLoaderService;

import java.util.function.Consumer;

public class ConstructionController {
    private final EconomyManager economy;
    private final PrototypeFactory<TowerType, Tower> factory;
    private BuildSpot currentSpot;
    private Consumer<Integer> goldChangedCallback;

    public ConstructionController(EconomyManager economy, AssetLoaderService loader) {
        this.economy = economy;
        this.factory = loader.getTowerFactory();

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
            Tower t = factory.create(type);
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
            Tower t = factory.create(type);
            menu.setEnabled(type, economy.canAfford(t.getCost()));
        }
    }
}
