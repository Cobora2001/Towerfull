// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: Class allowing to render the game area, including towers, monsters, paths, and shots.
// -------------------------------------------------------------------------------------

package io.github.towerfull.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import io.github.towerfull.entities.defenses.BuildSpot;
import io.github.towerfull.entities.defenses.Tower;
import io.github.towerfull.gameBoard.GameArea;
import io.github.towerfull.gameBoard.level.Node;
import io.github.towerfull.screen.accessories.AssetRenderer;
import io.github.towerfull.entities.defenses.ShotRecord;
import io.github.towerfull.entities.*;

/**
 * GameRenderer is responsible for rendering the game area, including towers, monsters,
 * paths, and shots. It uses a ShapeRenderer for shapes and a SpriteBatch for textures.
 */
public class GameRenderer {
    // The game area to render
    private final GameArea gameArea;

    // The starting position of the game area in pixel coordinates
    private final Vector2 startPosition;

    // The width and height of each cell in the game area in pixel coordinates
    private final float cellWidth, cellHeight;

    // Renderers for shapes and sprites
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final SpriteBatch spriteBatch = new SpriteBatch();

    // Asset renderer for rendering appearances
    private final AssetRenderer assetRenderer;

    /**
     * Constructs a GameRenderer for the specified game area.
     *
     * @param gameArea The game area to render.
     * @param startPosition The starting position of the game area in pixel coordinates.
     * @param cellWidth The width of each cell in pixel coordinates.
     * @param cellHeight The height of each cell in pixel coordinates.
     */
    public GameRenderer(GameArea gameArea, Vector2 startPosition, float cellWidth, float cellHeight) {
        this.gameArea = gameArea;
        this.startPosition = startPosition;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.assetRenderer = new AssetRenderer(spriteBatch, cellWidth, cellHeight);
    }

    /**
     * Renders the game area, including paths, towers, monsters, and shots.
     * Uses OpenGL scissor test to limit rendering to the game area.
     */
    public void render() {
        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);

        // Define the scissor box to match the game area
        Gdx.gl.glScissor(
            (int) startPosition.x,
            (int) startPosition.y,
            (int) (gameArea.getCols() * cellWidth),
            (int) (gameArea.getRows() * cellHeight)
        );

