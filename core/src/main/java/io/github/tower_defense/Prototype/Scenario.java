package io.github.tower_defense.Prototype;

import java.util.ArrayList;
import java.util.List;

public class Scenario extends Prototype {
    private final List<Wave> waves;

    public Scenario() {
        this.waves = new ArrayList<>();
    }

    public Scenario(Scenario other) {
        this.waves = new ArrayList<>();
        for (Wave w : other.waves) {
            this.waves.add(w.clone());
        }
    }

    public void addWave(Wave wave) {
        waves.add(wave);
    }

    public List<Wave> getWaves() {
        return waves;
    }

    public Wave getWave(int index) {
        return waves.get(index);
    }

    public int getTotalWaves() {
        return waves.size();
    }

    @Override
    public Scenario clone() {
        return new Scenario(this);
    }
}
