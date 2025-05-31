package io.github.tower_defense.Prototype;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class Killable extends Prototype {
    protected int pv;
    protected int maxPv;
    protected Vector2 logicalPos;
    protected Actor sprite; // Peut être null ou remplacé plus tard

    private KillableAppearance appearance;

    public Killable(int pv, int maxPv, Vector2 logicalPos, Actor sprite, KillableAppearance appearance) {
        this.pv = pv;
        this.maxPv = maxPv;
        this.logicalPos = logicalPos;
        this.sprite = sprite;
        this.appearance = appearance;
    }

    public Killable(Killable other) {
        this.pv = other.pv;
        this.maxPv = other.maxPv;
        this.logicalPos = new Vector2(other.logicalPos);
        this.sprite = other.sprite;
        this.appearance = other.appearance != null ? other.appearance : null;
    }

    public KillableAppearance getAppearance() {
        return appearance;
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

