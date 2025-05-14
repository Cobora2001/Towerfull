package io.github.tower_defense.Prototype;

public class Tower extends Killable {
    private int range;
    private int damage;
    private int attackSpeed;
    private int cost;

    public Tower(int pv, int maxPv, int x, int y, int range, int damage, int attackSpeed, int cost) {
        super(pv, maxPv, x, y, null);
        this.range = range;
        this.damage = damage;
        this.attackSpeed = attackSpeed;
        this.cost = cost;
    }

    public Tower(Tower t) {
        super(t);
        this.range = t.getRange();
        this.damage = t.getDamage();
        this.attackSpeed = t.getAttackSpeed();
        this.cost = t.getCost();
    }

    @Override
    public Tower clone() {
        return new Tower(this);
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getAttackSpeed() {
        return attackSpeed;
    }

    public void setAttackSpeed(int attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
