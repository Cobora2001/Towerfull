// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: Is the base class for all prototypes, which allows for cloning and creating instances of prototypes.
// -------------------------------------------------------------------------------------

package io.github.towerfull.tools;

/**
 * The Prototype class serves as a base class for all prototypes in the Tower Defense game.
 * It provides a method to clone instances of subclasses, allowing for the creation of new objects
 * based on existing ones.
 */
public abstract class Prototype {
    /**
     * Creates a clone of the current prototype instance.
     * @return a new instance of the prototype, which is a copy of the current instance.
     */
    public abstract Prototype clone();
}
