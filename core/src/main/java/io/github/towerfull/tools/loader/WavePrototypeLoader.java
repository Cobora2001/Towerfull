package io.github.towerfull.tools.loader;

import io.github.towerfull.entities.ennemies.Wave;
import io.github.towerfull.entities.ennemies.WaveEntry;
import io.github.towerfull.entities.ennemies.WaveSchedule;
import io.github.towerfull.enumElements.WaveId;
import io.github.towerfull.tools.data.WaveData;
import io.github.towerfull.tools.data.WaveEntryData;

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
