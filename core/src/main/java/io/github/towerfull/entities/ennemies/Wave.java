// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: Represents a wave of enemies to be spawned in a game.
// -------------------------------------------------------------------------------------

package io.github.towerfull.entities.ennemies;

import io.github.towerfull.tools.Prototype;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a wave of enemies to be spawned in a game.
 * Contains a list of WaveEntry objects that define the type and spawn time of each enemy.
 */
public class Wave extends Prototype {
    // List of WaveEntry objects, will be sorted by spawn time
    private final List<WaveEntry> entries = new ArrayList<>();

    /**
     * Constructs a Wave with the given list of WaveEntry objects.
     * The entries are sorted by their relative spawn time.
     *
     * @param entries List of WaveEntry objects to initialize the wave with.
     */
    public Wave(List<WaveEntry> entries) {
        this.entries.addAll(entries);
        this.entries.sort(Comparator.comparing(WaveEntry::getRelativeSpawnTime));
    }

    /**
     * Polls entries that are scheduled to spawn within a specified time range.
     *
     * @param fromTime The start of the time range (relative to the wave start).
     * @param delta The duration of the time range.
     * @return A list of WaveEntry objects that are scheduled to spawn within the specified time range.
     */
    public List<WaveEntry> pollEntriesInRange(float fromTime, float delta) {
        float toTime = fromTime + delta;
        List<WaveEntry> toSpawn = new ArrayList<>();

        Iterator<WaveEntry> iter = entries.iterator();
        while(iter.hasNext()) {
            WaveEntry entry = iter.next();
            float t = entry.getRelativeSpawnTime();
            if(t >= fromTime && t < toTime) {
                toSpawn.add(entry);
                iter.remove(); // ensure it's only returned once
            } else if(t >= toTime) {
                break; // list is sorted; safe to exit early
            }
        }
        return toSpawn;
    }

    /**
     * Creates a deep copy of this Wave instance.
     *
     * @return A new Wave instance with cloned entries.
     */
    @Override
    public Wave clone() {
        List<WaveEntry> clonedEntries = new ArrayList<>();
        for(WaveEntry e : entries) {
            clonedEntries.add(new WaveEntry(e.getType(), e.getRelativeSpawnTime()));
        }
        return new Wave(clonedEntries);
    }

    /**
     * Checks if the wave has no entries left.
     *
     * @return true if there are no entries, false otherwise.
     */
    public boolean isEmpty() {
        return entries.isEmpty();
    }
}
