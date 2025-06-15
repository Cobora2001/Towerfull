// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: Represents a tower in the game, that can attack monsters within a certain range.
// -------------------------------------------------------------------------------------

package io.github.towerfull.entities.defenses;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.towerfull.entities.Appearance;
import io.github.towerfull.gameBoard.GameArea;
import io.github.towerfull.tools.Prototype;
import io.github.towerfull.entities.ennemies.Monster;

/**
 * Represents a tower in the game, that can attack monsters within a certain range.
 * Towers have properties such as range, damage, cooldown, cost, and appearance.
 * They can shoot at monsters when they are within range and update their state based on time.
 */
public class Tower extends Prototype {
    // Range of the tower in cells
    private final int range;

    // Damage dealt by the tower to monsters
    private final int damage;

    // Cooldown time between shots in seconds
    private final float cooldown;

    // Cost of the tower in game currency
    private final int cost;

    // Appearance of the tower, used for rendering
    private final Appearance appearance;

    // Time since the last shot was fired
    private float timeSinceLastShot;

    /**
     * Constructs a Tower with specified properties.
     *
     * @param range      The range of the tower in cells.
     * @param damage     The damage dealt by the tower to monsters.
     * @param cooldown   The cooldown time between shots in seconds.
     * @param cost       The cost of the tower in game currency.
     * @param appearance The appearance of the tower for rendering.
     */
    public Tower(int range, int damage, float cooldown, int cost, Appearance appearance) {
        this.range = range;
        this.damage = damage;
        this.cooldown = cooldown;
        this.timeSinceLastShot = cooldown; // Start with cooldown to allow immediate firing
        this.cost = cost;
        this.appearance = appearance;
    }

    /**
     * Copy constructor to create a new Tower instance from an existing one.
     *
     * @param t The Tower instance to copy.
     */
    public Tower(Tower t) {
        this.range = t.range;
        this.damage = t.damage;
        this.cooldown = t.cooldown;
        this.cost = t.cost;
        this.appearance = t.appearance;
        this.timeSinceLastShot = t.timeSinceLastShot;
    }

    /**
     * Creates a clone of this Tower instance.
     *
     * @return A new Tower instance with the same properties as this one.
     */
    @Override
    public Tower clone() {
        return new Tower(this);
    }

    /**
     * Updates the tower's state, checking if it can shoot at any monsters within range.
     *
     * @param delta   The time since the last update in seconds.
     * @param monsters The list of monsters currently in the game area.
     * @param area    The game area where the tower is located, used for recording shots.
     * @param logicalPos The logical position of the tower in the game grid.
     */
    public void update(float delta, Array<Monster> monsters, GameArea area, Vector2 logicalPos) {
        timeSinceLastShot += delta;
        if(timeSinceLastShot < cooldown) return;

        for(Monster monster : monsters) {
            Vector2 monsterPosition = monster.getLogicalPos();
            // We use the logical position of the monster to calculate if the tower can hit it
            float distance = monsterPosition.dst(logicalPos);
            // The "strategy" here is that it shoots at the first monster that's alive and within range
            if(distance <= range && !monster.isDead()) {
                monster.takeDamage(damage);
                timeSinceLastShot = 0;

                // Record the shot
                Vector2 towerCenter = logicalPos.cpy().add(0.5f, 0.5f);
                Vector2 monsterCenter = monsterPosition.cpy().add(0.5f, 0.5f);
                area.addShot(towerCenter, monsterCenter, damage);

                break;
            }
        }
    }

    /**
     * Getters for the range
     * @return The range of the tower in cells.
     */
    public int getRange() {
        return range;
    }

    /**
     * Getters for the damage
     * @return The damage dealt by the tower to monsters.
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Getters for the cooldown
     * @return The cooldown time between shots in seconds.
     */
    public float getCooldown() {
        return cooldown;
    }

    /**
     * Getters for the cost
     * @return The cost of the tower in game currency.
     */
    public int getCost() {
        return cost;
    }

    /**
     * Getters for the appearance
     * @return The appearance of the tower, used for rendering.
     */
    public Appearance getAppearance() {
        return appearance;
    }
}
