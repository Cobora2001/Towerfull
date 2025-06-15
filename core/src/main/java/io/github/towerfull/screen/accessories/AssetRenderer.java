// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: A tool we use to render assets in the game, such as towers and other entities.
// -------------------------------------------------------------------------------------

package io.github.towerfull.screen.accessories;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.towerfull.entities.Appearance;

/**
 * A tool we use to render assets in the game, such as towers and other entities.
 * This class handles the rendering of appearances at specified pixel positions.
 */
public class AssetRenderer {
    /**
     * The SpriteBatch used for rendering.
     * It is assumed that the batch is already set up with the correct projection matrix.
     */
    private final SpriteBatch batch;

    /**
     * The width and height of a cell in pixels.
     * This is used to convert logical units to pixel coordinates.
     */
    private final float cellWidth, cellHeight;

    /**
     * Constructs an AssetRenderer with the specified SpriteBatch and cell dimensions.
     *
     * @param batch The SpriteBatch used for rendering.
     * @param cellWidth The width of a cell in pixels.
     * @param cellHeight The height of a cell in pixels.
     */
    public AssetRenderer(SpriteBatch batch, float cellWidth, float cellHeight) {
        this.batch = batch;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
    }

    /**
     * Renders the given appearance at the specified pixel center position.
     * The appearance's width and height are converted from logical units to pixels.
     *
     * @param appearance The appearance to render. If null, nothing is rendered.
     * @param pixelCenterPos The center position in pixels where the appearance should be drawn.
     */
    public void renderAppearance(Appearance appearance, Vector2 pixelCenterPos) {
        if(appearance == null) return;

        // Convert appearance width/height from logical units to pixels
        float width = appearance.getWidth() * cellWidth;
        float height = appearance.getHeight() * cellHeight;

        // Center the image
        float drawX = pixelCenterPos.x - width / 2f;
        float drawY = pixelCenterPos.y - height / 2f;

        batch.draw(appearance.getTexture(), drawX, drawY, width, height);
    }
}
