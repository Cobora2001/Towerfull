package io.github.tower_defense.prototype;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.tower_defense.loader.JsonLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Scenario extends Prototype {
    private final Array<Monster> activeMonsters;
    private final List<Wave> waves = new ArrayList<>();
    private Vector2 startPosition;

    private Wave currentWave;
    private int currentWaveIndex = 0;

    private final float waveDelay = 3f;
    private float waveCooldown = 0f;



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

    public void loadWavesFromDirectory(String folderName, PrototypeFactory<MonsterType, Monster> factory, Vector2 spawn) {
        this.startPosition = spawn;

        FileHandle dir = Gdx.files.internal(folderName);
        if (!dir.exists() || !dir.isDirectory()) {
            Gdx.app.error("Scenario", "❌ Dossier de vagues introuvable : " + folderName);
            return;
        }

        FileHandle[] allFiles = dir.list();

        List<FileHandle> jsonFiles = new ArrayList<>();
        for (FileHandle file : allFiles) {
            if (!file.isDirectory() && file.name().toLowerCase().endsWith(".json")) {
                jsonFiles.add(file);
            }
        }

        jsonFiles.sort(Comparator.comparing(FileHandle::name));

        for (FileHandle file : jsonFiles) {
            String waveId = file.nameWithoutExtension();
            List<WaveEntry> entries = JsonLoader.get().getWaveEntries(waveId);
            Wave wave = new Wave(entries, factory, activeMonsters, spawn);
            addWave(wave);
        }
    }




    public void update(float deltaTime) {
        if (currentWave != null) {
            currentWave.update(deltaTime);
            if (currentWave.isFinished() && activeMonsters.size == 0) {
                currentWave = null;
                waveCooldown = waveDelay;
            }
        } else if (currentWaveIndex < waves.size()) {
            waveCooldown -= deltaTime;
            if (waveCooldown <= 0f) {
                startNextWave();
            }
        }
    }

    public Wave getCurrentWave() {
        return currentWave;
    }

    public boolean hasNextWave() {
        return currentWaveIndex < waves.size() || currentWave != null;
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
