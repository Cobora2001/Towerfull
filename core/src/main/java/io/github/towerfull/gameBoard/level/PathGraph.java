// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: The oriented graph representing the paths in the game board.
// -------------------------------------------------------------------------------------

package io.github.towerfull.gameBoard.level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.Random;

/**
 * Represents a directed graph of nodes (PathGraph) where each node can have multiple outgoing connections (next axes).
 * This graph is used to determine the paths that monsters can take through the game board.
 */
public class PathGraph {
    // The nodes in the graph, keyed by their unique IDs.
    private final ObjectMap<String, Node> nodes;

    /**
     * Constructs a PathGraph with the given nodes.
     *
     * @param nodes A map of node IDs to Node objects representing the graph.
     */
    public PathGraph(ObjectMap<String, Node> nodes) {
        this.nodes = nodes;
    }

    /**
     * Gets a node by its ID.
     * @param id The unique identifier of the node to retrieve.
     * @return The Node object associated with the given ID, or null if no such node exists.
     */
    public Node get(String id) {
        return nodes.get(id);
    }

    /**
     * Gets all nodes in the graph.
     * @return An ObjectMap containing all nodes, keyed by their IDs.
     */
    public ObjectMap<String, Node> getNodes() {
        return nodes;
    }

    /**
     * Retrieves all spawn points in the graph.
     * A spawn point is defined as a node that has no predecessors (i.e., no incoming connections).
     *
     * @return An Array of Node objects representing the spawn points.
     */
    public Array<Node> getSpawns() {
        Array<Node> spawns = new Array<>();
        ObjectMap<String, Boolean> hasPrev = new ObjectMap<>();

        // Initialize all nodes as having no previous axis
        for(String key : nodes.keys()) {
            hasPrev.put(key, false);
        }

        // Mark nodes that are targets of a connection (i.e., have predecessors)
        for(Node node : nodes.values()) {
            for(Node next : node.getNextAxes()) {
                hasPrev.put(next.getId(), true);
            }
        }

        // Any axis not marked as having a predecessor is a spawn point
        for(Node node : nodes.values()) {
            if(!hasPrev.get(node.getId(), false)) {
                spawns.add(node);
            }
        }

        return spawns;
    }

    /**
     * Retrieves all end points in the graph.
     * An end point is defined as a node that has no outgoing connections (i.e., no next axes).
     *
     * @return An Array of Node objects representing the end points.
     */
    public Array<Node> getEnds() {
        Array<Node> ends = new Array<>();
        for(Node node : nodes.values()) {
            if(node.getNextAxes().isEmpty()) {
                ends.add(node);
            }
        }
        return ends;
    }

    /**
     * Retrieves a list of path points starting from a given node.
     * The path is determined by following the next axes of each node, choosing randomly among them.
     *
     * @param start The starting node from which to begin the path.
     * @return An Array of Vector2 objects representing the positions of the path points.
     */
    public Array<Vector2> getPathPointsStartingFrom(Node start) {
        Array<Vector2> pathPoints = new Array<>();
        Node current = start;
        Random random = new Random();

        while(current != null) {
            pathPoints.add(current.getPosition());
            if(current.getNextAxes().isEmpty()) {
                break; // End of path
            }
            // Choose a random next axis instead of always the first
            Array<Node> nextAxes = current.getNextAxes();
            int randomIndex = random.nextInt(nextAxes.size);
            current = nextAxes.get(randomIndex);
        }

        return pathPoints;
    }
}
