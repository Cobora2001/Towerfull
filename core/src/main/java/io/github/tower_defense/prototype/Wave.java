package io.github.tower_defense.prototype;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

public class Wave extends Prototype {
    private final List<WaveEntry> entries;
    private final PrototypeFactory<MonsterType, Monster> factory;
    private final Array<Monster> activeMonsters;

    private Vector2 spawnPoint;
    private float elapsedTime = 0f;
    private int currentIndex = 0;
    private boolean finished = false;
    private boolean started = false;

    public Wave(List<WaveEntry> entries,
                PrototypeFactory<MonsterType, Monster> factory,
                Array<Monster> activeMonsters,
                Vector2 spawnPoint) {
        this.entries = entries;
        this.factory = factory;
        this.activeMonsters = activeMonsters;
        this.spawnPoint = spawnPoint;
    }

    public void start() {
        this.started = true;
        this.elapsedTime = 0f;
        this.currentIndex = 0;
        this.finished = false;
        Gdx.app.log("Wave", "🚀 Vague démarrée avec " + entries.size() + " entrées.");
    }

    public void update(float deltaTime) {
        if (!started || finished) return;

        elapsedTime += deltaTime;

        while (currentIndex < entries.size()
                && entries.get(currentIndex).getSpawnTime() <= elapsedTime) {

            WaveEntry entry = entries.get(currentIndex);
            Monster m = factory.create(entry.getType());

            if (m == null) {
                Gdx.app.error("Wave", "❌ Prototype introuvable pour le type : " + entry.getType());
                currentIndex++;
                continue;
            }

            m.setLogicalPos(spawnPoint.cpy());
            activeMonsters.add(m);

            Gdx.app.log("Wave", "👾 Monstre spawné : " + entry.getType() + " à t=" + elapsedTime);
            ++currentIndex;
        }

        if (currentIndex >= entries.size()) {
            finished = true;
            Gdx.app.log("Wave", "✅ Vague terminée.");
        }
    }

    public boolean isFinished() {
        return finished;
    }

    public void reset() {
        this.started = false;
        this.elapsedTime = 0f;
        this.currentIndex = 0;
        this.finished = false;
    }

    public Array<Monster> getActiveMonsters() {
        return activeMonsters;
    }

    @Override
    public Wave clone() {
        return new Wave(
                new ArrayList<>(entries),
                factory,
                activeMonsters,
                spawnPoint
        );
    }
}
