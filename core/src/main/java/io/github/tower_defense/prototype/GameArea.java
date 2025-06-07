package io.github.tower_defense.prototype;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.tower_defense.level.Level;
import io.github.tower_defense.level.TowerPlacementGenerator;
import io.github.tower_defense.listener.GameOverListener;
import io.github.tower_defense.loader.JsonLoader;
import io.github.tower_defense.AssetRenderer;
import io.github.tower_defense.screen.ConstructionMenu;
import io.github.tower_defense.service.AssetLoaderService;

import java.util.List;

public class GameArea extends Prototype {

    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;

    private final Array<Monster> monsters = new Array<>();
    private final Array<BuildSpot> buildSpots = new Array<>();

    private PrototypeFactory<MonsterType, Monster> prototypeFactory = new PrototypeFactory<>();
    private PrototypeFactory<TowerType, Tower> towerFactory = new PrototypeFactory<>();

    private ConstructionMenu constructionMenu;
    private BuildSpot selectedBuildSpot;
    private AssetLoaderService assetLoaderService;

    private EconomyManager economyManager;
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
        this.shapeRenderer = new ShapeRenderer();
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        this.x = gameArea.x;
        this.y = gameArea.y;
        this.cellWidth = gameArea.cellWidth;
        this.cellHeight = gameArea.cellHeight;
        this.isPaused = gameArea.isPaused;
        this.cols = gameArea.cols;
        this.rows = gameArea.rows;
        this.gold = gameArea.gold;
        this.life = gameArea.life;

        // Deep copy build spots
        for (BuildSpot spot : gameArea.buildSpots) {
            this.buildSpots.add(spot.clone());
        }

        // Deep copy monsters
        for (Monster m : gameArea.monsters) {
            this.monsters.add(m.clone()); // Assuming Monster has a copy constructor
        }

        // Clone prototype factory (assuming it’s OK to share, or deep copy it if needed)
        this.prototypeFactory = gameArea.prototypeFactory.clone(); // You might need to implement this

        // Clone level if needed (you can share it if it’s immutable)
        this.currentLevel = gameArea.currentLevel;
    }

    public void addMonster(Monster m) {
        monsters.add(m);
    }

    public void setLevel(Level level) {
        this.economyManager = new EconomyManager();
        this.currentLevel = level;

        if (level == null) {
            Gdx.app.error("GameArea", "❌ Niveau null transmis à GameArea.setLevel()");
            return;
        }

        this.cols = level.getCols();
        this.rows = level.getRows();
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // 🔄 Initialisation des assets
        try {
            JsonLoader.get().loadAppearancePrototypes("appearances.json");
            JsonLoader.get().loadMonsterPrototypes("monsters/monsters.json", prototypeFactory);
            JsonLoader.get().loadTowerPrototypes("towers/towers.json", towerFactory);
        } catch (Exception e) {
            Gdx.app.error("GameArea", "❌ Erreur lors du chargement des assets JSON", e);
            return;
        }

        Vector2 spawn = currentLevel.getPathPoints().first();
        if (spawn == null) {
            Gdx.app.error("GameArea", "❌ Point de spawn null");
            return;
        }

        // Chargement des vagues
        List<WaveEntry> wave1 = JsonLoader.get().getWaveEntries("wave1");
        List<WaveEntry> wave2 = JsonLoader.get().getWaveEntries("wave2");

        scenario = new Scenario(monsters, spawn);
        scenario.addWave(new Wave(wave1, prototypeFactory, monsters, spawn));
        scenario.addWave(new Wave(wave2, prototypeFactory, monsters, spawn));
        scenario.startNextWave();

        // Initialisation des build spots
        for (Vector2 pos : TowerPlacementGenerator.generate(level)) {
            buildSpots.add(new BuildSpot(pos));
        }

        // Construction de test (à sécuriser)
//        for (BuildSpot spot : buildSpots) {
//            if (!spot.isUsed() && towerFactory.contains(TowerType.CASTLE)) {
//                Tower tower = towerFactory.create(TowerType.CASTLE);
//                if (tower != null) {
//                    spot.setTower(tower);
//                }
//            }
//        }
    }


    public void resize(int availableWidth, int availableHeight) {
        if (cols <= 0 || rows <= 0) return;

        float levelAspect = (float) cols / rows;
        float screenAspect = (float) availableWidth / availableHeight;

        if (screenAspect >= levelAspect) {
            // Screen is wider — height is limiting factor
            cellHeight = (float) availableHeight / rows;
            cellWidth = cellHeight;
        } else {
            // Screen is taller — width is limiting factor
            cellWidth = (float) availableWidth / cols;
            cellHeight = cellWidth;
        }

        // Center the level
        float totalWidth = cellWidth * cols;
        float totalHeight = cellHeight * rows;

        x = (availableWidth - totalWidth) / 2f;
        y = (availableHeight - totalHeight) / 2f;

        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public Array<BuildSpot> getBuiltSpots() {
        Array<BuildSpot> builtSpots = new Array<>();
        for (BuildSpot spot : buildSpots) {
            if (spot.isUsed()) {
                builtSpots.add(spot);
            }
        }
        return builtSpots;
    }

    public void update(float delta) {
        if (isPaused || cols == 0) return;

        if (scenario != null && currentLevel != null) {
            scenario.update(delta);
        }

        Array<Vector2> pathPoints = (currentLevel != null) ? currentLevel.getPathPoints() : null;

        for (int i = monsters.size - 1; i >= 0; i--) {
            Monster monster = monsters.get(i);

            if (pathPoints != null) {
                monster.update(delta, pathPoints, this);
            }

            if (monster.hasReachedEnd()) {
                monsters.removeIndex(i);
                loseLife(monster.getDamage());
            }
        }

        for (BuildSpot spot : getBuiltSpots()) {
            Tower tower = spot.getTower();
            if (tower != null) {
                tower.update(delta, monsters, this, spot.getLogicalPos());
            }
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

        // --- Start sprite rendering ---
        SpriteBatch spriteBatch = new SpriteBatch(); // Ideally move this outside render()
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        AssetRenderer renderer = new AssetRenderer(spriteBatch, cellWidth, cellHeight);

        // Render monsters
        for (Monster monster : monsters) {
            Vector2 pixelPos = logicalToPixel(monster.getLogicalPos());
            renderer.renderKillable(monster.getAppearance(), pixelPos);
        }

        // Render towers on build spots
        for (BuildSpot spot : buildSpots) {
            Vector2 pixelPos = logicalToPixel(spot.getLogicalPos());
            renderer.renderKillable(spot.getAppearance(), pixelPos);
        }

        spriteBatch.end(); // ✅ Close batch
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

    public float getCellWidth() {
        return cellWidth;
    }

    public EconomyManager getEconomyManager() {
        return this.economyManager;
    }

    public Array<BuildSpot> getBuildSpots() {
        return buildSpots;
    }


}
