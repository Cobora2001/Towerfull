// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: An enumeration of all appearance IDs used in the game.
// -------------------------------------------------------------------------------------

package io.github.towerfull.enumElements;

/**
 * An enumeration of all appearance IDs used in the game.
 * These IDs are used to identify different visual elements such as mobs, towers, backgrounds, and special locations.
 * It is generally an union of all the IDs that need an appearance in the game, given that we don't reuse appearances
 * for different element types (e.g., a goblin will never have the same appearance as a tower).
 */
public enum AppearanceId {
    // 🧱 Mobs
    GOBLIN,
    SKELETON,
    ORC,
    RAT,
    GOLEM,
    WOLF,
    SHAMAN,
    WIZARD,
    THIEF,
    DRAGON,
    ZOMBIE,
    ROBOT,
    ELEMENTAL,
    MULE,

    // 🏰 Tours
    CASTLE,
    SNIPER,
    CRYSTAL,
    CATAPULT,
    LONGBOW,
    SLINGSHOT,
    MAGE,
    GOLD_ARROW,

    // Build spots
    BUILD_SPOT,

    // Background
    GRASS,
    COBBLE,
    SAND,
    PATH,
    ICE,
    ICE_PATH,
    DIRT,
    BRICK,

    // Start and End
    PORTAL,
    TEMPLE,
    CAVE,
    DESERT_TEMPLE,
    ICE_SPAWN,
    ICE_CASTLE,
    BROKEN_TOWER,
    SHACK
}

