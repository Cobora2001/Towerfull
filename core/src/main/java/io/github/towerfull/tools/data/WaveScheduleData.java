// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: A data class to hold waveSchedule information temporarily,
//      which will be used to create instances of WaveSchedule later.
// -------------------------------------------------------------------------------------

package io.github.towerfull.tools.data;

import io.github.towerfull.enumElements.WaveId;

public class WaveScheduleData {
    public WaveId wave;
    public float startTime;
}
