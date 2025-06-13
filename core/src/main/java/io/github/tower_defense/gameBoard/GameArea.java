package io.github.tower_defense.gameBoard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.tower_defense.tools.Prototype;
import io.github.tower_defense.entities.defenses.ShotRecord;
import io.github.tower_defense.entities.defenses.BuildSpot;
import io.github.tower_defense.entities.defenses.Tower;
import io.github.tower_defense.entities.ennemies.Monster;
import io.github.tower_defense.entities.ennemies.Scenario;
import io.github.tower_defense.enumElements.MonsterType;
import io.github.tower_defense.gameBoard.level.Level;
import io.github.tower_defense.gameBoard.level.generators.TowerPlacementGenerator;
import io.github.tower_defense.listener.LevelListener;
import io.github.tower_defense.tools.GameAssets;
import io.github.tower_defense.tools.PrototypeFactory;

public class GameArea extends Prototype {

    private final Array<Monster> monsters = new Array<>();
    private final Array<BuildSpot> buildSpots = new Array<>();

    private PrototypeFactory<MonsterType, Monster> prototypeFactory = new PrototypeFactory<>();

    private final Array<Vector2> pathPoints;

    private final EconomyManager economyManager;
    private final Scenario scenario;

    private boolean isPaused = false;
    private int life;

    private int cols, rows;

    private LevelListener levelListener;

    private final Array<ShotRecord> recentShots = new Array<>();

    public GameArea(Level level) {
        this.economyManager = new EconomyManager(level.getStartingGold());
        this.life = level.getStartingLife();

        if (level == null) {
            Gdx.app.error("GameArea", "❌ Niveau null");
            this.pathPoints = new Array<>();
            this.scenario = null;
            return;
        }

        this.pathPoints = level.getPathPoints();
        this.scenario = level.getScenario();

        this.cols = level.getCols();
        this.rows = level.getRows();

        try {
            GameAssets.get().loadAll();
        } catch (Exception e) {
            Gdx.app.error("GameArea", "❌ Erreur lors du chargement des assets JSON", e);
            return;
        }

        Vector2 spawn = level.getPathPoints().first();
        if (spawn == null) {
            Gdx.app.error("GameArea", "❌ Point de spawn null");
            return;
        }

        buildSpots.clear();

        Array<Vector2> places = level.getBuildableTiles();

        for (Vector2 pos : places) {
            buildSpots.add(new BuildSpot(pos));
        }

        Gdx.app.log("GameArea", "✅ Niveau prêt, scénario initialisé.");
    }

    public GameArea(GameArea gameArea) {
        this.cols = gameArea.cols;
        this.rows = gameArea.rows;
        this.isPaused = gameArea.isPaused;
        this.life = gameArea.life;
        this.prototypeFactory = gameArea.prototypeFactory.clone();
        this.economyManager = gameArea.economyManager.clone();
        this.pathPoints = gameArea.pathPoints;
        this.scenario = gameArea.scenario.clone();

        for (BuildSpot spot : gameArea.buildSpots) {
            this.buildSpots.add(spot.clone());
        }

        for (Monster m : gameArea.monsters) {
            this.monsters.add(m.clone());
        }
    }

    public void update(float delta) {
        if (isPaused || cols == 0) return;

        // Updated: ask scenario to add monsters based on the current time
        if (scenario != null) {
            scenario.update(delta, this);
        }

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

        for (int i = recentShots.size - 1; i >= 0; i--) {
            recentShots.get(i).updateTime(delta);
            if (recentShots.get(i).getTimeSinceShot() > 0.3f) {
                recentShots.removeIndex(i);
            }
        }

        if (life <= 0 && levelListener != null) {
            levelListener.onGameOver();
            return;
        }

        if (monsters.size == 0 && scenario.isFinished() && levelListener != null) {
            levelListener.onLevelComplete();
        }
    }

    public void spawnMonster(Monster monster) {
        if (monster == null) {
            Gdx.app.error("GameArea", "❌ Tentative de spawn d'un monstre null");
            return;
        }
        monster.setLogicalPos(new Vector2(pathPoints.first()));
        monsters.add(monster);
    }

    public Array<BuildSpot> getBuildSpots() {
        return buildSpots;
    }

    public Array<Monster> getMonsters() {
        return monsters;
    }

    public void addShot(Vector2 from, Vector2 to, int damage) {
        recentShots.add(new ShotRecord(from.cpy(), to.cpy(), damage));
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

    public void setLevelListener(LevelListener listener) {
        this.levelListener = listener;
    }

    public GameArea clone() {
        return new GameArea(this);
    }

    public Array<Vector2> getPathPoints() {
        return pathPoints;
    }
}
