// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: Represents a monster in the game, which can move along a path, take damage, and give rewards when defeated
// -------------------------------------------------------------------------------------

package io.github.towerfull.entities.ennemies;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.towerfull.entities.Appearance;
import io.github.towerfull.entities.Printable;

/**
 * Represents a monster in the game, which can move along a path, take damage, and give rewards when defeated.
 * Monsters have health points (pv), speed, damage, and a reward value.
 * They can follow a path defined by a series of Vector2 positions.
 */
public class Monster extends Printable {
    // Health points (pv) of the monster, which damage can reduce.
    private int pv;

    // Maximum health points (maxPv) of the monster, used to reset or set initial health.
    private int maxPv;

    // Speed of the monster in cells per second, used to determine how fast it moves along the path.
    private final float speed;

    // Damage the monster can inflict on the player.
    private final int damage;

    // Reward given to the player when the monster is defeated (if negative, it becomes a cost).
    private final int reward;

    // Index of the current position in the path that the monster is following.
    private int pathIndex = 0;

    // Flag indicating whether the monster has reached the end of its path.
    private boolean hasReachedEnd = false;

    // The path the monster follows, represented as an array of Vector2 positions.
    private Array<Vector2> path = null;

    /**
     * Constructs a Monster with specified health, speed, damage, reward, and appearance.
     *
     * @param pv         Current health points of the monster.
     * @param maxPv      Maximum health points of the monster.
     * @param logicalPos Logical position of the monster in the game world.
     * @param speed      Speed of the monster in cells per second.
     * @param damage     Damage the monster can inflict on the player.
     * @param reward     Reward given to the player when the monster is defeated.
     * @param appearance Appearance of the monster.
     */
    public Monster(int pv, int maxPv, Vector2 logicalPos,
                   float speed, int damage, int reward,
                   Appearance appearance) {
        super(logicalPos, appearance);
        this.speed = speed;
        this.damage = damage;
        this.reward = reward;
        this.pv = pv;
        this.maxPv = maxPv;
    }

    /**
     * Constructs a Monster with specified health, speed, damage, and reward.
     * The logical position is set to null initially.
     *
     * @param pv      Current health points of the monster.
     * @param speed   Speed of the monster in cells per second.
     * @param damage  Damage the monster can inflict on the player.
     * @param reward  Reward given to the player when the monster is defeated.
     * @param appearance Appearance of the monster.
     */
    public Monster(int pv, float speed, int damage, int reward, Appearance appearance) {
        this(pv, pv, null, speed, damage, reward, appearance);
    }

    /**
     * Copy constructor to create a new Monster instance from an existing one.
     * This clones the monster's properties and path.
     *
     * @param m The Monster instance to clone.
     */
    public Monster(Monster m) {
        super(m);
        this.speed = m.speed;
        this.damage = m.damage;
        this.reward = m.reward;
        this.pathIndex = m.pathIndex;
        this.hasReachedEnd = m.hasReachedEnd;
        this.pv = m.pv;
        this.maxPv = m.maxPv;
        this.path = m.path;
    }

    /**
     * Creates a clone of this Monster instance.
     *
     * @return A new Monster instance with the same properties as this one.
     */
    @Override
    public Monster clone() {
        return new Monster(this);
    }

    /**
     * Updates the monster's position along its path based on the elapsed time (delta).
     * If the monster reaches a target position, it moves to the next position in the path.
     * If it reaches the end of the path, it sets hasReachedEnd to true.
     *
     * @param delta The time elapsed since the last update, used to calculate movement.
     */
    public void update(float delta) {
        if(path == null || path.size == 0) return;
        if(hasReachedEnd || pathIndex >= path.size) return;

        Vector2 target = path.get(pathIndex);
        Vector2 direction = target.cpy().sub(logicalPos);
        float distance = direction.len();

        // If the monster is very close to the target, move to the next path index
        if(distance < 0.01f) {
            ++pathIndex;
            if(pathIndex >= path.size) {
                hasReachedEnd = true;
            }
        } else {
            // Move towards the target position based on speed and delta time
            float maxDistance = speed * delta;
            if(distance <= maxDistance) {
                // We use the part of the delta that is needed to reach the target, and keep the rest
                logicalPos.set(target.cpy());
                ++pathIndex;
                if(pathIndex >= path.size) {
                    hasReachedEnd = true;
                } else {
                    // If we have not reached the end, we continue to the next target
                    this.update(delta - (distance / speed));
                }
            } else {
                direction.nor().scl(maxDistance);
                logicalPos.add(direction);
            }
        }
    }

    /**
     * Sets the path for the monster to follow.
     * Resets the path index and hasReachedEnd flag.
     *
     * @param path The new path as an array of Vector2 positions.
     */
    public void setPath(Array<Vector2> path) {
        this.path = path;
        this.pathIndex = 0;
        this.hasReachedEnd = false;
    }

    /**
     * Checks if the monster has reached the end of its path.
     *
     * @return true if the monster has reached the end, false otherwise.
     */
    public boolean hasReachedEnd() {
        return hasReachedEnd;
    }

    /**
     * Gets the speed of the monster.
     *
     * @return The speed of the monster in cells per second.
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * Gets the damage the monster can inflict on the player.
     *
     * @return The damage value of the monster.
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Gets the reward given to the player when the monster is defeated.
     *
     * @return The reward value of the monster.
     */
    public int getReward() {
        return reward;
    }

    /**
     * Gets the current path index of the monster.
     *
     * @return The index of the current position in the path.
     */
    public int getPathIndex() {
        return pathIndex;
    }

    /**
     * Gets the current health points (pv) of the monster.
     *
     * @return The current health points of the monster.
     */
    public int getPv() {
        return pv;
    }

    /**
     * Sets the current health points (pv) of the monster.
     *
     * @param pv The new health points to set for the monster.
     */
    public void setPv(int pv) {
        this.pv = pv;
    }

    /**
     * Gets the maximum health points (maxPv) of the monster.
     *
     * @return The maximum health points of the monster.
     */
    public int getMaxPv() {
        return maxPv;
    }

    /**
     * Sets the maximum health points (maxPv) of the monster.
     *
     * @param maxPv The new maximum health points to set for the monster.
     */
    public void setMaxPv(int maxPv) {
        this.maxPv = maxPv;
    }

    /**
     * Reduces the monster's health points (pv) by a specified amount.
     * If the health points drop below zero, they are set to zero.
     *
     * @param amount The amount of damage to apply to the monster.
     */
    public void takeDamage(int amount) {
        this.pv -= amount;
        if(this.pv < 0) this.pv = 0;
    }

    /**
     * Checks if the monster is dead (i.e., its health points are less than or equal to zero).
     *
     * @return true if the monster is dead, false otherwise.
     */
    public boolean isDead() {
        return pv <= 0;
    }
}
