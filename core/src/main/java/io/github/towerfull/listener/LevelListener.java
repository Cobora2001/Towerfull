// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: A listener interface for handling end-of-level events in a tower defense game.
// -------------------------------------------------------------------------------------

package io.github.towerfull.listener;

/**
 * Listener interface for handling end-of-level events.
 * Implement this interface to respond to game over and level completion events.
 */
public interface LevelListener {
    /**
     * Called when the game is over.
     * This method is triggered when the player loses the game.
     */
    void onGameOver();

    /**
     * Called when the level is completed successfully.
     * This method is triggered when the player successfully completes the level.
     */
    void onLevelComplete();
}
