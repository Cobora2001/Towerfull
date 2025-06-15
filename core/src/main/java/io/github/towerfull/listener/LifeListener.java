// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: A listener interface for handling changes in the player's life in a game.
// -------------------------------------------------------------------------------------

package io.github.towerfull.listener;

/**
 * LifeListener is an interface that defines a method to be called when the player's life changes.
 * Implementing classes should provide the logic for handling life changes.
 */
public interface LifeListener {
    void onLifeChanged(int newLife);
}
