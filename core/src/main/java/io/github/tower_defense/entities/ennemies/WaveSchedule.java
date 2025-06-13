package io.github.tower_defense.entities.ennemies;

import io.github.tower_defense.entities.Prototype;

import java.util.Collections;
import java.util.List;

public class WaveSchedule extends Prototype {
    private final Wave wave;
    private final float scenarioStartTime;

    public WaveSchedule(Wave wave, float scenarioStartTime) {
        this.wave = wave;
        this.scenarioStartTime = scenarioStartTime;
    }

    public List<WaveEntry> pollEntriesForGlobalTime(float scenarioTimeBefore, float delta) {
        float relativeStart = scenarioTimeBefore - scenarioStartTime;
        if (relativeStart + delta < 0)
            return Collections.emptyList();

        return wave.pollEntriesInRange(Math.max(relativeStart, 0f), delta);
    }

    public boolean isFinished() {
        return wave.isEmpty();
    }

    public float getScenarioStartTime() {
        return scenarioStartTime;
    }

    public Wave getWave() {
        return wave;
    }

    @Override
    public WaveSchedule clone() {
        return new WaveSchedule(wave.clone(), scenarioStartTime);
    }
}
