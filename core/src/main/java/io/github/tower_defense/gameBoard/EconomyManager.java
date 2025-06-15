package io.github.tower_defense.gameBoard;

import com.badlogic.gdx.utils.Array;
import io.github.tower_defense.tools.Prototype;
import io.github.tower_defense.listener.GoldListener;

public class EconomyManager extends Prototype {
    private int gold;
    private final Array<GoldListener> listeners = new Array<>();

    public EconomyManager(int startingGold) {
        this.gold = startingGold;
        notifyGoldChanged();
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
            notifyGoldChanged();
            return true;
        }
        return false;
    }

    public void earnGold(int amount) {
        if (amount > 0) {
            gold += amount;
            notifyGoldChanged();
        }
        else {
            gold -= amount; // Allow negative earnings, e.g., for penalties
            if (gold < 0) {
                gold = 0; // Ensure gold doesn't go negative
            }
            notifyGoldChanged();
        }
    }

    public void setGold(int amount) {
        this.gold = Math.max(0, amount);
        notifyGoldChanged();
    }

    public void addListener(GoldListener listener) {
        listeners.add(listener);
    }

    public void removeListener(GoldListener listener) {
        listeners.removeValue(listener, true);
    }

    private void notifyGoldChanged() {
        for (GoldListener l : listeners) {
            l.onGoldChanged(gold);
        }
    }

    @Override
    public EconomyManager clone() {
        return new EconomyManager(this.gold);
    }
}
