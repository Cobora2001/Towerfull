package io.github.tower_defense.prototype;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Tower extends Prototype {
    private int range;
    private int damage;
    private float cooldown;
    private int cost;
    private KillableAppearance appearance;
    private float timeSinceLastShot;

    public Tower(int range, int damage, float cooldown, int cost, KillableAppearance appearance) {
        this.range = range;
        this.damage = damage;
        this.cooldown = cooldown;
        this.timeSinceLastShot = cooldown; // Start with cooldown to allow immediate firing
        this.cost = cost;
        this.appearance = appearance;
    }

    public Tower(Tower t) {
        this.range = t.range;
        this.damage = t.damage;
        this.cooldown = t.cooldown;
        this.cost = t.cost;
        this.appearance = t.appearance;
        this.timeSinceLastShot = t.timeSinceLastShot;
    }


    @Override
    public Tower clone() {
        return new Tower(this);
    }

    public void update(float delta, Array<Monster> monsters, GameArea area, Vector2 logicalPos) {
        timeSinceLastShot += delta;
        if (timeSinceLastShot < cooldown) return;

        for (Monster monster : monsters) {
            Vector2 monsterPosition = monster.getLogicalPos();
            // We use the logical position of the monster to calculate if the tower can hit it
            float distance = monsterPosition.dst(logicalPos);
            if (distance <= range && !monster.isDead()) {
                // Tower can hit the monster
                monster.takeDamage(damage);
                timeSinceLastShot = 0; // Reset cooldown after shooting
                break; // Exit loop after hitting one monster
            }
        }
    }

    public int getRange() {
        return range;
    }

    public int getDamage() {
        return damage;
    }

    public float getCooldown() {
        return cooldown;
    }

    public int getCost() {
        return cost;
    }

    public KillableAppearance getAppearance() {
        return appearance;
    }
}
