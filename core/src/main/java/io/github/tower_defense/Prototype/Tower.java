package io.github.tower_defense.Prototype;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;

public class Tower extends Killable {
    private int range;
    private int damage;
    private int attackSpeed;
    private int cost;
    private float timeSinceLastShot = 0;

    public Tower(int pv, int maxPv, Vector2 logicalPos, int range, int damage, int attackSpeed, int cost) {
        super(pv, maxPv, logicalPos, null);
        this.range = range;
        this.damage = damage;
        this.attackSpeed = attackSpeed;
        this.cost = cost;
    }

    public Tower(Tower t) {
        super(t);
        this.range = t.range;
        this.damage = t.damage;
        this.attackSpeed = t.attackSpeed;
        this.cost = t.cost;
    }

    @Override
    public Tower clone() {
        return new Tower(this);
    }

    public void update(float delta, ArrayList<Monster> monsters, GameArea area) {
        timeSinceLastShot += delta;
        if (timeSinceLastShot < 1f / attackSpeed) return;

        Vector2 myPixel = getPixelPos(area);

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

    public int getAttackSpeed() {
        return attackSpeed;
    }

    public int getCost() {
        return cost;
    }
}
