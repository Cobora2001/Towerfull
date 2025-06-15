package io.github.towerfull.tools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import io.github.towerfull.gameBoard.level.Axis;
import io.github.towerfull.tools.data.LevelData;
import com.badlogic.gdx.Gdx;

import java.util.List;

public final class GraphUtilities {

    private GraphUtilities() {
        // Prevent instantiation
    }

    public static ObjectMap<String, Axis> buildPathGraph(ObjectMap<String, LevelData.PathNode> pathNodes) {
        ObjectMap<String, Axis> graph = new ObjectMap<>();

        // First pass: create Axis nodes without connections
        for (ObjectMap.Entry<String, LevelData.PathNode> entry : pathNodes.entries()) {
            String nodeId = entry.key;
            float x = entry.value.pos[0];
            float y = entry.value.pos[1];
            graph.put(nodeId, new Axis(nodeId, new Vector2(x, y)));
        }

        // Second pass: add connections
        for (ObjectMap.Entry<String, LevelData.PathNode> entry : pathNodes.entries()) {
            Axis axis = graph.get(entry.key);
            for (String nextId : entry.value.next) {
                Axis nextAxis = graph.get(nextId);
                if (nextAxis != null) {
                    axis.addNextAxis(nextAxis);
                } else {
                    Gdx.app.error("GraphUtilities", "Path node '" + nextId + "' not found for connection from '" + entry.key + "'");
                }
            }
        }

        return graph;
    }

    public static ObjectMap<String, Axis> buildLinearPathGraph(List<float[]> path) {
        ObjectMap<String, Axis> graph = new ObjectMap<>();
        Axis prevAxis = null;
        int idx = 0;

        for (float[] point : path) {
            String id = "P" + idx++;
            Axis axis = new Axis(id, new Vector2(point[0], point[1]));
            graph.put(id, axis);
            if (prevAxis != null) {
                prevAxis.addNextAxis(axis);
            }
            prevAxis = axis;
        }

        return graph;
    }

    public static ObjectMap<String, Axis> generateDefaultPathGraph(int cols, int rows) {
        ObjectMap<String, Axis> graph = new ObjectMap<>();
        Axis start = new Axis("Start", new Vector2(0, rows / 2));
        Axis end = new Axis("End", new Vector2(cols - 1, rows / 2));
        start.addNextAxis(end);
        graph.put("Start", start);
        graph.put("End", end);
        return graph;
    }

    public static Array<Vector2> flattenPathGraph(ObjectMap<String, Axis> graph) {
        Array<Vector2> pathPoints = new Array<>();
        Array<String> visited = new Array<>();
        Array<Axis> queue = new Array<>();

        Axis start = graph.get("A");
        if (start == null && graph.size > 0) {
            start = graph.values().iterator().next();
        }
        if (start == null) return pathPoints;

        queue.add(start);
        while (queue.size > 0) {
            Axis current = queue.removeIndex(0);
            if (visited.contains(current.getId(), false)) continue;

            visited.add(current.getId());
            pathPoints.add(current.getPosition());

            for (Axis next : current.getNextAxes()) {
                if (!visited.contains(next.getId(), false)) {
                    queue.add(next);
                }
            }
        }

        return pathPoints;
    }
}
