// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: A data class to hold waveEntry information temporarily,
//      which will be used to create instances of WaveEntry later.
// -------------------------------------------------------------------------------------

package io.github.towerfull.tools.data;

import io.github.towerfull.enumElements.MonsterType;

public class WaveEntryData {
    public MonsterType type;
    public float spawnTime;
}
