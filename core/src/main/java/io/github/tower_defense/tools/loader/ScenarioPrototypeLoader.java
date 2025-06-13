package io.github.tower_defense.tools.loader;

import io.github.tower_defense.entities.ennemies.Monster;
import io.github.tower_defense.entities.ennemies.Scenario;
import io.github.tower_defense.entities.ennemies.WaveSchedule;
import io.github.tower_defense.enumElements.MonsterType;
import io.github.tower_defense.enumElements.ScenarioId;
import io.github.tower_defense.enumElements.WaveId;
import io.github.tower_defense.tools.PrototypeFactory;
import io.github.tower_defense.tools.data.ScenarioData;
import io.github.tower_defense.tools.data.WaveScheduleData;

public class ScenarioPrototypeLoader extends AbstractPrototypeLoader<Scenario, ScenarioData, ScenarioId> {
    private final PrototypeFactory<WaveId, WaveSchedule> waveFactory;
    private final PrototypeFactory<MonsterType, Monster> monsterFactory;

    public ScenarioPrototypeLoader(
        PrototypeFactory<WaveId, WaveSchedule> waveFactory,
        PrototypeFactory<MonsterType, Monster> monsterFactory
    ) {
        this.waveFactory = waveFactory;
        this.monsterFactory = monsterFactory;
    }

    @Override
    protected Scenario createInstance(ScenarioId id, ScenarioData data) {
        Scenario scenario = new Scenario(monsterFactory);

        for (WaveScheduleData waveScheduleData : data.waves) {
            WaveSchedule original = waveFactory.create(waveScheduleData.wave);
            if (original != null) {
                WaveSchedule clone = new WaveSchedule(original.getWave().clone(), waveScheduleData.startTime);
                scenario.addWave(clone.getWave(), clone.getScenarioStartTime());
            }
        }

        return scenario;
    }
}
