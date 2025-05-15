package io.github.tower_defense.Prototype;

import io.github.tower_defense.Level.Level;

import java.util.ArrayList;
import java.util.List;

public class Scenario extends Prototype {

    private final List<Wave> waves;
    private Wave currentWave;
    private boolean finished = false;

    public Scenario() {
        this.waves = new ArrayList<>();
    }

    public Scenario(Scenario other) {
        this.waves = new ArrayList<>();
        for (Wave w : other.waves) {
            this.waves.add(w.clone());
        }
        this.finished = other.finished;
    }

    @Override
    public Scenario clone() {
        return new Scenario(this);
    }

    public void addWave(Wave wave) {
        waves.add(wave);
    }

    public void update(float delta, GameArea area, Level level) {
        if (finished) return;

        if (currentWave == null || currentWave.isFinished()) {
            if (!waves.isEmpty()) {
                currentWave = waves.remove(0);
            } else {
                finished = true;
                return;
            }
        }

        currentWave.update(delta, area, level);
    }

    public Wave getCurrentWave() {
        return currentWave;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean hasNextWave() {
        return !waves.isEmpty();
    }

    public void startNextWave() {
        if (!waves.isEmpty()) {
            currentWave = waves.remove(0);
        }
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
