// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: Represents a wave of enemies to be spawned in a scenario at a specific time.
// -------------------------------------------------------------------------------------

package io.github.towerfull.entities.ennemies;

import io.github.towerfull.tools.Prototype;

import java.util.Collections;
import java.util.List;

/**
 * Represents a schedule of a wave of enemies to be spawned in a scenario at a specific time.
 * It contains the wave and the time at which the wave starts in the scenario.
 */
public class WaveSchedule extends Prototype {
    // The wave of enemies to be spawned.
    private final Wave wave;

    // The time at which the wave starts in the scenario.
    private final float scenarioStartTime;

    /**
     * Constructor for WaveSchedule.
     * @param wave the wave of enemies to be spawned
     * @param scenarioStartTime the time at which the wave starts in the scenario
     */
    public WaveSchedule(Wave wave, float scenarioStartTime) {
        this.wave = wave;
        this.scenarioStartTime = scenarioStartTime;
    }

    /**
     * Polls the entries of the wave that should be spawned in the given time range.
     * @param scenarioTimeBefore the time before the scenario starts
     * @param delta the duration for which to poll entries
     * @return a list of WaveEntry objects that should be spawned in the given time range
     */
    public List<WaveEntry> pollEntriesForGlobalTime(float scenarioTimeBefore, float delta) {
        float relativeStart = scenarioTimeBefore - scenarioStartTime;
        if(relativeStart + delta < 0)
            return Collections.emptyList();

        return wave.pollEntriesInRange(Math.max(relativeStart, 0f), delta);
    }

    /**
     * Checks if the wave schedule is finished, i.e., if there are no more entries to spawn.
     * @return true if the wave is empty, false otherwise
     */
    public boolean isFinished() {
        return wave.isEmpty();
    }

    /**
     * Gets the start time of the wave in the scenario.
     * @return the scenario start time
     */
    public float getScenarioStartTime() {
        return scenarioStartTime;
    }

    /**
     * Gets the wave associated with this schedule.
     * @return the wave of enemies
     */
    public Wave getWave() {
        return wave;
    }

    /**
     * Creates a clone of this WaveSchedule.
     * @return a new WaveSchedule object with the same wave and scenario start time
     */
    @Override
    public WaveSchedule clone() {
        return new WaveSchedule(wave.clone(), scenarioStartTime);
    }
}
