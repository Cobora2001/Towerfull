package io.github.tower_defense.prototype;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Tower extends Prototype {
    private int range;
    private int damage;
    private float cooldown;
    private int cost;
    private KillableAppearance appearance;
    private float timeSinceLastShot = 0;

    public Tower(int range, int damage, float cooldown, int cost, KillableAppearance appearance) {
        this.range = range;
        this.damage = damage;
        this.cooldown = cooldown;
        this.cost = cost;
        this.appearance = appearance;
    }

    public Tower(Tower t) {
        this.range = t.range;
        this.damage = t.damage;
        this.cooldown = t.cooldown;
        this.cost = t.cost;
        this.appearance = t.appearance;
    }


    @Override
    public Tower clone() {
        return new Tower(this);
    }

    public void update(float delta, Array<Monster> monsters, GameArea area, Vector2 logicalPos) {
        timeSinceLastShot += delta;
        if (timeSinceLastShot < cooldown) return;

        Vector2 myPixel = area.logicalToPixel(logicalPos);

        for (Monster monster : monsters) {
            Vector2 monsterPixel = monster.getPixelPos(area);
            if (myPixel.dst(monsterPixel) <= range) {
                monster.takeDamage(damage);
                timeSinceLastShot = 0;
                break;
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
