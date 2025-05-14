package io.github.tower_defense.Prototype;

import java.util.LinkedHashMap;
import java.util.Map;

public class Wave extends Prototype {
    private final Map<String, Integer> monsterCounts;
    private float spawnDelay = 1f;

    public Wave() {
        this.monsterCounts = new LinkedHashMap<>();
    }

    public Wave(Wave other) {
        this.monsterCounts = new LinkedHashMap<>(other.monsterCounts);
        this.spawnDelay = other.spawnDelay;
    }

    public void addMonster(String type, int count) {
        monsterCounts.put(type, monsterCounts.getOrDefault(type, 0) + count);
    }

    public Map<String, Integer> getMonsterCounts() {
        return monsterCounts;
    }

    public void setSpawnDelay(float delay) {
        this.spawnDelay = delay;
    }

    public float getSpawnDelay() {
        return spawnDelay;
    }

    @Override
    public Wave clone() {
        return new Wave(this);
    }
}
