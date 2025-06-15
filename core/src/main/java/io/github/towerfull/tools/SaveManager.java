// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: A class aimed at managing game saves in a TowerFull game.
// -------------------------------------------------------------------------------------

package io.github.towerfull.tools;

import io.github.towerfull.gameBoard.GameArea;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Singleton class to manage game saves.
 */
public class SaveManager {
    // Singleton instance
    private static SaveManager instance;

    // Map to hold game saves, using a String as the key (name or timestamp) and GameArea as the value.
    private final Map<String, GameArea> saves = new LinkedHashMap<>();

    /**
     * Private constructor to prevent instantiation.
     * Use getInstance() to access the singleton instance.
     */
    private SaveManager() {}

    /**
     * Get the singleton instance of SaveManager.
     * @return the SaveManager instance
     */
    public static SaveManager getInstance() {
        if(instance == null) {
            instance = new SaveManager();
        }
        return instance;
    }

    /**
     * Save the current game area with a timestamp.
     * The timestamp is formatted as "yyyy-MM-dd_HH-mm-ss".
     * This allows for easy sorting and identification of saves.
     *
     * @param area the GameArea to save
     */
    public void saveGameWithTimestamp(GameArea area) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        saves.put(timestamp, area.clone());
    }

    /**
     * Save the current game area with a custom name.
     * If a save with the same name already exists, it will be overwritten.
     *
     * @param name the name for the save
     * @param area the GameArea to save
     */
    public void saveGame(String name, GameArea area) {
        saves.put(name, area.clone());
    }

    /**
     * Load a game area by its name.
     * If the save does not exist, it returns null.
     *
     * @param name the name of the save to load
     * @return the GameArea if found, otherwise null
     */
    public GameArea loadGame(String name) {
        GameArea area = saves.get(name);
        return area != null ? area.clone() : null;
    }

    /**
     * Check if a save with the given name exists.
     *
     * @param name the name of the save to check
     * @return true if the save exists, false otherwise
     */
    public boolean hasSave(String name) {
        return saves.containsKey(name);
    }

    /**
     * Remove a save by its name.
     * If the save does not exist, nothing happens.
     *
     * @param name the name of the save to remove
     */
    public void removeSave(String name) {
        saves.remove(name);
    }

    /**
     * Clear all saved games.
     * This will remove all entries from the saves map.
     */
    public void clearSaves() {
        saves.clear();
    }

    /**
     * Get a set of all save names.
     * This can be used to display available saves to the user.
     *
     * @return a Set of save names
     */
    public Set<String> getSaveNames() {
        return saves.keySet();
    }

    /**
     * Get a list of save names sorted by timestamp, newest first.
     * This is useful for displaying saves in a user-friendly manner.
     *
     * @return a List of save names sorted by timestamp
     */
    public List<String> getSortedSaveNamesNewestFirst() {
        List<String> list = new ArrayList<>(saves.keySet());
        list.sort(Collections.reverseOrder()); // Works with timestamp format
        return list;
    }

    /**
     * Get a list of save names sorted by timestamp, oldest first.
     * This is useful for displaying saves in a user-friendly manner.
     *
     * @return a List of save names sorted by timestamp
     */
    public GameArea getGameArea(String name) {
        GameArea area = saves.get(name);
        return area != null ? area.clone() : null;
    }
}
