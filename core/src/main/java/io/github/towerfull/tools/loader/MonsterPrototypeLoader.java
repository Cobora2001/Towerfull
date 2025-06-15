// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: Class to load monster prototypes from data files
// -------------------------------------------------------------------------------------

package io.github.towerfull.tools.loader;

import io.github.towerfull.entities.ennemies.Monster;
import io.github.towerfull.enumElements.MonsterType;
import io.github.towerfull.tools.GameAssets;
import io.github.towerfull.tools.data.MonsterData;

public class MonsterPrototypeLoader extends AbstractPrototypeLoader<Monster, MonsterData, MonsterType> {
    @Override
    protected Monster createInstance(MonsterType type, MonsterData data) {
        return new Monster(
                data.hp,
                data.speed,
                data.damage,
                data.reward,
                GameAssets.get().appearances.get(data.appearance)
        );
    }
}

