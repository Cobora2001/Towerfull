package io.github.tower_defense.tools.loader;

import io.github.tower_defense.entities.ennemies.Wave;
import io.github.tower_defense.entities.ennemies.WaveEntry;
import io.github.tower_defense.entities.ennemies.WaveSchedule;
import io.github.tower_defense.enumElements.WaveId;
import io.github.tower_defense.tools.data.WaveData;
import io.github.tower_defense.tools.data.WaveEntryData;

import java.util.ArrayList;
import java.util.List;

public class WavePrototypeLoader extends AbstractPrototypeLoader<WaveSchedule, WaveData, WaveId> {
    @Override
    protected WaveSchedule createInstance(WaveId type, WaveData data) {
        List<WaveEntry> entries = new ArrayList<>();
        for (WaveEntryData entry : data.data) {
            entries.add(new WaveEntry(entry.type, entry.spawnTime));
        }

        Wave wave = new Wave(entries);
        return new WaveSchedule(wave, 0f);
    }
}
