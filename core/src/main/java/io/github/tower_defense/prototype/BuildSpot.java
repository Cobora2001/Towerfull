package io.github.tower_defense.prototype;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import io.github.tower_defense.loader.AppearanceAssets;

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

    public void setUsed(boolean used) {
        if (!used) {
            this.tower = null;
        }
        // si used == true, on ne fait rien ici pour éviter d’écraser une tour non encore assignée
    }

    @Override
    public BuildSpot clone() {
        BuildSpot clone = new BuildSpot(new Vector2(logicalPos));
        if (tower != null) {
            clone.tower = tower.clone();
        }
        return clone;
    }

    public void removeTower() {
        this.tower = null;
    }

    public void setTower(Tower tower) {
        this.tower = tower;
    }

    @Override
    public Appearance getAppearance() {
        Appearance appearance = isUsed() ? tower.getAppearance() : super.getAppearance();
        if (appearance == null) {
            Gdx.app.error("BuildSpot", "❌ Apparence manquante pour " + (isUsed() ? "la tour" : "SPOT") + " au spot " + logicalPos);
        }
        return appearance;
    }
}
