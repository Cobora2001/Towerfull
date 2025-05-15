package io.github.tower_defense.Prototype;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.tower_defense.Level.Level;
import io.github.tower_defense.Level.TowerPlacementGenerator;
import io.github.tower_defense.MonsterRenderer;

import java.util.ArrayList;

public class GameArea extends Prototype {
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;
    private final ArrayList<Monster> monsters = new ArrayList<>();
    private final ArrayList<Tower> towers = new ArrayList<>();

    private float x;
    private float y;
    private float cellWidth, cellHeight;

    private boolean isPaused = false;

    private Level currentLevel;
    private int cols, rows;

    public GameArea() {
        shapeRenderer = MonsterRenderer.getInstance().getShapeRenderer();
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
        this.isPaused = gameArea.isPaused;
        this.currentLevel = gameArea.currentLevel;
        this.cols = gameArea.cols;
        this.rows = gameArea.rows;
    }

    public void placeTower(Vector2 logicalPos) {
        towers.add(new Tower(10, 10, logicalPos, 100, 2, 1, 20));
    }

    public void setLevel(Level level) {
        this.currentLevel = level;
        this.cols = level.getCols();
        this.rows = level.getRows();

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Array<Vector2> spots = TowerPlacementGenerator.generate(level);
        for (Vector2 spot : spots) {
            System.out.println("Tour générée en : " + spot);
            placeTower(spot);
        }

        spawnMonster(currentLevel.getPathPoints().first(), 5, 1, 1, 2);
    }

    public void resize(int availableWidth, int height) {
        if (cols <= 0 || rows <= 0) return;

        float levelAspect = (float) cols / rows;
        float availableAspect = (float) availableWidth / height;

        float size;
        if (availableAspect >= levelAspect) {
            size = height;
            cellHeight = size / rows;
            cellWidth = cellHeight;
            size = cellWidth * cols;
        } else {
            size = availableWidth;
            cellWidth = size / cols;
            cellHeight = cellWidth;
            size = cellHeight * rows;
        }

        x = (availableWidth - (cellWidth * cols)) / 2f;
        y = (height - (cellHeight * rows)) / 2f;

        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void update(float delta) {
        if (isPaused || cols == 0) return;

        if (currentLevel != null) {
            for (Monster monster : monsters) {
                monster.update(delta, currentLevel.getPathPoints(), this);
            }

            for (Tower tower : towers) {
                tower.update(delta, monsters, this);
            }
        }
    }

    public void render() {
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);

        renderBorder();
        renderPath();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.MAGENTA);
        for (Tower tower : towers) {
            Vector2 pos = tower.getPixelPos(this);
            shapeRenderer.circle(pos.x, pos.y, 10);
        }
        shapeRenderer.setColor(Color.RED);
        for (Monster monster : monsters) {
            Vector2 pos = monster.getPixelPos(this);
            shapeRenderer.circle(pos.x, pos.y, 8);
        }
        shapeRenderer.end();
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

        Vector2 endPoint = path.peek();
        shapeRenderer.setColor(Color.YELLOW);
        drawCell(shapeRenderer, (int) endPoint.x, (int) endPoint.y);

        Vector2 startPoint = path.first();
        shapeRenderer.setColor(Color.GREEN);
        drawCell(shapeRenderer, (int) startPoint.x, (int) startPoint.y);

        shapeRenderer.setColor(Color.BLUE);
        drawCell(shapeRenderer, (int) endPoint.x, (int) endPoint.y);

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

    public void spawnMonster(Vector2 logicalPosition, int pv, int speed, int damage, int reward) {
        Monster m = new Monster(pv, pv, logicalPosition, speed, damage, reward);
        monsters.add(m);
    }

    public Vector2 logicalToPixel(Vector2 logical) {
        float px = x + (logical.x + 0.5f) * cellWidth;
        float py = y + (logical.y + 0.5f) * cellHeight;
        return new Vector2(px, py);
    }

    public Vector2 pixelToLogical(Vector2 pixel) {
        float lx = (pixel.x - x) / cellWidth - 0.5f;
        float ly = (pixel.y - y) / cellHeight - 0.5f;
        return new Vector2(lx, ly);
    }

}
