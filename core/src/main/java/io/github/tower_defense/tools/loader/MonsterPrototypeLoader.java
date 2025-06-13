package io.github.tower_defense.tools.loader;

import io.github.tower_defense.entities.ennemies.Monster;
import io.github.tower_defense.enumElements.MonsterType;
import io.github.tower_defense.tools.GameAssets;
import io.github.tower_defense.tools.data.MonsterData;

public class MonsterPrototypeLoader extends AbstractPrototypeLoader<Monster, MonsterData, MonsterType> {
    @Override
    protected Monster createInstance(MonsterType type, MonsterData data) {
        return new Monster(
                data.hp,
                data.hp,
                null, // logicalPos (sera set plus tard)
                data.speed,
                data.damage,
                data.reward,
                GameAssets.get().appearances.get(data.appearance)
        );
    }
}

