package io.github.towerfull.gameBoard.level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.Random;

public class PathGraph {

    private final ObjectMap<String, Axis> nodes;

    public PathGraph(ObjectMap<String, Axis> nodes) {
        this.nodes = nodes;
    }

    public Axis get(String id) {
        return nodes.get(id);
    }

    public ObjectMap<String, Axis> getNodes() {
        return nodes;
    }

    public Array<Axis> getSpawns() {
        Array<Axis> spawns = new Array<>();
        ObjectMap<String, Boolean> hasPrev = new ObjectMap<>();

        // Initialize all nodes as having no previous axis
        for (String key : nodes.keys()) {
            hasPrev.put(key, false);
        }

        // Mark nodes that are targets of a connection (i.e., have predecessors)
        for (Axis axis : nodes.values()) {
            for (Axis next : axis.getNextAxes()) {
                hasPrev.put(next.getId(), true);
            }
        }

        // Any axis not marked as having a predecessor is a spawn point
        for (Axis axis : nodes.values()) {
            if (!hasPrev.get(axis.getId(), false)) {
                spawns.add(axis);
            }
        }

        return spawns;
    }

    public Array<Axis> getEnds() {
        Array<Axis> ends = new Array<>();
        for (Axis axis : nodes.values()) {
            if (axis.getNextAxes().isEmpty()) {
                ends.add(axis);
            }
        }
        return ends;
    }

    public Array<Vector2> getPathPointsStartingFrom(Axis start) {
        Array<Vector2> pathPoints = new Array<>();
        Axis current = start;
        Random random = new Random();

        while (current != null) {
            pathPoints.add(current.getPosition());
            if (current.getNextAxes().isEmpty()) {
                break; // End of path
            }
            // Choose a random next axis instead of always the first
            Array<Axis> nextAxes = current.getNextAxes();
            int randomIndex = random.nextInt(nextAxes.size);
            current = nextAxes.get(randomIndex);
        }

        return pathPoints;
    }
}
