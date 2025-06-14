package io.github.tower_defense.tools.data;

import com.badlogic.gdx.utils.Array;
import io.github.tower_defense.enumElements.BackgroundId;
import io.github.tower_defense.enumElements.ScenarioId;

import java.util.List;

public class LevelData {
    public int cols;
    public int rows;
    public List<float[]> path; // Each element is a [x, y] array
    public ScenarioId scenario;
    public Array<float[]> buildableTiles;
    public int startingGold;
    public int startingLife;
    public BackgroundId background;
}
