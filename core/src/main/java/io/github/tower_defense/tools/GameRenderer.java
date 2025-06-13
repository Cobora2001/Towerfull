package io.github.tower_defense.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.tower_defense.entities.defenses.BuildSpot;
import io.github.tower_defense.entities.defenses.Tower;
import io.github.tower_defense.enumElements.AppearanceId;
import io.github.tower_defense.gameBoard.GameArea;
import io.github.tower_defense.screen.accessories.AssetRenderer;
import io.github.tower_defense.entities.defenses.ShotRecord;
import io.github.tower_defense.entities.*;

public class GameRenderer {
    private static final AppearanceId backgroundAppearance = AppearanceId.GRASS;
    private static final AppearanceId pathappearance = AppearanceId.COBBLE;
    private static final AppearanceId spawnAppearance = AppearanceId.PORTAL;
    private static final AppearanceId endAppearance = AppearanceId.TEMPLE;

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
        renderBackground();
        renderPaths();
        renderPathEndpoints();
        renderBuildSpots();
        renderTowerRanges();
        renderMonsters();
        renderShots();
    }

    private void renderShots() {
        Array<ShotRecord> shots = gameArea.getRecentShots();
        if (shots.size == 0) return;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(1f, 0f, 0f, 0.8f)); // bright red line

        for (ShotRecord shot : shots) {
            Vector2 from = logicalToPixel(shot.getFrom());
            Vector2 to = logicalToPixel(shot.getTo());
            shapeRenderer.rectLine(from, to, (float) shot.getDamage());
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
        Array<Vector2> path = gameArea.getPathPoints();
        if (path == null || path.size == 0) return;

        Appearance portal = GameAssets.get().appearances.get(spawnAppearance);
        Appearance temple = GameAssets.get().appearances.get(endAppearance);
        if (portal == null || temple == null) return;

        spriteBatch.begin();

        Vector2 startPixel = logicalToPixelCenter(path.first());
        Vector2 endPixel = logicalToPixelCenter(path.peek());

        assetRenderer.renderAppearance(portal, startPixel);
        assetRenderer.renderAppearance(temple, endPixel);

        spriteBatch.end();
    }

    private void renderPaths() {
        Array<Vector2> path = gameArea.getPathPoints();
        if (path == null || path.size < 2) return;

        Appearance cobble = GameAssets.get().appearances.get(pathappearance);
        if (cobble == null) return;

        spriteBatch.begin();

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

            // Draw all tiles between start and end
            while (x != x1 || y != y1) {
                Vector2 pixelPos = logicalToPixelCenter(new Vector2(x, y));
                assetRenderer.renderAppearance(cobble, pixelPos);
                x += dx;
                y += dy;
            }
        }

        // Also render the last tile
        Vector2 last = path.peek();
        Vector2 lastPixel = logicalToPixelCenter(last);
        assetRenderer.renderAppearance(cobble, lastPixel);

        spriteBatch.end();
    }

    private void renderBackground() {
        spriteBatch.begin();

        Appearance background = GameAssets.get().appearances.get(backgroundAppearance);

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
