package io.github.tower_defense.level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Level {

    private int cols, rows;
    private Array<Vector2> pathPoints;
    private LevelMode mode = LevelMode.CLASSIC;

    public Level(int cols, int rows, Array<Vector2> pathPoints) {
        this.cols = cols;
        this.rows = rows;
        this.pathPoints = pathPoints;
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public Array<Vector2> getPathPoints() {
        return pathPoints;
    }

    public LevelMode getMode() { return mode; }

    public void setSurvival() { this.mode = LevelMode.SURVIVAL;}
}
