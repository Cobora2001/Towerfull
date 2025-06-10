package io.github.tower_defense;

import com.badlogic.gdx.math.Vector2;

public class ShotRecord {
    private final Vector2 from;
    private final Vector2 to;
    private float timeSinceShot;

    public ShotRecord(Vector2 from, Vector2 to) {
        this.from = from;
        this.to = to;
        this.timeSinceShot = 0;
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
}
