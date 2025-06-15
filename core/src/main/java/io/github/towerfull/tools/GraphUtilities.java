// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: It's a utility class for building and manipulating path graphs in a game board.
// -------------------------------------------------------------------------------------

package io.github.towerfull.tools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import io.github.towerfull.gameBoard.level.Node;
import io.github.towerfull.tools.data.LevelData;
import com.badlogic.gdx.Gdx;

import java.util.List;

/**
 * GraphUtilities provides utility methods for building and manipulating path graphs.
 * It includes methods to create graphs from path nodes, linear paths, and default paths,
 * as well as methods to flatten a graph into a list of points.
 */
public final class GraphUtilities {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private GraphUtilities() {
        // Prevent instantiation
    }

    /**
     * Builds a path graph from the provided path nodes.
     *
     * @param pathNodes The path nodes to build the graph from.
     * @return An ObjectMap representing the path graph.
     */
    public static ObjectMap<String, Node> buildPathGraph(ObjectMap<String, LevelData.PathNode> pathNodes) {
        ObjectMap<String, Node> graph = new ObjectMap<>();

        // First pass: create Axis nodes without connections
        for(ObjectMap.Entry<String, LevelData.PathNode> entry : pathNodes.entries()) {
            String nodeId = entry.key;
            float x = entry.value.pos[0];
            float y = entry.value.pos[1];
            graph.put(nodeId, new Node(nodeId, new Vector2(x, y)));
        }

        // Second pass: add connections
        for(ObjectMap.Entry<String, LevelData.PathNode> entry : pathNodes.entries()) {
            Node node = graph.get(entry.key);
            for(String nextId : entry.value.next) {
                Node nextNode = graph.get(nextId);
                if(nextNode != null) {
                    node.addNextAxis(nextNode);
                } else {
                    Gdx.app.error("GraphUtilities", "Path node '" + nextId + "' not found for connection from '" + entry.key + "'");
                }
            }
        }

        return graph;
    }

    /**
     * Builds a linear path graph from a list of points.
     *
     * @param path The list of points to build the graph from.
     * @return An ObjectMap representing the linear path graph.
     */
    public static ObjectMap<String, Node> buildLinearPathGraph(List<float[]> path) {
        ObjectMap<String, Node> graph = new ObjectMap<>();
        Node prevNode = null;
        int idx = 0;

        for(float[] point : path) {
            String id = "P" + idx++;
            Node node = new Node(id, new Vector2(point[0], point[1]));
            graph.put(id, node);
            if(prevNode != null) {
                prevNode.addNextAxis(node);
            }
            prevNode = node;
        }

        return graph;
    }

    /**
     * Generates a default path graph with a start and end node.
     *
     * @param cols The number of columns in the grid.
     * @param rows The number of rows in the grid.
     * @return An ObjectMap representing the default path graph.
     */
    public static ObjectMap<String, Node> generateDefaultPathGraph(int cols, int rows) {
        ObjectMap<String, Node> graph = new ObjectMap<>();
        Node start = new Node("Start", new Vector2(0, rows / 2));
        Node end = new Node("End", new Vector2(cols - 1, rows / 2));
        start.addNextAxis(end);
        graph.put("Start", start);
        graph.put("End", end);
        return graph;
    }

    /**
     * Flattens a path graph into a list of points.
     *
     * @param graph The path graph to flatten.
     * @return An Array of Vector2 representing the flattened path points.
     */
    public static Array<Vector2> flattenPathGraph(ObjectMap<String, Node> graph) {
        Array<Vector2> pathPoints = new Array<>();
        Array<String> visited = new Array<>();
        Array<Node> queue = new Array<>();

        Node start = graph.get("A");
        if(start == null && graph.size > 0) {
            start = graph.values().iterator().next();
        }
        if(start == null) return pathPoints;

        queue.add(start);
        while(queue.size > 0) {
            Node current = queue.removeIndex(0);
            if(visited.contains(current.getId(), false)) continue;

            visited.add(current.getId());
            pathPoints.add(current.getPosition());

            for(Node next : current.getNextAxes()) {
                if(!visited.contains(next.getId(), false)) {
                    queue.add(next);
                }
            }
        }

        return pathPoints;
    }
}
