// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: A data class to hold level information temporarily,
//      which will be used to create instances of Level later.
// -------------------------------------------------------------------------------------

package io.github.towerfull.tools.data;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import io.github.towerfull.enumElements.BackgroundId;
import io.github.towerfull.enumElements.ScenarioId;

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

    /**
     * Represents a node in the path graph, as a temporary data structure.
     * Each node has a position and a list of next nodes it can lead to.
     */
    public static class PathNode {
        public float[] pos;
        public Array<String> next;
    }

}
