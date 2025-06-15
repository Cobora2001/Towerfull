// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: Represents a buildable spot for towers in the game.
// -------------------------------------------------------------------------------------

package io.github.towerfull.entities.defenses;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import io.github.towerfull.entities.Appearance;
import io.github.towerfull.entities.Printable;
import io.github.towerfull.enumElements.AppearanceId;
import io.github.towerfull.tools.GameAssets;

/**
 * Represents a buildable spot for towers in the game.
 * Each BuildSpot can either be empty or occupied by a Tower.
 * It provides methods to check if it has a tower, build a tower,
 * and manage the tower's state.
 */
public class BuildSpot extends Printable {
    // The logical position of the build spot in the game grid.
    private final Vector2 logicalPos;

    // The tower currently built on this spot, or null if no tower is present.
    private Tower tower;

    /**
     * Constructs a BuildSpot at the specified logical position.
     * The appearance is set to the default build spot appearance.
     * Note that getAppearance() will return the tower's appearance if a tower is built on this spot.
     *
     * @param logicalPos The logical position of the build spot in the game grid.
     */
    public BuildSpot(Vector2 logicalPos) {
        super(logicalPos, GameAssets.get().appearances.get(AppearanceId.BUILD_SPOT));
        this.logicalPos = logicalPos;
        this.tower = null;
    }

    /**
     * Builds a tower on this build spot.
     * If a tower is already present, it will be replaced.
     *
     * @param tower The tower to build on this spot.
     */
    public void buildTower(Tower tower) {
        this.tower = tower;
    }

    /**
     * Gets the tower built on this spot.
     *
     * @return The tower if present, null otherwise.
     */
    public Tower getTower() {
        return tower;
    }

    /**
     * Gets the logical position of this build spot.
     *
     * @return The logical position as a Vector2.
     */
    public Vector2 getLogicalPos() {
        return logicalPos;
    }

    /**
     * Checks if this build spot is currently occupied by a tower.
     *
     * @return true if a tower is built on this spot, false otherwise.
     */
    public boolean isUsed() {
        return tower != null;
    }

    /**
     * Sets the usage state of this build spot.
     * If used is false, the tower is removed.
     * If used is true, the current tower remains unchanged.
     *
     * @param used true to mark the spot as used (keeping the tower), false to mark it as unused (removing the tower).
     */
    public void setUsed(boolean used) {
        if(!used) {
            removeTower();
        }
        // If used is true, we do nothing as the tower remains unchanged.
    }

    /**
     * Clones this BuildSpot. Clones the tower if it exists.
     *
     * @return A new BuildSpot instance with the same logical position and a tower clone.
     */
    @Override
    public BuildSpot clone() {
        BuildSpot clone = new BuildSpot(new Vector2(logicalPos));
        if(tower != null) {
            clone.tower = tower.clone();
        }
        return clone;
    }

    /**
     * Removes the tower from this build spot.
     * After calling this method, isUsed() will return false.
     */
    public void removeTower() {
        this.tower = null;
    }

    /**
     * Sets the tower for this build spot.
     * This method allows replacing the current tower with a new one.
     *
     * @param tower The new tower to set on this build spot.
     */
    public void setTower(Tower tower) {
        this.tower = tower;
    }

    /**
     * Returns the appearance of this build spot.
     * If a tower is built on this spot, it returns the tower's appearance.
     * Otherwise, it returns the default build spot appearance.
     *
     * @return The appearance of the build spot or the tower if built.
     */
    @Override
    public Appearance getAppearance() {
        Appearance appearance = isUsed() ? tower.getAppearance() : super.getAppearance();
        if(appearance == null) {
            Gdx.app.error("BuildSpot", "❌ Apparence manquante pour " + (isUsed() ? "la tour" : "SPOT") + " au spot " + logicalPos);
        }
        return appearance;
    }
}
