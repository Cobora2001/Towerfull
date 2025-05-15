package io.github.tower_defense.Prototype;

import com.badlogic.gdx.math.Vector2;

public abstract class Killable {
    protected int pv;
    protected int maxPv;
    protected Vector2 logicalPos;
    protected Object sprite; // Peut être null ou remplacé plus tard

    public Killable(int pv, int maxPv, Vector2 logicalPos, Object sprite) {
        this.pv = pv;
        this.maxPv = maxPv;
        this.logicalPos = logicalPos;
        this.sprite = sprite;
    }

    public Killable(Killable other) {
        this.pv = other.pv;
        this.maxPv = other.maxPv;
        this.logicalPos = new Vector2(other.logicalPos);
        this.sprite = other.sprite;
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

