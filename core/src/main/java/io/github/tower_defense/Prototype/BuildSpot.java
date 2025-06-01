package io.github.tower_defense.Prototype;

import com.badlogic.gdx.math.Vector2;
import io.github.tower_defense.Loader.AppearanceAssets;

public class BuildSpot extends Killable {
    private final Vector2 logicalPos;
    private Tower tower; // null if empty

    public BuildSpot(Vector2 logicalPos) {
        super(logicalPos, AppearanceAssets.getInstance().getAppearance("SPOT"));
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

    @Override
    public BuildSpot clone() {
        BuildSpot clone = new BuildSpot(new Vector2(logicalPos));
        if (tower != null) {
            clone.tower = tower.clone();
        }
        return clone;
    }

    public void setTower(Tower tower) {
        this.tower = tower;
    }

    @Override
    public KillableAppearance getAppearance() {
        return isUsed() ? tower.getAppearance() : super.getAppearance();
    }
}
