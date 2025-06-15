// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: A listener interface for handling tower destruction events in a tower defense game.
// -------------------------------------------------------------------------------------

package io.github.towerfull.listener;

import io.github.towerfull.entities.defenses.BuildSpot;

/**
 * Listener interface for handling tower destruction events.
 * Implement this interface to respond to tower sell and cancel actions.
 */
public interface DestructionListener {
    /**
     * Called when a tower is sold.
     *
     * @param spot The build spot where the tower was located.
     */
    void onSellTower(BuildSpot spot);

    /**
     * Called when the sell action is canceled.
     */
    void onCancel();
}
