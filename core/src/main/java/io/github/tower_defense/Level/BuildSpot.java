package io.github.tower_defense.Level;

import com.badlogic.gdx.math.Vector2;
import io.github.tower_defense.Prototype.GameArea;
import io.github.tower_defense.Prototype.Tower;

public class BuildSpot {
    private final Vector2 logicalPos;
    private Tower tower; // null if empty

    public BuildSpot(Vector2 logicalPos) {
        this.logicalPos = logicalPos;
        this.tower = null;
    }

    public boolean hasTower() {
        return tower != null;
    }

    public void buildTower(Tower tower) {
        this.tower = tower;
    }

    public Tower getTower() {
        return tower;
    }

    public Vector2 getLogicalPos() {
        return logicalPos;
    }

    public boolean isUsed() {
        return tower != null;
    }

    public Vector2 getPixelPos(GameArea gameArea) {
        return gameArea.logicalToPixel(logicalPos);
    }
}
