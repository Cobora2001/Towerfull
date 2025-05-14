package io.github.tower_defense.Prototype;

import com.badlogic.gdx.graphics.Texture;

public abstract class Killable extends Prototype {
    private int pv;
    private final int maxPv;
    private int x, y;

    private Texture texture;

    public Killable(int pv, int maxPv, int x, int y, Texture texture) {
        this.pv = pv;
        this.maxPv = maxPv;
        this.x = x;
        this.y = y;
        this.texture = texture;
    }

    public Killable(Killable m) {
        this(m.getPv(), m.getMaxPv(), m.getX(), m.getY(), m.getTexture());
    }

    public abstract Killable clone();

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    protected int getPv() {
        return pv;
    }

    protected int getMaxPv() {
        return maxPv;
    }

    protected void setPv(int pv) {
        this.pv = pv;
    }

    @Override
    public String toString() {
        return "[" + getPv() + "/" + getMaxPv() + "] PV";
    }
}
