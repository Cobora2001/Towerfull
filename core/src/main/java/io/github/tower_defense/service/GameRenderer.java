package io.github.tower_defense.service;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.tower_defense.AssetRenderer;
import io.github.tower_defense.prototype.*;

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
        renderMapBorder();
        renderPaths();
        renderBuildSpots();
        renderTowerRanges();
        renderMonsters();
    }

    private Color getColorForTowerInstance(Tower tower) {
        int hash = System.identityHashCode(tower);

        // Convert to hue between 0–360, avoid super-saturated
        float hue = (hash & 0xFFFFFF) % 360;
        float saturation = 0.4f; // softer color
        float brightness = 1f;   // full brightness

        Color base = hsbToColor(hue, saturation, brightness);
        base.a = 0.05f; // soft transparency
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
        if (gameArea.getCurrentLevel() == null) return;

        int cols = gameArea.getCols();
        int rows = gameArea.getRows();

        float width = cols * cellWidth;
        float height = rows * cellHeight;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(startPosition.x, startPosition.y, width, height);
        shapeRenderer.end();
    }


    private void renderPaths() {
        if (gameArea.getCurrentLevel() == null) return;

        Array<Vector2> path = gameArea.getCurrentLevel().getPathPoints();
        if (path == null || path.size < 2) return;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int i = 0; i < path.size - 1; i++) {
            Vector2 start = path.get(i);
            Vector2 end = path.get(i + 1);

            int x0 = (int) start.x;
            int y0 = (int) start.y;
            int x1 = (int) end.x;
            int y1 = (int) end.y;

            int dx = Integer.signum(x1 - x0);
            int dy = Integer.signum(y1 - y0);

            int x = x0;
            int y = y0;

            // Draw all steps between start and end (inclusive of start, exclusive of end)
            while (x != x1 || y != y1) {
                Color color;
                if (x == path.first().x && y == path.first().y) {
                    color = Color.GREEN; // Start
                } else {
                    color = Color.YELLOW; // Middle
                }

                shapeRenderer.setColor(color);
                Vector2 pixelPos = logicalToPixel(new Vector2(x, y));
                shapeRenderer.rect(pixelPos.x, pixelPos.y, cellWidth, cellHeight);

                x += dx;
                y += dy;
            }
        }

        // Draw the final point (end)
        Vector2 last = path.peek();
        shapeRenderer.setColor(Color.RED);
        Vector2 lastPixel = logicalToPixel(last);
        shapeRenderer.rect(lastPixel.x, lastPixel.y, cellWidth, cellHeight);

        shapeRenderer.end();
    }

    private void renderBuildSpots() {
        spriteBatch.begin();

        for (BuildSpot spot : gameArea.getBuildSpots()) {
            Vector2 pixelCenter = logicalToPixelCenter(spot.getLogicalPos());
            KillableAppearance appearance = spot.getAppearance();

            assetRenderer.renderKillable(appearance, pixelCenter);
        }

        spriteBatch.end();
    }

    private void renderMonsters() {
        spriteBatch.begin();

        for (Killable monster : gameArea.getMonsters()) {
            Vector2 pixelCenter = logicalToPixelCenter(monster.getLogicalPos());
            assetRenderer.renderKillable(monster.getAppearance(), pixelCenter);
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
