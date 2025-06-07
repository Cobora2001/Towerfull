package io.github.tower_defense.Prototype;

public class EconomyManager {

    private int gold;

    public EconomyManager(int startingGold) {
        this.gold = startingGold;
    }

    public int getGold() {
        return gold;
    }

    public boolean canAfford(int amount) {
        return gold >= amount;
    }

    public boolean spendGold(int amount) {
        if (canAfford(amount)) {
            gold -= amount;
            return true;
        }
        return false;
    }

    public void earnGold(int amount) {
        gold += amount;
    }

    public void setGold(int amount) {
        gold = amount;
    }
}
