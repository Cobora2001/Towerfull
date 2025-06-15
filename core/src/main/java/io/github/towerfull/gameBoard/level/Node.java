package io.github.towerfull.gameBoard.level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Node {
    private final String id;
    private final Vector2 position;
    private final Array<Node> nextAxes = new Array<>();

    public Node(String id, Vector2 position) {
        this.id = id;
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Array<Node> getNextAxes() {
        return nextAxes;
    }

    public void addNextAxis(Node node) {
        nextAxes.add(node);
    }
}
