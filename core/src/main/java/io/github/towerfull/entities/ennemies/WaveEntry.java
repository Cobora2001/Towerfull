// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: Represents one entry in a wave, containing the type of monster and its relative spawn time.
// -------------------------------------------------------------------------------------

package io.github.towerfull.entities.ennemies;

import io.github.towerfull.enumElements.MonsterType;

/**
 * Represents one entry in a wave, containing the type of monster and its relative spawn time.
 */
public class WaveEntry {
    // The type of monster to spawn in this entry
    private MonsterType type;

    // The relative spawn time of the monster in this wave
    private float relativeSpawnTime;

    /**
     * Constructs a WaveEntry with the specified monster type and relative spawn time.
     *
     * @param type              The type of monster to spawn.
     * @param relativeSpawnTime The relative spawn time of the monster in this wave.
     */
    public WaveEntry(MonsterType type, float relativeSpawnTime) {
        this.type = type;
        this.relativeSpawnTime = relativeSpawnTime;
    }

    /**
     * Gets the type of monster for this wave entry.
     *
     * @return The type of monster.
     */
    public MonsterType getType() {
        return type;
    }

    /**
     * Gets the relative spawn time of the monster in this wave entry.
     *
     * @return The relative spawn time.
     */
    public float getRelativeSpawnTime() {
        return relativeSpawnTime;
    }

    /**
     * Sets the type of monster for this wave entry.
     *
     * @param type The type of monster to set.
     */
    public void setType(MonsterType type) { this.type = type; }

    /**
     * Sets the relative spawn time of the monster in this wave entry.
     *
     * @param relativeSpawnTime The relative spawn time to set.
     */
    public void setRelativeSpawnTime(float relativeSpawnTime) { this.relativeSpawnTime = relativeSpawnTime; }
}
