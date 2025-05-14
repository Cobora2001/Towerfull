package io.github.tower_defense.Prototype;

public class Monster extends Killable {
    private int speed;
    private int damage;
    private int reward;
    private int pathIndex;

    public Monster(int pv, int maxPv, int x, int y, int speed, int damage, int reward) {
        super(pv, maxPv, x, y, null);
        this.speed = speed;
        this.damage = damage;
        this.reward = reward;
        this.pathIndex = 0;
    }

    public Monster(Monster m) {
        super(m);
        this.speed = m.getSpeed();
        this.damage = m.getDamage();
        this.reward = m.getReward();
        this.pathIndex = m.getPathIndex();
    }

    public Monster clone() {
        return new Monster(this);
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public int getPathIndex() {
        return pathIndex;
    }

    public void setPathIndex(int pathIndex) {
        this.pathIndex = pathIndex;
    }
}
