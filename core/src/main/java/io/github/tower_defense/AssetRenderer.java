package io.github.tower_defense;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.tower_defense.prototype.Appearance;

public class AssetRenderer {
    private final SpriteBatch batch;
    private final float cellWidth, cellHeight;

    public AssetRenderer(SpriteBatch batch, float cellWidth, float cellHeight) {
        this.batch = batch;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
    }

    public void renderAppearance(Appearance appearance, Vector2 pixelCenterPos) {
        if (appearance == null) return;

        // Convert appearance width/height from logical units to pixels
        float width = appearance.getWidth() * cellWidth;
        float height = appearance.getHeight() * cellHeight;

        // Center the image
        float drawX = pixelCenterPos.x - width / 2f;
        float drawY = pixelCenterPos.y - height / 2f;

        batch.draw(appearance.getTexture(), drawX, drawY, width, height);
    }
}
