package io.github.tower_defense.Prototype;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.tower_defense.Level.Level;

public class GameArea extends Prototype{
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;

    private float x;
    private float y;
    private float cellWidth, cellHeight;

    private float redX = 0;
    private float redY = 3;

    private boolean isPaused = false;

    private Level currentLevel;
    private int cols, rows;

    public GameArea() {
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public GameArea(GameArea gameArea) {
        this.shapeRenderer = gameArea.shapeRenderer;
        this.camera = gameArea.camera;
        this.x = gameArea.x;
        this.y = gameArea.y;
        this.cellWidth = gameArea.cellWidth;
        this.cellHeight = gameArea.cellHeight;
        this.redX = gameArea.redX;
        this.redY = gameArea.redY;
        this.isPaused = gameArea.isPaused;
        this.currentLevel = gameArea.currentLevel;
        this.cols = gameArea.cols;
        this.rows = gameArea.rows;
    }

    public void setLevel(Level level) {
        this.currentLevel = level;
        this.cols = level.getCols();
        this.rows = level.getRows();
    }

    public void resize(int availableWidth, int height) {
        if (cols <= 0 || rows <= 0) return;

        float levelAspect = (float) cols / rows;
        float availableAspect = (float) availableWidth / height;

        float size;
        if (availableAspect >= levelAspect) {
            // Screen is wider than level → height is limiting factor
            size = height;
            cellHeight = size / rows;
            cellWidth = cellHeight; // Keep square cells
            size = cellWidth * cols; // Full width of game area
        } else {
            // Screen is taller than level → width is limiting factor
            size = availableWidth;
            cellWidth = size / cols;
            cellHeight = cellWidth;
            size = cellHeight * rows; // Full height of game area
        }

        x = (availableWidth - (cellWidth * cols)) / 2f;
        y = (height - (cellHeight * rows)) / 2f;

        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void update(float delta) {
        if (isPaused || cols == 0) return;

        redX += delta * 2f; // Move 2 cells per second
        if (redX >= cols) redX = 0;
    }

    public void render() {
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);

        renderBorder();
        renderPath();
        renderMovingSquare();
    }

    private void renderBorder() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(x, y, cellWidth * cols, cellHeight * rows);
        shapeRenderer.end();
    }

    private void renderPath() {
        if (currentLevel == null) return;

        Array<Vector2> path = currentLevel.getPathPoints();
        if (path.size == 0) return;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Fill full path
        for (int i = 0; i < path.size - 1; i++) {
            Vector2 start = path.get(i);
            Vector2 end = path.get(i + 1);

            int x1 = (int) start.x;
            int y1 = (int) start.y;
            int x2 = (int) end.x;
            int y2 = (int) end.y;

            int dx = Integer.signum(x2 - x1);
            int dy = Integer.signum(y2 - y1);

            int xCurr = x1;
            int yCurr = y1;

            while (xCurr != x2 || yCurr != y2) {
                shapeRenderer.setColor(Color.YELLOW);
                drawCell(shapeRenderer, xCurr, yCurr);
                if (xCurr != x2) xCurr += dx;
                if (yCurr != y2) yCurr += dy;
            }
        }

        // Draw final point
        Vector2 endPoint = path.peek();
        shapeRenderer.setColor(Color.YELLOW);
        drawCell(shapeRenderer, (int) endPoint.x, (int) endPoint.y);

        // Draw start in green
        Vector2 startPoint = path.first();
        shapeRenderer.setColor(Color.GREEN);
        drawCell(shapeRenderer, (int) startPoint.x, (int) startPoint.y);

        // Draw end in blue
        shapeRenderer.setColor(Color.BLUE);
        drawCell(shapeRenderer, (int) endPoint.x, (int) endPoint.y);

        shapeRenderer.end();
    }

    private void renderMovingSquare() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(
            x + redX * cellWidth,
            y + redY * cellHeight,
            cellWidth,
            cellHeight
        );
        shapeRenderer.end();
    }

    private void drawCell(ShapeRenderer renderer, int col, int row) {
        float px = x + col * cellWidth;
        float py = y + row * cellHeight;
        renderer.rect(px, py, cellWidth, cellHeight);
    }

    public void dispose() {
        if (shapeRenderer != null) shapeRenderer.dispose();
    }

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public GameArea clone() {
        return new GameArea(this);
    }
}
