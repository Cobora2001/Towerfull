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
import io.github.towerfull.gameBoard.level.Axis;
import io.github.towerfull.screen.accessories.AssetRenderer;
import io.github.towerfull.entities.defenses.ShotRecord;
import io.github.towerfull.entities.*;

public class GameRenderer {
    private final GameArea gameArea;
    private final Vector2 startPosition;
    private final float cellWidth, cellHeight;

    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final SpriteBatch spriteBatch = new SpriteBatch();

    private final AssetRenderer assetRenderer;

    public GameRenderer(GameArea gameArea, Vector2 startPosition, float cellWidth, float cellHeight) {
        this.gameArea = gameArea;
        this.startPosition = startPosition;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.assetRenderer = new AssetRenderer(spriteBatch, cellWidth, cellHeight);
    }

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

    private void renderShots() {
        Array<ShotRecord> shots = gameArea.getRecentShots();
        if (shots.size == 0) return;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float baseThickness = 0.2f * Math.min(cellWidth, cellHeight);

        // Optional: define min/max thickness scale
        float minScale = 0.5f;
        float maxScale = 2.0f;

        for (ShotRecord shot : shots) {
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

    private Color hsbToColor(float h, float s, float b) {
        float c = b * s;
        float x = c * (1 - Math.abs((h / 60f) % 2 - 1));
        float m = b - c;

        float r = 0, g = 0, bl = 0;

        if (h < 60) {
            r = c; g = x;
        } else if (h < 120) {
            r = x; g = c;
        } else if (h < 180) {
            g = c; bl = x;
        } else if (h < 240) {
            g = x; bl = c;
        } else if (h < 300) {
            r = x; bl = c;
        } else {
            r = c; bl = x;
        }

        return new Color(r + m, g + m, bl + m, 1f);
    }

    private void renderTowerRanges() {
        // Enable blending for transparency
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (BuildSpot spot : gameArea.getBuildSpots()) {
            Tower tower = spot.getTower();
            if (tower == null) continue;

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

    private void renderPathEndpoints() {
        Appearance start = gameArea.getBackground().getPathStartAppearance();
        Appearance end = gameArea.getBackground().getPathEndAppearance();

        if (start == null || end == null) return;

        spriteBatch.begin();

        for (Axis spawn : gameArea.getPathGraph().getSpawns()) {
            Vector2 pos = logicalToPixelCenter(spawn.getPosition());
            assetRenderer.renderAppearance(start, pos);
        }

        for (Axis endNode : gameArea.getPathGraph().getEnds()) {
            Vector2 pos = logicalToPixelCenter(endNode.getPosition());
            assetRenderer.renderAppearance(end, pos);
        }

        spriteBatch.end();
    }

    private void renderPaths() {
        Appearance pathAppearance = gameArea.getBackground().getPathAppearance();
        if (pathAppearance == null) return;

        spriteBatch.begin();

        ObjectMap<String, Axis> nodes = gameArea.getPathGraph().getNodes();

        for (Axis from : nodes.values()) {
            Vector2 start = from.getPosition();

            for (Axis to : from.getNextAxes()) {
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
                while (x != x1 || y != y1) {
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

    private void renderBackground() {
        spriteBatch.begin();

        Appearance background = gameArea.getBackground().getBackgroundAppearance();

        if (background != null) {
            for (int x = 0; x < gameArea.getCols(); x++) {
                for (int y = 0; y < gameArea.getRows(); y++) {
                    Vector2 pixelPos = logicalToPixel(new Vector2(x, y));
                    assetRenderer.renderAppearance(background, pixelPos.add(cellWidth / 2f, cellHeight / 2f));
                }
            }
        }

        spriteBatch.end();
    }

    private void renderBuildSpots() {
        spriteBatch.begin();

        for (BuildSpot spot : gameArea.getBuildSpots()) {
            Vector2 pixelCenter = logicalToPixelCenter(spot.getLogicalPos());
            Appearance appearance = spot.getAppearance();

            assetRenderer.renderAppearance(appearance, pixelCenter);
        }

        spriteBatch.end();
    }

    private void renderMonsters() {
        spriteBatch.begin();

        for (Printable monster : gameArea.getMonsters()) {
            Vector2 pixelCenter = logicalToPixelCenter(monster.getLogicalPos());
            assetRenderer.renderAppearance(monster.getAppearance(), pixelCenter);
        }

        spriteBatch.end();
    }

    public Vector2 logicalToPixel(Vector2 logical) {
        return new Vector2(
            startPosition.x + logical.x * cellWidth,
            startPosition.y + logical.y * cellHeight
        );
    }

    public Vector2 logicalToPixelCenter(Vector2 logical) {
        return logicalToPixel(logical).add(cellWidth / 2f, cellHeight / 2f);
    }

    public Vector2 pixelToLogical(Vector2 pixel) {
        return new Vector2(
            (pixel.x - startPosition.x) / cellWidth,
            (pixel.y - startPosition.y) / cellHeight
        );
    }

    public void dispose() {
        shapeRenderer.dispose();
        spriteBatch.dispose();
    }
}
