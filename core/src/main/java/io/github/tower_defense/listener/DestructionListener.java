package io.github.tower_defense.listener;

import io.github.tower_defense.entities.defenses.BuildSpot;

public interface DestructionListener {
    void onSellTower(BuildSpot spot);
    void onCancel();
}
