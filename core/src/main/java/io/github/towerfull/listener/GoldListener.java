// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: A listener interface for handling changes in gold amount.
// -------------------------------------------------------------------------------------

package io.github.towerfull.listener;

/**
 * GoldListener is an interface that defines a method to be called when the amount of gold changes.
 * Implementing classes should provide the logic for handling the change in gold amount.
 */
public interface GoldListener {
    /**
     * This method is called when the amount of gold changes.
     *
     * @param newAmount The new amount of gold.
     */
    void onGoldChanged(int newAmount);
}
