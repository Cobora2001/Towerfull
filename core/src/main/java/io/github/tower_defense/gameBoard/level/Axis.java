package io.github.tower_defense.gameBoard.level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Axis {
    private final String id;
    private final Vector2 position;
    private final Array<Axis> nextAxes = new Array<>();

    public Axis(String id, Vector2 position) {
        this.id = id;
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Array<Axis> getNextAxes() {
        return nextAxes;
    }

    public void addNextAxis(Axis axis) {
        nextAxes.add(axis);
    }
}
