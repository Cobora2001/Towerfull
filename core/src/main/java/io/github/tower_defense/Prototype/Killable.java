package io.github.tower_defense.Prototype;

import com.badlogic.gdx.math.Vector2;

public abstract class Killable extends Prototype {
    protected Vector2 logicalPos;
    protected KillableAppearance appearance;

    public Killable(Vector2 logicalPos, KillableAppearance appearance) {
        this.logicalPos = logicalPos;
        this.appearance = appearance;
    }

    public Killable(Killable other) {
        this.logicalPos = other.logicalPos != null ? new Vector2(other.logicalPos) : null;
        this.appearance = other.appearance != null ? other.appearance : null;
    }

    public KillableAppearance getAppearance() {
        return appearance;
    }

    public void setAppearance(KillableAppearance appearance) {
        this.appearance = appearance;
    }

    public Vector2 getLogicalPos() {
        return logicalPos;
    }

    public void setLogicalPos(Vector2 logicalPos) {
        this.logicalPos = logicalPos;
    }

    public Vector2 getPixelPos(GameArea area) {
        return area.logicalToPixel(logicalPos);
    }
}

