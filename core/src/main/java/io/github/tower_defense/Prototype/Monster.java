package io.github.tower_defense.Prototype;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;


public class Monster extends Killable {
    private int pv;
    private int maxPv;
    private int speed;
    private int damage;
    private int reward;
    private int pathIndex = 0;
    private float speedMultiplier = 60f;
    private boolean hasReachedEnd = false;


    public Monster(int pv, int maxPv, Vector2 logicalPos,
                   int speed, int damage, int reward,
                   KillableAppearance appearance) {
        super(logicalPos, appearance);
        this.speed = speed;
        this.damage = damage;
        this.reward = reward;
        this.pv = pv;
        this.maxPv = maxPv;
    }

    public Monster(Monster m) {
        super(m); // Clone logique + sprite
        this.speed = m.speed;
        this.damage = m.damage;
        this.reward = m.reward;
        this.pathIndex = m.pathIndex;
        this.speedMultiplier = m.speedMultiplier;
        this.hasReachedEnd = m.hasReachedEnd;
        this.pv = m.pv;
        this.maxPv = m.maxPv;
    }

    @Override
    public Monster clone() {
        return new Monster(this);
    }

    public void update(float delta, Array<Vector2> path, GameArea area) {
        if (hasReachedEnd || pathIndex >= path.size) return;

        Vector2 targetLogical = path.get(pathIndex);
        Vector2 targetPixel = area.logicalToPixel(targetLogical);
        Vector2 currentPixel = area.logicalToPixel(logicalPos);

        Vector2 direction = targetPixel.cpy().sub(currentPixel);
        float distance = direction.len();

        if (distance < 1f) {
            pathIndex++;
            if (pathIndex >= path.size) {
                hasReachedEnd = true;
            }
        } else {
            float pixelsPerSecond = speed * area.getCellWidth();
            direction.nor().scl(pixelsPerSecond * delta);
            Vector2 newPixel = currentPixel.add(direction);
            this.logicalPos = area.pixelToLogical(newPixel);
        }
    }

    public boolean hasReachedEnd() {
        return hasReachedEnd;
    }

    public int getSpeed() {
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
