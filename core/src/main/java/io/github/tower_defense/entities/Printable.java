package io.github.tower_defense.entities;

import com.badlogic.gdx.math.Vector2;
import io.github.tower_defense.tools.Prototype;

public abstract class Printable extends Prototype {
    protected Vector2 logicalPos;
    protected Appearance appearance;

    public Printable(Vector2 logicalPos, Appearance appearance) {
        this.logicalPos = logicalPos;
        this.appearance = appearance;
    }

    public Printable(Printable other) {
        this.logicalPos = other.logicalPos != null ? new Vector2(other.logicalPos) : null;
        this.appearance = other.appearance;
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

