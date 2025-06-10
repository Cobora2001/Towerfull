package io.github.tower_defense.prototype;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.tower_defense.ShotRecord;
import io.github.tower_defense.level.Level;
import io.github.tower_defense.level.TowerPlacementGenerator;
import io.github.tower_defense.listener.LevelListener;
import io.github.tower_defense.loader.JsonLoader;

public class GameArea extends Prototype {

    private final Array<Monster> monsters = new Array<>();
    private final Array<BuildSpot> buildSpots = new Array<>();

    private PrototypeFactory<MonsterType, Monster> prototypeFactory = new PrototypeFactory<>();
    private PrototypeFactory<TowerType, Tower> towerFactory = new PrototypeFactory<>();

    private EconomyManager economyManager;
    private Scenario scenario;

    private boolean isPaused = false;
    private int life = 20;

    private Level currentLevel;
    private int cols, rows;

    private LevelListener levelListener;

    private final Array<ShotRecord> recentShots = new Array<>();

    public GameArea() {}

    public GameArea(GameArea gameArea) {
        this.cols = gameArea.cols;
        this.rows = gameArea.rows;
        this.isPaused = gameArea.isPaused;
        this.life = gameArea.life;
        this.currentLevel = gameArea.currentLevel;
        this.prototypeFactory = gameArea.prototypeFactory.clone();
        this.economyManager = gameArea.economyManager.clone();
        this.scenario = gameArea.scenario.clone();

        for (BuildSpot spot : gameArea.buildSpots) {
            this.buildSpots.add(spot.clone());
        }

        for (Monster m : gameArea.monsters) {
            this.monsters.add(m.clone());
        }
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

        scenario = new Scenario(monsters, spawn);
        scenario.loadWavesFromIndex("waves/index.json", prototypeFactory, spawn);

        for (Vector2 pos : TowerPlacementGenerator.generate(level)) {
            buildSpots.add(new BuildSpot(pos));
        }

        Gdx.app.log("GameArea", "✅ Niveau prêt, scénario initialisé.");
    }

    public void update(float delta) {
        if (isPaused || cols == 0) return;

        if (scenario != null && currentLevel != null) {
            scenario.update(delta);
        }

        Array<Vector2> pathPoints = currentLevel.getPathPoints();
        for (int i = monsters.size - 1; i >= 0; i--) {
            Monster monster = monsters.get(i);

            monster.update(delta, pathPoints);

            if (monster.hasReachedEnd()) {
                monsters.removeIndex(i);
                loseLife(monster.getDamage());
            } else if (monster.isDead()) {
                monsters.removeIndex(i);
                economyManager.earnGold(monster.getReward());
            }
        }

        for (BuildSpot spot : buildSpots) {
            if (spot.isUsed()) {
                Tower tower = spot.getTower();
                if (tower != null) {
                    tower.update(delta, monsters, this, spot.getLogicalPos());
                }
            }
        }

        // Update their time and remove them if they've been there long enough
        for (int i = recentShots.size - 1; i >= 0; i--) {
            recentShots.get(i).updateTime(delta);
            float timeShotsLeftOn = 0.3f;
            if(recentShots.get(i).getTimeSinceShot() > timeShotsLeftOn) {
                recentShots.removeIndex(i);
            }
        }

        if (life <= 0) {
            if (levelListener != null) {
                levelListener.onGameOver();
            }
        }

        boolean noActiveMonsters = monsters.size == 0;
        boolean noMoreWaves = scenario.allWavesFinished();

        if (noActiveMonsters && noMoreWaves && levelListener != null) {
            levelListener.onLevelComplete();
        }
    }

    public void spawnMonster(Vector2 logicalPosition, int pv, int speed, int damage, int reward, Appearance appearance) {
        Monster m = new Monster(pv, pv, logicalPosition, speed, damage, reward, appearance);
        monsters.add(m);
    }

    public Array<BuildSpot> getBuildSpots() {
        return buildSpots;
    }

    public Array<Monster> getMonsters() {
        return monsters;
    }

    public void addShot(Vector2 from, Vector2 to) {
        recentShots.add(new ShotRecord(from.cpy(), to.cpy()));
    }

    public Array<ShotRecord> getRecentShots() {
        return recentShots;
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

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public int getLife() {
        return life;
    }

    public void loseLife(int amount) {
        life -= amount;
        if (life <= 0) {
            life = 0;
            System.out.println("💀 Game Over!");
            if (levelListener != null) {
                levelListener.onGameOver();
            }
        }
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public void setLevelListener(LevelListener listener) {
        this.levelListener = listener;
    }

    public GameArea clone() {
        return new GameArea(this);
    }
}
