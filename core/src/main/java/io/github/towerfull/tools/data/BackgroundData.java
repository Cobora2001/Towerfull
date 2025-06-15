// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: A data class to hold background information temporarily,
//      which will be used to create instances of Background later.
// -------------------------------------------------------------------------------------

package io.github.towerfull.tools.data;

import io.github.towerfull.enumElements.AppearanceId;

public class BackgroundData {
    public AppearanceId backgroundAppearance;
    public AppearanceId pathAppearance;
    public AppearanceId pathStartAppearance;
    public AppearanceId pathEndAppearance;
}
