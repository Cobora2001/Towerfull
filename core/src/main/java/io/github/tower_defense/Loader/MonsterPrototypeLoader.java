package io.github.tower_defense.Loader;

import io.github.tower_defense.Prototype.Monster;
import io.github.tower_defense.Prototype.MonsterType;

public class MonsterPrototypeLoader extends AbstractPrototypeLoader<JsonLoader.MonsterPrototype, Monster, MonsterType> {

    @Override
    protected Monster createInstance(MonsterType type, JsonLoader.MonsterPrototype proto) {
        return new Monster(proto.hp, proto.hp, null, proto.speed, proto.damage, proto.reward,
                AppearanceAssets.getInstance().getAppearance(proto.appearance));
    }
}
