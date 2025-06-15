package io.github.towerfull.entities.ennemies;

import io.github.towerfull.enumElements.MonsterType;

public class WaveEntry {
    private MonsterType type;
    private float relativeSpawnTime; // relative to the wave start time

    public WaveEntry(MonsterType type, float relativeSpawnTime) {
        this.type = type;
        this.relativeSpawnTime = relativeSpawnTime;
    }

    public MonsterType getType() {
        return type;
    }

    public float getRelativeSpawnTime() {
        return relativeSpawnTime;
    }

    public void setType(MonsterType type) { this.type = type; }
    public void setRelativeSpawnTime(float relativeSpawnTime) { this.relativeSpawnTime = relativeSpawnTime; }
}
