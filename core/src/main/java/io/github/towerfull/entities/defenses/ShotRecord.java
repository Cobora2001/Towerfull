package io.github.towerfull.entities.defenses;

import com.badlogic.gdx.math.Vector2;
import io.github.towerfull.tools.Prototype;

public class ShotRecord extends Prototype {
    private static final float TIME_TO_LIVE = 0.3f;
    private final Vector2 from;
    private final Vector2 to;
    private float timeSinceShot;
    private final int damage;

    public ShotRecord(Vector2 from, Vector2 to, int damage, float timeSinceShot) {
        this.from = from;
        this.to = to;
        this.timeSinceShot = timeSinceShot;
        this.damage = damage;
    }

    public ShotRecord(Vector2 from, Vector2 to, int damage) {
        this(from, to, damage, 0f);
    }

    public boolean isAlive() {
        return timeSinceShot < TIME_TO_LIVE;
    }

    public float getPercentageAlive() {
        return Math.max(0, (TIME_TO_LIVE - timeSinceShot) / TIME_TO_LIVE);
    }

    public Vector2 getFrom() {
        return from;
    }

    public Vector2 getTo() {
        return to;
    }

    public float getTimeSinceShot() {
        return timeSinceShot;
    }

    public void updateTime(float deltaTime) {
        this.timeSinceShot += deltaTime;
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public ShotRecord clone() {
        return new ShotRecord(from.cpy(), to.cpy(), damage, timeSinceShot);
    }
}
