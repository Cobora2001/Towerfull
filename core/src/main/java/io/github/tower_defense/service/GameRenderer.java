package io.github.tower_defense.service;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.tower_defense.prototype.*;

public class GameRenderer {

    private final GameArea gameArea;
    private final Vector2 startPosition;
    private final float cellWidth, cellHeight;

    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final SpriteBatch spriteBatch = new SpriteBatch();

    public GameRenderer(GameArea gameArea, Vector2 startPosition, float cellWidth, float cellHeight) {
        this.gameArea = gameArea;
        this.startPosition = startPosition;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
    }

    public void render() {
        renderMapBorder();
        renderPaths();
        renderBuildSpots();
        renderTowers();
        renderMonsters();
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
        if (path == null || path.isEmpty()) return;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int i = 0; i < path.size; i++) {
            Vector2 point = path.get(i);
            Vector2 pos = logicalToPixel(point);

            if (i == 0) shapeRenderer.setColor(Color.GREEN);        // Start
            else if (i == path.size - 1) shapeRenderer.setColor(Color.RED); // End
            else shapeRenderer.setColor(Color.YELLOW);              // Middle

            shapeRenderer.rect(pos.x, pos.y, cellWidth, cellHeight);
        }

        shapeRenderer.end();
    }


    private void renderBuildSpots() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (BuildSpot spot : gameArea.getBuildSpots()) {
            Vector2 pos = logicalToPixel(spot.getLogicalPos());
            shapeRenderer.setColor(spot.isUsed() ? Color.RED : Color.GREEN);
            shapeRenderer.rect(pos.x, pos.y, cellWidth, cellHeight);
        }

        shapeRenderer.end();
    }

    private void renderTowers() {
        spriteBatch.begin();

        for (BuildSpot spot : gameArea.getBuildSpots()) {
            if (spot.isUsed()) {
                Tower tower = spot.getTower();
                if (tower != null) {
                    KillableAppearance appearance = tower.getAppearance();
                    if (appearance != null && appearance.getTexture() != null) {
                        Vector2 pixelPos = logicalToPixel(spot.getLogicalPos());
                        spriteBatch.draw(
                            appearance.getTexture(),
                            pixelPos.x, pixelPos.y,
                            cellWidth, cellHeight
                        );
                    }
                }
            }
        }

        spriteBatch.end();
    }

    private void renderMonsters() {
        spriteBatch.begin();

        for (Monster monster : gameArea.getMonsters()) {
            KillableAppearance appearance = monster.getAppearance();
            if (appearance != null && appearance.getTexture() != null) {
                Vector2 pixelPos = logicalToPixel(monster.getLogicalPos());
                spriteBatch.draw(
                    appearance.getTexture(),
                    pixelPos.x, pixelPos.y,
                    cellWidth, cellHeight
                );
            }
        }

        spriteBatch.end();
    }

    public Vector2 logicalToPixel(Vector2 logical) {
        return new Vector2(
            startPosition.x + logical.x * cellWidth,
            startPosition.y + logical.y * cellHeight
        );
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
