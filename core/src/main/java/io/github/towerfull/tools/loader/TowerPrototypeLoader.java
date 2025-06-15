// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: Class to load tower prototypes from JSON files
// -------------------------------------------------------------------------------------

package io.github.towerfull.tools.loader;

import io.github.towerfull.entities.defenses.Tower;
import io.github.towerfull.enumElements.TowerType;
import io.github.towerfull.tools.GameAssets;
import io.github.towerfull.tools.data.TowerData;

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
