package io.github.towerfull.gameBoard.level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import io.github.towerfull.entities.ennemies.Scenario;

public class Level {

    private final int cols;
    private final int rows;
    private final PathGraph pathGraph;
    private final Array<Vector2> buildableTiles;
    private final Scenario scenario;
    private final int startingGold;
    private final int startingLife;
    private final Background background;

    public Level(int cols, int rows, PathGraph pathGraph, Scenario scenario, Array<Vector2> buildableTiles,
                 int startingGold, int startingLife, Background background) {
        this.cols = cols;
        this.rows = rows;
        this.pathGraph = pathGraph;
        this.scenario = scenario;
        this.buildableTiles = buildableTiles;
        this.startingGold = startingGold;
        this.startingLife = startingLife;
        this.background = background;
    }

    public Level(int cols, int rows, Array<Vector2> path, Scenario scenario, Array<Vector2> buildableTiles,
                 int startingGold, int startingLife, Background background) {
        this.cols = cols;
        this.rows = rows;

        ObjectMap<String, Node> graphMap = new ObjectMap<>();
        char currentId = 'A';

        Node prevNode = null;
        for (Vector2 pos : path) {
            String id = String.valueOf(currentId++);
            Node currentNode = new Node(id, pos);

            graphMap.put(id, currentNode);

            if (prevNode != null) {
                prevNode.addNextAxis(currentNode);
            }

            prevNode = currentNode;
        }

        this.pathGraph = new PathGraph(graphMap);
        this.scenario = scenario;
        this.buildableTiles = buildableTiles;
        this.startingGold = startingGold;
        this.startingLife = startingLife;
        this.background = background;
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public PathGraph getPathGraph() {
        return pathGraph;
    }

    public Scenario getScenario() { return scenario; }

    public Array<Vector2> getBuildableTiles() {
        return buildableTiles;
    }

    public int getStartingGold() {
        return startingGold;
    }

    public int getStartingLife() {
        return startingLife;
    }

    public Background getBackground() {
        return background;
    }
}
