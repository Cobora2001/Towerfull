package io.github.towerfull.listener;

import io.github.towerfull.entities.defenses.BuildSpot;

public interface DestructionListener {
    void onSellTower(BuildSpot spot);
    void onCancel();
}
