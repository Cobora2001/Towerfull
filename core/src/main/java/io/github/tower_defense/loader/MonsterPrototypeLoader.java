package io.github.tower_defense.loader;

import io.github.tower_defense.prototype.Monster;
import io.github.tower_defense.prototype.MonsterType;

public class MonsterPrototypeLoader extends AbstractPrototypeLoader<Monster, JsonLoader.MonsterData, MonsterType> {
    @Override
    protected Monster createInstance(MonsterType type, JsonLoader.MonsterData data) {
        return new Monster(
                data.hp,
                data.hp,
                null, // logicalPos (sera set plus tard)
                data.speed,
                data.damage,
                data.reward,
                AppearanceAssets.getInstance().getAppearance(data.appearance)
        );
    }
}

