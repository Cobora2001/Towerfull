package io.github.tower_defense.gameBoard.level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.tower_defense.entities.ennemies.Scenario;

public class Level {

    private final int cols;
    private final int rows;
    private final Array<Vector2> pathPoints;
    private LevelMode mode = LevelMode.CLASSIC;
    private final Scenario scenario;

    public Level(int cols, int rows, Array<Vector2> pathPoints, Scenario scenario) {
        this.cols = cols;
        this.rows = rows;
        this.pathPoints = pathPoints;
        this.scenario = scenario;
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

    public Scenario getScenario() { return scenario; }
}
