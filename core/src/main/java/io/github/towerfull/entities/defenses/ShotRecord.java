// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: Represents a shot fired by a defense tower, including its origin, destination,
//      damage, and time since it was fired.
// -------------------------------------------------------------------------------------

package io.github.towerfull.entities.defenses;

import com.badlogic.gdx.math.Vector2;
import io.github.towerfull.tools.Prototype;

/**
 * Represents a shot fired by a defense tower, including its origin, destination,
 * damage, and time since it was fired. They are only temporary records used
 * to visualize the shots on the game board.
 */
public class ShotRecord extends Prototype {
    // The time a shot is alive before it disappears
    private static final float TIME_TO_LIVE = 0.3f;

    // The shot's origin and destination coordinates,
    private final Vector2 from;
    private final Vector2 to;

    // The time since the shot was fired, used to determine if it is still alive
    private float timeSinceShot;

    // The damage dealt by the shot, used for visual effects only
    private final int damage;

    /**
     * Constructs a ShotRecord with specified parameters.
     *
     * @param from          The origin of the shot.
     * @param to            The destination of the shot.
     * @param damage        The damage dealt by the shot.
     * @param timeSinceShot The time since the shot was fired.
     */
    public ShotRecord(Vector2 from, Vector2 to, int damage, float timeSinceShot) {
        this.from = from;
        this.to = to;
        this.timeSinceShot = timeSinceShot;
        this.damage = damage;
    }

    /**
     * Constructs a ShotRecord with specified parameters, initializing timeSinceShot to 0.
     *
     * @param from   The origin of the shot.
     * @param to     The destination of the shot.
     * @param damage The damage dealt by the shot.
     */
    public ShotRecord(Vector2 from, Vector2 to, int damage) {
        this(from, to, damage, 0f);
    }

    /**
     * Checks if the shot is still alive based on the time since it was fired.
     *
     * @return true if the shot is still alive, false otherwise.
     */
    public boolean isAlive() {
        return timeSinceShot < TIME_TO_LIVE;
    }

    /**
     * Gets the percentage of the shot's lifetime that has elapsed.
     *
     * @return A float value between 0 and 1 representing the percentage of the shot's lifetime that has elapsed.
     */
    public float getPercentageAlive() {
        return Math.max(0, (TIME_TO_LIVE - timeSinceShot) / TIME_TO_LIVE);
    }

    /**
     * Gets the origin of the shot.
     *
     * @return The origin of the shot as a Vector2.
     */
    public Vector2 getFrom() {
        return from;
    }

    /**
     * Gets the destination of the shot.
     *
     * @return The destination of the shot as a Vector2.
     */
    public Vector2 getTo() {
        return to;
    }

    /**
     * Gets the time since the shot was fired.
     *
     * @return The time since the shot was fired as a float.
     */
    public float getTimeSinceShot() {
        return timeSinceShot;
    }

    /**
     * Updates the time since the shot was fired.
     *
     * @param deltaTime The time to add to the time since the shot was fired.
     */
    public void updateTime(float deltaTime) {
        this.timeSinceShot += deltaTime;
    }

    /**
     * Gets the damage dealt by the shot.
     *
     * @return The damage dealt by the shot as an int.
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Creates a copy of this ShotRecord.
     *
     * @return A new ShotRecord instance with the same properties as this one.
     */
    @Override
    public ShotRecord clone() {
        return new ShotRecord(from.cpy(), to.cpy(), damage, timeSinceShot);
    }
}