        // All game rendering
        renderMapBorder();
        renderBackground();
        renderPaths();
        renderPathEndpoints();
        renderBuildSpots();
        renderTowerRanges();
        renderMonsters();
        renderShots();

        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
    }

    /**
     * Renders the recent shots in the game area.
     * Shots are drawn as lines with varying thickness based on damage.
     */
    private void renderShots() {
        Array<ShotRecord> shots = gameArea.getRecentShots();
        if(shots.size == 0) return;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float baseThickness = 0.2f * Math.min(cellWidth, cellHeight);

        // Optional: define min/max thickness scale
        float minScale = 0.5f;
        float maxScale = 2.0f;

        for(ShotRecord shot : shots) {
            float alpha = shot.getPercentageAlive();
            alpha = Math.max(0.2f, alpha); // Clamp alpha

            Vector2 from = logicalToPixel(shot.getFrom());
            Vector2 to = logicalToPixel(shot.getTo());

            int damage = shot.getDamage();

            // Scale thickness by damage (you can normalize if needed)
            float scale = MathUtils.clamp(damage / 10f, minScale, maxScale); // Example: damage 10 = 1.0x
            float thickness = baseThickness * scale;

            shapeRenderer.setColor(1f, 0.1f, 0.1f, alpha); // rich red
            shapeRenderer.rectLine(from, to, thickness);
        }

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    /**
     * Generates a color for a tower instance based on its identity hash code.
     * This ensures each tower has a unique but consistent color.
     *
     * @param tower The tower instance to generate a color for.
     * @return A Color object representing the tower's color.
     */
    private Color getColorForTowerInstance(Tower tower) {
        int hash = System.identityHashCode(tower);

        // Convert to hue between 0–360, avoid super-saturated
        float hue = (hash & 0xFFFFFF) % 360;
        float saturation = 0.4f; // softer color
        float brightness = 1f;   // full brightness

        Color base = hsbToColor(hue, saturation, brightness);
        base.a = 0.15f;
        return base;
    }

    /**
     * Converts HSB (Hue, Saturation, Brightness) values to a Color object.
     *
     * @param h Hue value (0–360).
     * @param s Saturation value (0–1).
     * @param b Brightness value (0–1).
     * @return A Color object representing the HSB color.
     */
    private Color hsbToColor(float h, float s, float b) {
        float c = b * s;
        float x = c * (1 - Math.abs((h / 60f) % 2 - 1));
        float m = b - c;

        float r = 0, g = 0, bl = 0;

        if(h < 60) {
            r = c; g = x;
        } else if(h < 120) {
            r = x; g = c;
        } else if(h < 180) {
            g = c; bl = x;
        } else if(h < 240) {
            g = x; bl = c;
        } else if(h < 300) {
            r = x; bl = c;
        } else {
            r = c; bl = x;
        }

        return new Color(r + m, g + m, bl + m, 1f);
    }

    /**
     * Renders the ranges of all towers in the game area.
     * Each tower's range is drawn as a circle with a semi-transparent color.
     */
    private void renderTowerRanges() {
        // Enable blending for transparency
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for(BuildSpot spot : gameArea.getBuildSpots()) {
            Tower tower = spot.getTower();
            if(tower == null) continue;

            float range = tower.getRange();
            Vector2 logicalCenter = spot.getLogicalPos().cpy().add(0.5f, 0.5f);
            Vector2 pixelCenter = logicalToPixel(logicalCenter);
            float pixelRadius = range * cellWidth;

            shapeRenderer.setColor(getColorForTowerInstance(tower));
            shapeRenderer.circle(pixelCenter.x, pixelCenter.y, pixelRadius);
        }
        shapeRenderer.end();

        // Disable blending after drawing (optional but good practice)
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    /**
     * Renders the border of the game area.
     * The border is drawn as a white rectangle around the game area.
     */
    private void renderMapBorder() {
        int cols = gameArea.getCols();
        int rows = gameArea.getRows();

        float width = cols * cellWidth;
        float height = rows * cellHeight;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(startPosition.x, startPosition.y, width, height);
        shapeRenderer.end();
    }

    /**
     * Renders the start and end points of the path in the game area.
     * The start and end points are drawn using their respective appearances.
     */
    private void renderPathEndpoints() {
        Appearance start = gameArea.getBackground().getPathStartAppearance();
        Appearance end = gameArea.getBackground().getPathEndAppearance();

        if(start == null || end == null) return;

        spriteBatch.begin();

        for(Node spawn : gameArea.getPathGraph().getSpawns()) {
            Vector2 pos = logicalToPixelCenter(spawn.getPosition());
            assetRenderer.renderAppearance(start, pos);
        }

        for(Node endNode : gameArea.getPathGraph().getEnds()) {
            Vector2 pos = logicalToPixelCenter(endNode.getPosition());
            assetRenderer.renderAppearance(end, pos);
        }

        spriteBatch.end();
    }

    /**
     * Renders the paths between nodes in the game area.
     * Paths are drawn as lines connecting nodes in the path graph.
     */
    private void renderPaths() {
        Appearance pathAppearance = gameArea.getBackground().getPathAppearance();
        if(pathAppearance == null) return;

        spriteBatch.begin();

        ObjectMap<String, Node> nodes = gameArea.getPathGraph().getNodes();

        for(Node from : nodes.values()) {
            Vector2 start = from.getPosition();

            for(Node to : from.getNextAxes()) {
                Vector2 end = to.getPosition();

                int x0 = (int) start.x;
                int y0 = (int) start.y;
                int x1 = (int) end.x;
                int y1 = (int) end.y;

                int dx = Integer.signum(x1 - x0);
                int dy = Integer.signum(y1 - y0);

                int x = x0;
                int y = y0;

                // Draw all tiles between from and to
                while(x != x1 || y != y1) {
                    Vector2 pixelPos = logicalToPixelCenter(new Vector2(x, y));
                    assetRenderer.renderAppearance(pathAppearance, pixelPos);
                    x += dx;
                    y += dy;
                }

                // Draw last tile
                Vector2 last = to.getPosition();
                Vector2 lastPixel = logicalToPixelCenter(last);
                assetRenderer.renderAppearance(pathAppearance, lastPixel);
            }
        }

        spriteBatch.end();
    }

    /**
     * Renders the background of the game area.
     * The background is drawn as a grid of appearances based on the background settings.
     */
    private void renderBackground() {
        spriteBatch.begin();

        Appearance background = gameArea.getBackground().getBackgroundAppearance();

        if(background != null) {
            for(int x = 0; x < gameArea.getCols(); ++x) {
                for(int y = 0; y < gameArea.getRows(); ++y) {
                    Vector2 pixelPos = logicalToPixel(new Vector2(x, y));
                    assetRenderer.renderAppearance(background, pixelPos.add(cellWidth / 2f, cellHeight / 2f));
                }
            }
        }

        spriteBatch.end();
    }

    /**
     * Renders all build spots in the game area.
     * Each build spot is drawn using its appearance.
     */
    private void renderBuildSpots() {
        spriteBatch.begin();

        for(BuildSpot spot : gameArea.getBuildSpots()) {
            Vector2 pixelCenter = logicalToPixelCenter(spot.getLogicalPos());
            Appearance appearance = spot.getAppearance();

            assetRenderer.renderAppearance(appearance, pixelCenter);
        }

        spriteBatch.end();
    }

    /**
     * Renders all monsters in the game area.
     * Each monster is drawn using its appearance at its logical position.
     */
    private void renderMonsters() {
        spriteBatch.begin();

        for(Printable monster : gameArea.getMonsters()) {
            Vector2 pixelCenter = logicalToPixelCenter(monster.getLogicalPos());
            assetRenderer.renderAppearance(monster.getAppearance(), pixelCenter);
        }

        spriteBatch.end();
    }

    /**
     * Converts a logical position (in cells) to a pixel position.
     *
     * @param logical The logical position to convert.
     * @return The corresponding pixel position.
     */
    public Vector2 logicalToPixel(Vector2 logical) {
        return new Vector2(
            startPosition.x + logical.x * cellWidth,
            startPosition.y + logical.y * cellHeight
        );
    }

    /**
     * Converts a logical position (in cells) to a pixel position at the center of the cell.
     *
     * @param logical The logical position to convert.
     * @return The corresponding pixel position at the center of the cell.
     */
    public Vector2 logicalToPixelCenter(Vector2 logical) {
        return logicalToPixel(logical).add(cellWidth / 2f, cellHeight / 2f);
    }

    /**
     * Converts a pixel position to a logical position (in cells).
     *
     * @param pixel The pixel position to convert.
     * @return The corresponding logical position.
     */
    public Vector2 pixelToLogical(Vector2 pixel) {
        return new Vector2(
            (pixel.x - startPosition.x) / cellWidth,
            (pixel.y - startPosition.y) / cellHeight
        );
    }

    /**
     * Disposes of the resources used by the GameRenderer.
     * This should be called when the renderer is no longer needed.
     */
    public void dispose() {
        shapeRenderer.dispose();
        spriteBatch.dispose();
    }
}
