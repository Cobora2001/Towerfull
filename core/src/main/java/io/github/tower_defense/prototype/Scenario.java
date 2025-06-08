package io.github.tower_defense.prototype;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.tower_defense.loader.JsonLoader;

import java.util.*;

public class Scenario extends Prototype {

    private final Array<Monster> activeMonsters;
    private final List<Wave> waves = new ArrayList<>();
    private Vector2 startPosition;

    private Wave currentWave;
    private int currentWaveIndex = 0;

    private final float waveDelay = 3f;
    private float waveCooldown = 0f;

    // === Constructeurs ===
    public Scenario(Array<Monster> activeMonsters, Vector2 spawnPoint) {
        this.activeMonsters = activeMonsters;
        this.startPosition = spawnPoint;
    }

    public Scenario(Scenario other) {
        this.activeMonsters = other.activeMonsters;
        this.startPosition = other.startPosition.cpy();
        for (Wave w : other.waves) this.waves.add(w.clone());
        this.currentWave = other.currentWave != null ? other.currentWave.clone() : null;
        this.currentWaveIndex = other.currentWaveIndex;
        this.waveCooldown = other.waveCooldown;
    }

    @Override
    public Scenario clone() {
        return new Scenario(this);
    }

    // === Chargement de vagues ===
    public void loadWavesFromIndex(String indexPath, PrototypeFactory<MonsterType, Monster> factory, Vector2 spawn) {
        this.startPosition = spawn;

        List<String> wavePaths = JsonLoader.get().loadJsonList(indexPath, String.class);
        if (wavePaths == null || wavePaths.isEmpty()) {
            Gdx.app.error("Scenario", "❌ Aucune vague trouvée dans " + indexPath);
            return;
        }

        for (String wavePath : wavePaths) {
            List<WaveEntry> entries = JsonLoader.get().getWaveEntries(wavePath);
            if (entries == null || entries.isEmpty()) {
                Gdx.app.error("Scenario", "⚠️ Vague ignorée (vide ou invalide) : " + wavePath);
                continue;
            }

            Wave wave = new Wave(entries, factory, activeMonsters, spawn);
            waves.add(wave);

            Gdx.app.log("Scenario", "✅ Vague chargée : " + wavePath + " (" + entries.size() + " monstres)");
        }

        Gdx.app.log("Scenario", "📦 Nombre total de vagues : " + waves.size());
    }




    // === Contrôle du scénario ===
    public void update(float deltaTime) {
        if (currentWave != null) {
            currentWave.update(deltaTime);

            if (currentWave.isFinished() && activeMonsters.size == 0) {
                currentWave = null;
                waveCooldown = waveDelay;
                Gdx.app.log("Scenario", "⏳ Attente avant prochaine vague...");
            }

        } else if (currentWaveIndex < waves.size()) {
            waveCooldown -= deltaTime;

            if (waveCooldown <= 0f) {
                startNextWave();
            }
        }
    }

    private void startNextWave() {
        if (currentWaveIndex >= waves.size()) {
            Gdx.app.log("Scenario", "🏁 Toutes les vagues ont été lancées.");
            return;
        }

        currentWave = waves.get(currentWaveIndex++);
        currentWave.start();

        Gdx.app.log("Scenario", "🚀 Lancement de la vague #" + currentWaveIndex);
    }

    // === Accès / infos ===
    public boolean isFinished() {
        return currentWave == null && currentWaveIndex >= waves.size() && activeMonsters.isEmpty();
    }

    public boolean hasNextWave() {
        return currentWave != null || currentWaveIndex < waves.size();
    }

    public int getTotalWaves() {
        return waves.size();
    }

    public Wave getCurrentWave() {
        return currentWave;
    }

    public List<Wave> getWaves() {
        return Collections.unmodifiableList(waves);
    }

    public Wave getWave(int index) {
        if (index < 0 || index >= waves.size()) throw new IndexOutOfBoundsException();
        return waves.get(index);
    }

    public boolean allWavesFinished() {
        return (currentWave == null || currentWave.isFinished()) && currentWaveIndex >= waves.size();
    }

}
