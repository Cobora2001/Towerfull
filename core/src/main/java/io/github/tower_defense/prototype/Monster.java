package io.github.tower_defense.prototype;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Monster extends Killable {
    private int pv;
    private int maxPv;
    private float speed; // cells per second
    private int damage;
    private int reward;
    private int pathIndex = 0;
    private boolean hasReachedEnd = false;

    public Monster(int pv, int maxPv, Vector2 logicalPos,
                   float speed, int damage, int reward,
                   KillableAppearance appearance) {
        super(logicalPos, appearance);
        this.speed = speed;
        this.damage = damage;
        this.reward = reward;
        this.pv = pv;
        this.maxPv = maxPv;
    }

    public Monster(Monster m) {
        super(m); // Clone logic + appearance
        this.speed = m.speed;
        this.damage = m.damage;
        this.reward = m.reward;
        this.pathIndex = m.pathIndex;
        this.hasReachedEnd = m.hasReachedEnd;
        this.pv = m.pv;
        this.maxPv = m.maxPv;
    }

    @Override
    public Monster clone() {
        return new Monster(this);
    }

    public void update(float delta, Array<Vector2> path) {
        if (hasReachedEnd || pathIndex >= path.size) return;

        Vector2 target = path.get(pathIndex);
        Vector2 direction = target.cpy().sub(logicalPos);
        float distance = direction.len();

        if (distance < 0.01f) {
            pathIndex++;
            if (pathIndex >= path.size) {
                hasReachedEnd = true;
            }
        } else {
            float maxDistance = speed * delta; // cells per second × seconds
            if (distance <= maxDistance) {
                logicalPos.set(target);
                pathIndex++;
                if (pathIndex >= path.size) {
                    hasReachedEnd = true;
                }
            } else {
                direction.nor().scl(maxDistance);
                logicalPos.add(direction);
            }
        }
    }

    public boolean hasReachedEnd() {
        return hasReachedEnd;
    }

    public float getSpeed() {
        return speed;
    }

    public int getDamage() {
        return damage;
    }

    public int getReward() {
        return reward;
    }

    public int getPathIndex() {
        return pathIndex;
    }

    public int getPv() {
        return pv;
    }

    public void setPv(int pv) {
        this.pv = pv;
    }

    public int getMaxPv() {
        return maxPv;
    }

    public void setMaxPv(int maxPv) {
        this.maxPv = maxPv;
    }

    public void takeDamage(int amount) {
        this.pv -= amount;
        if (this.pv < 0) this.pv = 0;
    }

    public boolean isDead() {
        return pv <= 0;
    }
}
