package io.github.tower_defense.Prototype;

import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

public class Scenario extends Prototype {
    private final Array<Monster> activeMonsters;
    private final List<Wave> waves = new ArrayList<>();

    private Wave currentWave;
    private int currentWaveIndex = 0;

    public Scenario(Array<Monster> activeMonsters) {
        this.activeMonsters = activeMonsters;
    }

    public Scenario(Scenario other) {
        this.activeMonsters = other.activeMonsters;
        this.waves.addAll(other.waves);
        this.currentWave = other.currentWave != null ? other.currentWave.clone() : null;
        this.currentWaveIndex = other.currentWaveIndex;
    }

    @Override
    public Scenario clone() {
        return new Scenario(this);
    }

    public void addWave(Wave wave) {
        waves.add(wave);
    }

    public void startNextWave() {
        if (currentWaveIndex < waves.size()) {
            currentWave = waves.get(currentWaveIndex);
            currentWaveIndex++;
        } else {
            currentWave = null;
        }
    }

    public void update(float deltaTime) {
        if (currentWave != null) {
            currentWave.update(deltaTime);
        }
    }

    public Wave getCurrentWave() {
        return currentWave;
    }

    public boolean hasNextWave() {
        return !waves.isEmpty();
    }

    public int getTotalWaves() {
        return waves.size() + (currentWave != null && !currentWave.isFinished() ? 1 : 0);
    }

    public List<Wave> getWaves() {
        return waves;
    }

    public Wave getWave(int index) {
        return waves.get(index);
    }
}
