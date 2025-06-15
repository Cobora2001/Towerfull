// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: Class to load scenario prototypes from data files
// -------------------------------------------------------------------------------------

package io.github.towerfull.tools.loader;

import io.github.towerfull.entities.ennemies.Monster;
import io.github.towerfull.entities.ennemies.Scenario;
import io.github.towerfull.entities.ennemies.WaveSchedule;
import io.github.towerfull.enumElements.MonsterType;
import io.github.towerfull.enumElements.ScenarioId;
import io.github.towerfull.enumElements.WaveId;
import io.github.towerfull.tools.PrototypeFactory;
import io.github.towerfull.tools.data.ScenarioData;
import io.github.towerfull.tools.data.WaveScheduleData;

public class ScenarioPrototypeLoader extends AbstractPrototypeLoader<Scenario, ScenarioData, ScenarioId> {
    // The factories to create wave and monster prototypes
    private final PrototypeFactory<WaveId, WaveSchedule> waveFactory;
    private final PrototypeFactory<MonsterType, Monster> monsterFactory;

    /**
     * Constructor for ScenarioPrototypeLoader.
     * @param waveFactory the factory to create wave prototypes
     * @param monsterFactory the factory to create monster prototypes
     */
    public ScenarioPrototypeLoader(
        PrototypeFactory<WaveId, WaveSchedule> waveFactory,
        PrototypeFactory<MonsterType, Monster> monsterFactory
    ) {
        this.waveFactory = waveFactory;
        this.monsterFactory = monsterFactory;
    }

    /**
     * Creates a new instance of Scenario based on the provided ScenarioData.
     * @param id the identifier for the scenario
     * @param data the data containing wave schedules and other scenario information
     * @return a new Scenario instance populated with the provided data
     */
    @Override
    protected Scenario createInstance(ScenarioId id, ScenarioData data) {
        Scenario scenario = new Scenario(monsterFactory);

        for(WaveScheduleData waveScheduleData : data.waves) {
            WaveSchedule original = waveFactory.create(waveScheduleData.wave);
            if(original != null) {
                WaveSchedule clone = new WaveSchedule(original.getWave().clone(), waveScheduleData.startTime);
                scenario.addWave(clone.getWave(), clone.getScenarioStartTime());
            }
        }

        return scenario;
    }
}
