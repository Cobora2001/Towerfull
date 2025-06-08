package io.github.tower_defense.Prototype;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

public class Scenario extends Prototype {
    private final Array<Monster> activeMonsters;
    private final List<Wave> waves = new ArrayList<>();
    private Vector2 startPosition;

    private Wave currentWave;
    private int currentWaveIndex = 0;

    public Scenario(Array<Monster> activeMonsters, Vector2 startPosition) {
        this.activeMonsters = activeMonsters;
    }

    public Scenario(Scenario other) {
        this.activeMonsters = other.activeMonsters;
        this.waves.addAll(other.waves);
        this.currentWave = other.currentWave != null ? other.currentWave.clone() : null;
        this.currentWaveIndex = other.currentWaveIndex;
        this.startPosition = other.startPosition;
    }

    @Override
    public Scenario clone() {
        return new Scenario(this);
    }

    public void addWave(Wave wave) {
        if (wave == null) {
            throw new IllegalArgumentException("Wave cannot be null");
        }

        Wave clonedWave = wave.clone();
        waves.add(clonedWave);
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

    public boolean hasMoreWave(){
        return currentWaveIndex < waves.size();
    }

    public boolean allWaveFinished(){
        return (currentWave == null || currentWave.isFinished() && currentWaveIndex >= waves.size());
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
