// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: Class to load waves prototypes from data files
// -------------------------------------------------------------------------------------

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
    /**
     * Creates an instance of WaveSchedule from the provided WaveData.
     *
     * @param type The identifier for the wave type.
     * @param data The data containing wave entries and their spawn times.
     * @return A new WaveSchedule instance containing the wave entries.
     */
    @Override
    protected WaveSchedule createInstance(WaveId type, WaveData data) {
        List<WaveEntry> entries = new ArrayList<>();
        for(WaveEntryData entry : data.data) {
            entries.add(new WaveEntry(entry.type, entry.spawnTime));
        }

        Wave wave = new Wave(entries);
        return new WaveSchedule(wave, 0f);
    }
}
