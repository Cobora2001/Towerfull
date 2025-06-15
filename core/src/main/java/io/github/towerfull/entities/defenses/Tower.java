package io.github.towerfull.entities.defenses;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.towerfull.entities.Appearance;
import io.github.towerfull.gameBoard.GameArea;
import io.github.towerfull.tools.Prototype;
import io.github.towerfull.entities.ennemies.Monster;

public class Tower extends Prototype {
    private final int range;
    private final int damage;
    private final float cooldown;
    private final int cost;
    private final Appearance appearance;
    private float timeSinceLastShot;

    public Tower(int range, int damage, float cooldown, int cost, Appearance appearance) {
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

    public Appearance getAppearance() {
        return appearance;
    }
}
