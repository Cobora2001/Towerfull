package io.github.tower_defense.Loader;

import io.github.tower_defense.Prototype.Tower;
import io.github.tower_defense.Prototype.TowerType;

public class TowerPrototypeLoader extends AbstractPrototypeLoader<Tower, TowerType> {

    @Override
    protected Tower createInstance(TowerType type, Tower prototypeData) {
        return new Tower(prototypeData);
    }
}
