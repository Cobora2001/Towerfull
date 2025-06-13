package io.github.tower_defense.entities.ennemies;

import io.github.tower_defense.entities.Prototype;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Wave extends Prototype {
    private final List<WaveEntry> entries = new ArrayList<>();

    public Wave(List<WaveEntry> entries) {
        this.entries.addAll(entries);
        this.entries.sort(Comparator.comparing(WaveEntry::getRelativeSpawnTime));
    }

    public List<WaveEntry> pollEntriesInRange(float fromTime, float delta) {
        float toTime = fromTime + delta;
        List<WaveEntry> toSpawn = new ArrayList<>();

        Iterator<WaveEntry> iter = entries.iterator();
        while (iter.hasNext()) {
            WaveEntry entry = iter.next();
            float t = entry.getRelativeSpawnTime();
            if (t >= fromTime && t < toTime) {
                toSpawn.add(entry);
                iter.remove(); // ensure it's only returned once
            } else if (t >= toTime) {
                break; // list is sorted; safe to exit early
            }
        }

        return toSpawn;
    }

    @Override
    public Wave clone() {
        List<WaveEntry> clonedEntries = new ArrayList<>();
        for (WaveEntry e : entries) {
            clonedEntries.add(new WaveEntry(e.getType(), e.getRelativeSpawnTime()));
        }
        return new Wave(clonedEntries);
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }
}
