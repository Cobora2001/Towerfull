// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: A class to store the background and path appearances for a game level.
// -------------------------------------------------------------------------------------

package io.github.towerfull.gameBoard.level;

import io.github.towerfull.entities.Appearance;

/**
 * Represents the background and path appearances for a game level.
 * This class encapsulates the visual elements that define the level's aesthetics.
 */
public class Background {
    // The background appearance of the level.
    private final Appearance backgroundAppearance;

    // The appearance of the path that monsters will follow.
    private final Appearance pathAppearance;

    // The appearance of the start points of the path (where monsters spawn).
    private final Appearance pathStartAppearance;

    // The appearance of the end point of the path (where monsters exit).
    private final Appearance pathEndAppearance;

    /**
     * Constructs a Background instance with specified appearances.
     *
     * @param backgroundAppearance The appearance of the background.
     * @param pathAppearance The appearance of the path.
     * @param pathStartAppearance The appearance of the path start.
     * @param pathEndAppearance The appearance of the path end.
     */
    public Background(Appearance backgroundAppearance, Appearance pathAppearance,
                      Appearance pathStartAppearance, Appearance pathEndAppearance) {
        this.backgroundAppearance = backgroundAppearance;
        this.pathAppearance = pathAppearance;
        this.pathStartAppearance = pathStartAppearance;
        this.pathEndAppearance = pathEndAppearance;
    }

    /**
     * Gets the background appearance of the level.
     * @return The background appearance.
     */
    public Appearance getBackgroundAppearance() {
        return backgroundAppearance;
    }

    /**
     * Gets the appearance of the path that monsters will follow.
     * @return The path appearance.
     */
    public Appearance getPathAppearance() {
        return pathAppearance;
    }

    /**
     * Gets the appearance of the start points of the path (where monsters spawn).
     * @return The path start appearance.
     */
    public Appearance getPathStartAppearance() {
        return pathStartAppearance;
    }

    /**
     * Gets the appearance of the end point of the path (where monsters exit).
     * @return The path end appearance.
     */
    public Appearance getPathEndAppearance() {
        return pathEndAppearance;
    }
}
