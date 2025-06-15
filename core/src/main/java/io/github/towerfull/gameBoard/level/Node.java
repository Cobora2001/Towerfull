// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: Represents a point in the game board level, which can connect to other nodes to form a path.
// -------------------------------------------------------------------------------------

package io.github.towerfull.gameBoard.level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Represents a point in the game board level, which can connect to other nodes to form a path.
 * Each node has an ID, a position, and a list of next axes (nodes it can connect to).
 */
public class Node {
    // The unique identifier for this node
    private final String id;

    // The position of this node in the game board
    private final Vector2 position;

    // The list of nodes that can be reached from this node
    private final Array<Node> nextAxes = new Array<>();

    /**
     * Constructs a Node with the specified ID and position.
     *
     * @param id       The unique identifier for this node.
     * @param position The position of this node in the game board.
     */
    public Node(String id, Vector2 position) {
        this.id = id;
        this.position = position;
    }

    /**
     * Gets the unique identifier of this node.
     * @return The ID of this node.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the position of this node in the game board.
     * @return The position of this node as a Vector2.
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * Gets the list of nodes that can be reached from this node.
     * @return An array of next axes (nodes).
     */
    public Array<Node> getNextAxes() {
        return nextAxes;
    }

    /**
     * Adds a new node to the list of next axes.
     *
     * @param node The node to be added as a next axis.
     */
    public void addNextAxis(Node node) {
        nextAxes.add(node);
    }
}
