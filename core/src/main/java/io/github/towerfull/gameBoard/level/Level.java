// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: Represents the game level, including its dimensions, path graph, scenario,
//      buildable tiles, starting resources, and background.
// -------------------------------------------------------------------------------------

package io.github.towerfull.gameBoard.level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import io.github.towerfull.entities.ennemies.Scenario;

/**
 * Represents a game level with its dimensions, path graph, scenario, buildable tiles,
 * starting resources, and background.
 */
public class Level {
    // The number of columns and rows in the level grid
    private final int cols;
    private final int rows;

    // The path graph representing the paths that enemies can take
    private final PathGraph pathGraph;

    // The tiles where players can build defenses
    private final Array<Vector2> buildableTiles;

    // The scenario for the level, which may include specific enemy types and behaviors
    private final Scenario scenario;

    // The starting resources for the player
    private final int startingGold;

    // The starting life points for the player
    private final int startingLife;

    // The background of the level, which can be used for visual representation
    private final Background background;

    /**
     * Constructs a Level with specified parameters.
     *
     * @param cols The number of columns in the level grid.
     * @param rows The number of rows in the level grid.
     * @param pathGraph The path graph representing the paths that enemies can take.
     * @param scenario The scenario for the level, which may include specific enemy types and behaviors.
     * @param buildableTiles The tiles where players can build defenses.
     * @param startingGold The starting amount of gold for the player.
     * @param startingLife The starting life points for the player.
     * @param background The background of the level.
     */
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

    /**
     * Constructs a Level with specified parameters, generating the path graph from a list of positions.
     *
     * @param cols The number of columns in the level grid.
     * @param rows The number of rows in the level grid.
     * @param path The list of positions representing the path for enemies.
     * @param scenario The scenario for the level, which may include specific enemy types and behaviors.
     * @param buildableTiles The tiles where players can build defenses.
     * @param startingGold The starting amount of gold for the player.
     * @param startingLife The starting life points for the player.
     * @param background The background of the level.
     */
    public Level(int cols, int rows, Array<Vector2> path, Scenario scenario, Array<Vector2> buildableTiles,
                 int startingGold, int startingLife, Background background) {
        this.cols = cols;
        this.rows = rows;

        ObjectMap<String, Node> graphMap = new ObjectMap<>();
        char currentId = 'A';

        Node prevNode = null;
        for(Vector2 pos : path) {
            String id = String.valueOf(currentId++);
            Node currentNode = new Node(id, pos);

            graphMap.put(id, currentNode);

            if(prevNode != null) {
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

    /**
     * Gets the number of columns in the level grid.
     *
     * @return The number of columns.
     */
    public int getCols() {
        return cols;
    }

    /**
     * Gets the number of rows in the level grid.
     *
     * @return The number of rows.
     */
    public int getRows() {
        return rows;
    }

    /**
     * Gets the path graph representing the paths that enemies can take.
     *
     * @return The path graph.
     */
    public PathGraph getPathGraph() {
        return pathGraph;
    }

    /**
     * Gets the scenario for the level, which may include specific enemy types and behaviors.
     *
     * @return The scenario.
     */
    public Scenario getScenario() { return scenario; }

    /**
     * Gets the tiles where players can build defenses.
     *
     * @return The array of buildable tiles.
     */
    public Array<Vector2> getBuildableTiles() {
        return buildableTiles;
    }

    /**
     * Gets the starting amount of gold for the player.
     *
     * @return The starting gold.
     */
    public int getStartingGold() {
        return startingGold;
    }

    /**
     * Gets the starting life points for the player.
     *
     * @return The starting life points.
     */
    public int getStartingLife() {
        return startingLife;
    }

    /**
     * Gets the background of the level.
     *
     * @return The background.
     */
    public Background getBackground() {
        return background;
    }
}
