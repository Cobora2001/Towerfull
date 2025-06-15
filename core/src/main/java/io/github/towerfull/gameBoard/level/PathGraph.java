package io.github.towerfull.gameBoard.level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.Random;

public class PathGraph {

    private final ObjectMap<String, Node> nodes;

    public PathGraph(ObjectMap<String, Node> nodes) {
        this.nodes = nodes;
    }

    public Node get(String id) {
        return nodes.get(id);
    }

    public ObjectMap<String, Node> getNodes() {
        return nodes;
    }

    public Array<Node> getSpawns() {
        Array<Node> spawns = new Array<>();
        ObjectMap<String, Boolean> hasPrev = new ObjectMap<>();

        // Initialize all nodes as having no previous axis
        for (String key : nodes.keys()) {
            hasPrev.put(key, false);
        }

        // Mark nodes that are targets of a connection (i.e., have predecessors)
        for (Node node : nodes.values()) {
            for (Node next : node.getNextAxes()) {
                hasPrev.put(next.getId(), true);
            }
        }

        // Any axis not marked as having a predecessor is a spawn point
        for (Node node : nodes.values()) {
            if (!hasPrev.get(node.getId(), false)) {
                spawns.add(node);
            }
        }

        return spawns;
    }

    public Array<Node> getEnds() {
        Array<Node> ends = new Array<>();
        for (Node node : nodes.values()) {
            if (node.getNextAxes().isEmpty()) {
                ends.add(node);
            }
        }
        return ends;
    }

    public Array<Vector2> getPathPointsStartingFrom(Node start) {
        Array<Vector2> pathPoints = new Array<>();
        Node current = start;
        Random random = new Random();

        while (current != null) {
            pathPoints.add(current.getPosition());
            if (current.getNextAxes().isEmpty()) {
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
