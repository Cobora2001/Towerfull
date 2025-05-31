package io.github.tower_defense;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.tower_defense.Prototype.KillableAppearance;

public class AssetRenderer {
    private SpriteBatch batch;

    public AssetRenderer(SpriteBatch batch) {
        this.batch = batch;
    }

    public void renderKillable(KillableAppearance appearance, Vector2 pixelPos) {
        if (appearance == null) return;
        batch.draw(appearance.getTexture(), pixelPos.x, pixelPos.y, appearance.getWidth(), appearance.getHeight());
    }

}
