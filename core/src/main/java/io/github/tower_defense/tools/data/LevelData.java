package io.github.tower_defense.tools.data;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import io.github.tower_defense.enumElements.BackgroundId;
import io.github.tower_defense.enumElements.ScenarioId;

import java.util.List;

public class LevelData {
    public int cols;
    public int rows;
    public ScenarioId scenario;
    public Array<float[]> buildableTiles;
    public int startingGold;
    public int startingLife;
    public BackgroundId background;
    public ObjectMap<String, PathNode> pathNodes;
    public List<float[]> path;

    public static class PathNode {
        public float[] pos;
        public Array<String> next;
    }

}
