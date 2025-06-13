package io.github.tower_defense.tools.loader;

import io.github.tower_defense.entities.defenses.Tower;
import io.github.tower_defense.enumElements.TowerType;
import io.github.tower_defense.tools.GameAssets;
import io.github.tower_defense.tools.data.TowerData;

public class TowerPrototypeLoader extends AbstractPrototypeLoader<Tower, TowerData, TowerType> {
    @Override
    protected Tower createInstance(TowerType type, TowerData data) {
        return new Tower(
                data.range,
                data.damage,
                data.cooldown,
                data.cost,
                GameAssets.get().appearances.get(data.appearance)
        );
    }
}
