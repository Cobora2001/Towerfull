// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: Represents the arrival of waves of monsters in the game.
// -------------------------------------------------------------------------------------

package io.github.towerfull.entities.ennemies;

import com.badlogic.gdx.Gdx;
import io.github.towerfull.gameBoard.GameArea;
import io.github.towerfull.tools.Prototype;
import io.github.towerfull.tools.PrototypeFactory;
import io.github.towerfull.enumElements.MonsterType;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a scenario containing multiple waves of monsters.
 * Each wave can be scheduled to spawn at a specific time.
 */
public class Scenario extends Prototype {
    // List of scheduled waves in the scenario.
    private final List<WaveSchedule> schedules = new ArrayList<>();

    // Factory to create monster instances based on their type.
    private final PrototypeFactory<MonsterType, Monster> factory;

    // Current time in the scenario, used to track when waves should spawn.
    private float scenarioTime;

    /**
     * Creates a new Scenario with a specified factory for monster creation.
     * * @param factory Factory to create monsters of different types.
     */
    public Scenario(PrototypeFactory<MonsterType, Monster> factory) {
        this(factory, 0f);
    }

    /**
     * Creates a new Scenario with a specified factory for monster creation and an initial scenario time.
     *
     * @param factory       Factory to create monsters of different types.
     * @param scenarioTime  Initial time in the scenario.
     */
    public Scenario(PrototypeFactory<MonsterType, Monster> factory, float scenarioTime) {
        this.factory = factory;
        this.scenarioTime = scenarioTime;
    }

    /**
     * Adds a wave to the scenario with a specified start time.
     *
     * @param wave       The wave to be added.
     * @param startTime  The time at which the wave should start spawning.
     */
    public void addWave(Wave wave, float startTime) {
        schedules.add(new WaveSchedule(wave, startTime));
    }

    /**
     * Updates the scenario by advancing the scenario time and spawning monsters
     *
     * @param delta Time delta since the last update, used to advance the scenario time.
     * @param gameArea The game area where monsters will be spawned.
     */
    public void update(float delta, GameArea gameArea) {
        float previousTime = scenarioTime;
        scenarioTime += delta;

        for(WaveSchedule schedule : schedules) {
            List<WaveEntry> spawns = schedule.pollEntriesForGlobalTime(previousTime, delta);
            for(WaveEntry entry : spawns) {
                Monster m = factory.create(entry.getType());
                if(m != null) {
                    gameArea.spawnMonster(m);
                    Gdx.app.log("Scenario", "Spawning " + entry.getType() + " at t=" + scenarioTime);
                } else {
                    Gdx.app.error("Scenario", "Failed to create monster: " + entry.getType());
                }
            }
        }
    }

    /**
     * Checks if all scheduled waves in the scenario have finished.
     *
     * @return true if all waves are finished, false otherwise.
     */
    public boolean isFinished() {
        return schedules.stream().allMatch(WaveSchedule::isFinished);
    }

    /**
     * Resets the scenario to its initial state.
     */
    public void reset() {
        scenarioTime = 0f;
        schedules.clear();
    }

    /**
     * Clones the Scenario instance, creating a new instance with the same properties.
     *
     * @return A new Scenario instance that is a clone of this one.
     */
    @Override
    public Scenario clone() {
        Scenario cloned = new Scenario(factory.clone(), scenarioTime);
        for(WaveSchedule schedule : schedules) {
            cloned.schedules.add(schedule.clone());
        }
        return cloned;
    }
}
