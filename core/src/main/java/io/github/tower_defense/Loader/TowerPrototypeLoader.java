package io.github.tower_defense.Loader;

import io.github.tower_defense.Prototype.PrototypeFactory;
import io.github.tower_defense.Prototype.Tower;
import io.github.tower_defense.Prototype.TowerType;

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
