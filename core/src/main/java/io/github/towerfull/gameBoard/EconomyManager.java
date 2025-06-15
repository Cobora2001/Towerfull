// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: A tool to manage the economy of the game, including gold management.
// -------------------------------------------------------------------------------------

package io.github.towerfull.gameBoard;

import com.badlogic.gdx.utils.Array;
import io.github.towerfull.tools.Prototype;
import io.github.towerfull.listener.GoldListener;

/**
 * EconomyManager is responsible for managing the game's economy, specifically the gold.
 * It allows earning, spending, and checking the amount of gold available.
 * It also notifies listeners when the amount of gold changes.
 */
public class EconomyManager extends Prototype {
    // The amount of gold the player has
    private int gold;

    // Listeners that will be notified when the gold amount changes
    private final Array<GoldListener> listeners = new Array<>();

    /**
     * Constructor to initialize the EconomyManager with a starting amount of gold.
     *
     * @param startingGold The initial amount of gold.
     */
    public EconomyManager(int startingGold) {
        this.gold = startingGold;
        notifyGoldChanged();
    }

    /**
     * Gets the current amount of gold.
     * @return The current amount of gold.
     */
    public int getGold() {
        return gold;
    }

    /**
     * Checks if the player can afford an item or action that costs a certain amount of gold.
     *
     * @param amount The cost in gold.
     * @return true if the player has enough gold, false otherwise.
     */
    public boolean canAfford(int amount) {
        return gold >= amount;
    }

    /**
     * Attempts to spend a certain amount of gold.
     * If the player has enough gold, it deducts the amount and notifies listeners.
     *
     * @param amount The amount of gold to spend.
     * @return true if the spending was successful, false if not enough gold.
     */
    public boolean spendGold(int amount) {
        if(canAfford(amount)) {
            gold -= amount;
            notifyGoldChanged();
            return true;
        }
        return false;
    }

    /**
     * Earns a certain amount of gold.
     * If the amount is positive, it adds to the gold. If negative, it deducts (allowing for penalties).
     * It ensures that gold does not go below zero and notifies listeners of the change.
     *
     * @param amount The amount of gold to earn (can be negative).
     */
    public void earnGold(int amount) {
        if(amount > 0) {
            gold += amount;
            notifyGoldChanged();
        }
        else {
            gold -= amount; // Allow negative earnings, e.g., for penalties
            if(gold < 0) {
                gold = 0; // Ensure gold doesn't go negative
            }
            notifyGoldChanged();
        }
    }

    /**
     * Sets the amount of gold directly.
     * This method allows setting the gold to a specific value, ensuring it does not go below zero.
     *
     * @param amount The new amount of gold.
     */
    public void setGold(int amount) {
        this.gold = Math.max(0, amount);
        notifyGoldChanged();
    }

    /**
     * Adds a listener that will be notified when the gold amount changes.
     *
     * @param listener The listener to add.
     */
    public void addListener(GoldListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener so it will no longer be notified of gold changes.
     *
     * @param listener The listener to remove.
     */
    public void removeListener(GoldListener listener) {
        listeners.removeValue(listener, true);
    }

    /**
     * Notifies all registered listeners that the amount of gold has changed.
     * This method is called whenever the gold amount is modified.
     */
    private void notifyGoldChanged() {
        for(GoldListener l : listeners) {
            l.onGoldChanged(gold);
        }
    }

    /**
     * Creates a clone of the EconomyManager with the current amount of gold.
     * This is useful for saving or resetting the economy state.
     *
     * @return A new EconomyManager instance with the same amount of gold.
     */
    @Override
    public EconomyManager clone() {
        return new EconomyManager(this.gold);
    }
}
