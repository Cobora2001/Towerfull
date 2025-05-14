package io.github.tower_defense;

import io.github.tower_defense.Prototype.GameArea;

import java.util.Stack;

public class SaveManager {
    private final Stack<GameArea> saves;

    public SaveManager() {
        saves = new Stack<>();
    }

    public void saveGame(GameArea g) {
        saves.add(g.clone());
    }

    public GameArea loadLastSave() {
        return saves.pop();
    }
}
