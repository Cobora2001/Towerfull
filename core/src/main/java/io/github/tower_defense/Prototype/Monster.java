package io.github.tower_defense.Prototype;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;


public class Monster extends Killable {
    private int speed;
    private int damage;
    private int reward;
    private int pathIndex = 0;
    private float speedMultiplier = 60f;
    private boolean hasReachedEnd = false;


    public Monster(int pv, int maxPv, Vector2 logicalPos, int speed, int damage, int reward) {
        super(pv, maxPv, logicalPos, null);
        this.speed = speed;
        this.damage = damage;
        this.reward = reward;
    }

    public Monster(Monster m) {
        super(m); // Clone logique + sprite
        this.speed = m.speed;
        this.damage = m.damage;
        this.reward = m.reward;
        this.pathIndex = m.pathIndex;
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
            direction.nor().scl(speed * delta * speedMultiplier);
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
}
