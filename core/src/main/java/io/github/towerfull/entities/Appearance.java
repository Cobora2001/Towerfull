// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: Serve as a container for the appearance of an entity, including its texture and dimensions.
// -------------------------------------------------------------------------------------

package io.github.towerfull.entities;

import com.badlogic.gdx.graphics.Texture;

/**
 * Represents the appearance of an entity in the game, encapsulating its texture and dimensions.
 * This class is used to define how entities like monsters, towers, or other game objects look.
 * The appearance is shared across different instances of the same type of entity
 */
public class Appearance {
    // The texture representing the appearance of the entity
    private final Texture texture;

    // The width and height of the entity's appearance in logical units
    private final float width, height;

    /**
     * Constructs an Appearance with the specified texture and dimensions.
     *
     * @param texture The texture to be used for the appearance.
     * @param width   The width of the appearance in logical units.
     * @param height  The height of the appearance in logical units.
     */
    public Appearance(Texture texture, float width, float height) {
        this.texture = texture;
        this.width = width;
        this.height = height;
    }

    /**
     * Returns the texture associated with this appearance.
     *
     * @return The texture of the entity's appearance.
     */
    public Texture getTexture() {
        return texture;
    }

    /**
     * Returns the width of the entity's appearance.
     *
     * @return The width in logical units.
     */
    public float getWidth() {
        return width;
    }

    /**
     * Returns the height of the entity's appearance.
     *
     * @return The height in logical units.
     */
    public float getHeight() {
        return height;
    }
}
