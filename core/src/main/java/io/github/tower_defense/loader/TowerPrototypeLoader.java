package io.github.tower_defense.loader;

import io.github.tower_defense.prototype.Tower;
import io.github.tower_defense.prototype.TowerType;

public class TowerPrototypeLoader extends AbstractPrototypeLoader<Tower, JsonLoader.TowerData, TowerType> {
    @Override
    protected Tower createInstance(TowerType type, JsonLoader.TowerData data) {
        return new Tower(
                data.range,
                data.damage,
                data.cooldown,
                data.cost,
                AppearanceAssets.getInstance().getAppearance(data.appearance)
        );
    }
}
