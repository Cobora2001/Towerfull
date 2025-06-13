package io.github.tower_defense.tools.data;

import io.github.tower_defense.enumElements.ScenarioId;

import java.util.List;

public class LevelData {
    public int cols;
    public int rows;
    public List<float[]> path; // Each element is a [x, y] array
    public ScenarioId scenario; // optional
    public boolean survival = false; // optional
}
