package io.github.tower_defense.prototype;

public class WaveEntry {
    private MonsterType type;
    private float spawnTime;

    public WaveEntry() {
    }

    public WaveEntry(MonsterType type, float spawnTime) {
        this.type = type;
        this.spawnTime = spawnTime;
    }

    public Enum<MonsterType> getType() {
        return type;
    }

    public float getSpawnTime() {
        return spawnTime;
    }

    public void setType(MonsterType type) { this.type = type; }
    public void setSpawnTime(float spawnTime) { this.spawnTime = spawnTime; }
}
