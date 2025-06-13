package io.github.tower_defense.entities.ennemies;

import com.badlogic.gdx.Gdx;
import io.github.tower_defense.gameBoard.GameArea;
import io.github.tower_defense.entities.Prototype;
import io.github.tower_defense.tools.PrototypeFactory;
import io.github.tower_defense.enumElements.MonsterType;

import java.util.ArrayList;
import java.util.List;

public class Scenario extends Prototype {
    private final List<WaveSchedule> schedules = new ArrayList<>();
    private final PrototypeFactory<MonsterType, Monster> factory;
    private float scenarioTime;

    public Scenario(PrototypeFactory<MonsterType, Monster> factory) {
        this(factory, 0f);
    }

    public Scenario(PrototypeFactory<MonsterType, Monster> factory, float scenarioTime) {
        this.factory = factory;
        this.scenarioTime = scenarioTime;
    }

    public void addWave(Wave wave, float startTime) {
        schedules.add(new WaveSchedule(wave, startTime));
    }

    public void update(float delta, GameArea gameArea) {
        float previousTime = scenarioTime;
        scenarioTime += delta;

        for (WaveSchedule schedule : schedules) {
            List<WaveEntry> spawns = schedule.pollEntriesForGlobalTime(previousTime, delta);
            for (WaveEntry entry : spawns) {
                Monster m = factory.create(entry.getType());
                if (m != null) {
                    gameArea.spawnMonster(m);
                    Gdx.app.log("Scenario", "👾 Spawning " + entry.getType() + " at t=" + scenarioTime);
                } else {
                    Gdx.app.error("Scenario", "❌ Failed to create monster: " + entry.getType());
                }
            }
        }
    }

    public boolean isFinished() {
        return schedules.stream().allMatch(WaveSchedule::isFinished);
    }

    public void reset() {
        scenarioTime = 0f;
        schedules.clear();
    }

    @Override
    public Scenario clone() {
        Scenario cloned = new Scenario(factory.clone(), scenarioTime);
        for (WaveSchedule schedule : schedules) {
            cloned.schedules.add(schedule.clone());
        }
        return cloned;
    }
}
