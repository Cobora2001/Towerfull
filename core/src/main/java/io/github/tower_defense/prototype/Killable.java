package io.github.tower_defense.prototype;

import com.badlogic.gdx.math.Vector2;

public abstract class Killable extends Prototype {
    protected Vector2 logicalPos;
    protected Appearance appearance;

    public Killable(Vector2 logicalPos, Appearance appearance) {
        this.logicalPos = logicalPos;
        this.appearance = appearance;
    }

    public Killable(Killable other) {
        this.logicalPos = other.logicalPos != null ? new Vector2(other.logicalPos) : null;
        this.appearance = other.appearance != null ? other.appearance : null;
    }

    public Appearance getAppearance() {
        return appearance;
    }

    public void setAppearance(Appearance appearance) {
        this.appearance = appearance;
    }

    public Vector2 getLogicalPos() {
        return logicalPos;
    }

    public void setLogicalPos(Vector2 logicalPos) {
        this.logicalPos = logicalPos;
    }
}

