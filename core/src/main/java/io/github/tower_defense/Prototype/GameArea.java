package io.github.tower_defense.Prototype;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.tower_defense.Level.BuildSpot;
import io.github.tower_defense.Level.Level;
import io.github.tower_defense.Level.TowerPlacementGenerator;
import io.github.tower_defense.Listener.GameOverListener;
import io.github.tower_defense.Loader.JavaLoader;
import io.github.tower_defense.AssetRenderer;

import java.util.List;

public class GameArea extends Prototype {

    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;

    private final Array<Monster> monsters = new Array<>();
    private Array<BuildSpot> buildSpots = new Array<>();

    private final PrototypeFactory<MonsterType, Monster> prototypeFactory = new PrototypeFactory<>();
    private Scenario scenario;

    private float x, y;
    private float cellWidth, cellHeight;
    private boolean isPaused = false;

    private Level currentLevel;
    private int cols, rows;

    private int gold = 100; // Starting gold

    private int life = 20; // Starting life

    private GameOverListener gameOverListener;

    public void setGameOverListener(GameOverListener listener) {
        this.gameOverListener = listener;
    }

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
        this.isPaused = gameArea.isPaused;
        this.currentLevel = gameArea.currentLevel;
        this.cols = gameArea.cols;
        this.rows = gameArea.rows;
    }

    public void addMonster(Monster m) {
        monsters.add(m);
    }

    public void setLevel(Level level) {
        this.currentLevel = level;
        this.cols = level.getCols();
        this.rows = level.getRows();
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Vector2 spawn = currentLevel.getPathPoints().first();

        JavaLoader.get().loadMonsterPrototypes("monsters/monsters.json", prototypeFactory);

        List<WaveEntry> wave1 = JavaLoader.get().getWaveEntries("wave1");
        List<WaveEntry> wave2 = JavaLoader.get().getWaveEntries("wave2");

        Wave w1 = new Wave(wave1, prototypeFactory, monsters, spawn);
        Wave w2 = new Wave(wave2, prototypeFactory, monsters, spawn);

        scenario = new Scenario(monsters);
        scenario.addWave(w1);
        scenario.addWave(w2);

        scenario = new Scenario(monsters);
        scenario.addWave(w1);
        scenario.addWave(w2);
        scenario.startNextWave();

        for(Vector2 pos : TowerPlacementGenerator.generate(level)) {
            buildSpots.add(new BuildSpot(pos));
        }
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

    public Array<Tower> getBuiltTowers() {
        Array<Tower> towers = new Array<>();
        for (BuildSpot spot : buildSpots) {
            if (spot.hasTower()) {
                towers.add(spot.getTower());
            }
        }
        return towers;
    }

    public void update(float delta) {
        if (isPaused || cols == 0) return;

        if (scenario != null && currentLevel != null) {
            scenario.update(delta);
        }

        // 🔁 Mise à jour des monstres + nettoyage si arrivée
        for (int i = monsters.size - 1; i >= 0; i--) {
            Monster monster = monsters.get(i);
            monster.update(delta, currentLevel.getPathPoints(), this);

            if (monster.hasReachedEnd()) {
                monsters.removeIndex(i);
                System.out.println("❌ Monstre arrivé à la fin du chemin !");
                loseLife(monster.getDamage());
            }
        }

        for (Tower tower : getBuiltTowers()) {
            tower.update(delta, monsters, this);
        }
    }

    public int getLife() {
        return life;
    }

    public void loseLife(int amount) {
        life -= amount;
        if (life <= 0) {
            life = 0;
            System.out.println("💀 Game Over!");
            if (gameOverListener != null) {
                gameOverListener.onGameOver();
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
        for (BuildSpot spot : buildSpots) {
            if (spot.isUsed()) {
                Tower tower = spot.getTower();
                Vector2 pos = tower.getPixelPos(this);
                shapeRenderer.rect(pos.x - 8, pos.y - 8, 16, 16);
            } else {
                Vector2 pos = spot.getPixelPos(this);
                shapeRenderer.circle(pos.x, pos.y, 8);
            }
        }

        SpriteBatch spriteBatch = new SpriteBatch();

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        AssetRenderer renderer = new AssetRenderer(spriteBatch);
        for (Monster monster : monsters) {
            Vector2 pixelPos = logicalToPixel(monster.getLogicalPos());
            renderer.renderKillable(monster.getAppearance(), pixelPos);
        }

        spriteBatch.end(); // ✅ closes the batch properly

        shapeRenderer.end(); // ✅ ends shapeRenderer separately
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

    public int getGold() {
        return gold;
    }

    public void addGold(int amount) {
        gold += amount;
    }

    public boolean spendGold(int amount) {
        if (gold >= amount) {
            gold -= amount;
            return true;
        }
        return false; // Not enough gold
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

    public void spawnMonster(Vector2 logicalPosition, int pv, int speed, int damage, int reward, KillableAppearance appearance) {
        Monster m = new Monster(pv, pv, logicalPosition, speed, damage, reward, appearance);
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
