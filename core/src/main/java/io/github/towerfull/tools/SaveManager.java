package io.github.towerfull.tools;

import io.github.towerfull.gameBoard.GameArea;

import java.text.SimpleDateFormat;
import java.util.*;

public class SaveManager {

    private static SaveManager instance;
    private final Map<String, GameArea> saves = new LinkedHashMap<>(); // Keep insertion order

    private SaveManager() {}

    public static SaveManager getInstance() {
        if (instance == null) {
            instance = new SaveManager();
        }
        return instance;
    }

    // Save using a timestamp as the slot name
    public void saveGameWithTimestamp(GameArea area) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        saves.put(timestamp, area.clone());
    }

    // Save with custom name (still supported)
    public void saveGame(String name, GameArea area) {
        saves.put(name, area.clone());
    }

    public GameArea loadGame(String name) {
        GameArea area = saves.get(name);
        return area != null ? area.clone() : null;
    }

    public boolean hasSave(String name) {
        return saves.containsKey(name);
    }

    public void removeSave(String name) {
        saves.remove(name);
    }

    public void clearSaves() {
        saves.clear();
    }

    public Set<String> getSaveNames() {
        return saves.keySet();
    }

    public List<String> getSortedSaveNamesNewestFirst() {
        List<String> list = new ArrayList<>(saves.keySet());
        list.sort(Collections.reverseOrder()); // Works with timestamp format
        return list;
    }

    public GameArea getGameArea(String name) {
        GameArea area = saves.get(name);
        return area != null ? area.clone() : null;
    }
}
