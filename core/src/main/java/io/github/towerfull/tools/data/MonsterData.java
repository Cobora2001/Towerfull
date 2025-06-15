// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: A data class to hold monster information temporarily,
//      which will be used to create instances of Monster later.
// -------------------------------------------------------------------------------------

package io.github.towerfull.tools.data;

import io.github.towerfull.enumElements.AppearanceId;

public class MonsterData {
    public float speed;
    public int hp;
    public int damage;
    public int reward;
    public AppearanceId appearance;
}
