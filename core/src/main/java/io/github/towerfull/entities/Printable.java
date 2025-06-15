// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: Represents elements that can be printed on the screen, such as entities or items.
// -------------------------------------------------------------------------------------

package io.github.towerfull.entities;

import com.badlogic.gdx.math.Vector2;
import io.github.towerfull.tools.Prototype;

/**
 * Represents an entity that can be printed on the screen.
 * This class serves as a base for all entities that have a visual representation
 * and a logical position in the game world.
 * Towers aren't as they don't have a logical position in the game world,
 * but rather a place in a spot.
 */
public abstract class Printable extends Prototype {
    // The logical position of the entity in the game world.
    protected Vector2 logicalPos;

    // The appearance of the entity, which defines how it looks visually.
    protected Appearance appearance;

    /**
     * Constructs a Printable entity with a specified logical position and appearance.
     *
     * @param logicalPos The logical position of the entity in the game world.
     * @param appearance The appearance of the entity.
     */
    public Printable(Vector2 logicalPos, Appearance appearance) {
        this.logicalPos = logicalPos;
        this.appearance = appearance;
    }

    /**
     * Copy constructor to create a new Printable entity from an existing one.
     *
     * @param other The Printable entity to copy.
     */
    public Printable(Printable other) {
        this.logicalPos = other.logicalPos != null ? new Vector2(other.logicalPos) : null;
        this.appearance = other.appearance;
    }

    /**
     * Getter for the appearance of the entity.
     * @return The appearance of the entity.
     */
    public Appearance getAppearance() {
        return appearance;
    }

    /**
     * Setter for the appearance of the entity.
     * @param appearance The new appearance to set for the entity.
     */
    public void setAppearance(Appearance appearance) {
        this.appearance = appearance;
    }

    /**
     * Getter for the logical position of the entity.
     * @return The logical position of the entity in the game world.
     */
    public Vector2 getLogicalPos() {
        return logicalPos;
    }

    /**
     * Setter for the logical position of the entity.
     * @param logicalPos The new logical position to set for the entity.
     */
    public void setLogicalPos(Vector2 logicalPos) {
        this.logicalPos = logicalPos;
    }
}

