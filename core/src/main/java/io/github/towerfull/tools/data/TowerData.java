// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: A data class to hold tower information temporarily,
//      which will be used to create instances of Tower later.
// -------------------------------------------------------------------------------------

package io.github.towerfull.tools.data;

import io.github.towerfull.enumElements.AppearanceId;

public class TowerData {
    public int damage;
    public int range;
    public int cost;
    public float cooldown;
    public AppearanceId appearance;
}
