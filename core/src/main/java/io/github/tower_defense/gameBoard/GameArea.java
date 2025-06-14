package io.github.tower_defense.gameBoard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.tower_defense.gameBoard.level.Axis;
import io.github.tower_defense.gameBoard.level.Background;
import io.github.tower_defense.gameBoard.level.PathGraph;
import io.github.tower_defense.listener.LifeListener;
import io.github.tower_defense.tools.Prototype;
import io.github.tower_defense.entities.defenses.ShotRecord;
import io.github.tower_defense.entities.defenses.BuildSpot;
import io.github.tower_defense.entities.defenses.Tower;
import io.github.tower_defense.entities.ennemies.Monster;
import io.github.tower_defense.entities.ennemies.Scenario;
import io.github.tower_defense.enumElements.MonsterType;
import io.github.tower_defense.gameBoard.level.Level;
import io.github.tower_defense.listener.LevelListener;
import io.github.tower_defense.tools.PrototypeFactory;

public class GameArea extends Prototype {

    private final Array<Monster> monsters = new Array<>();
    private final Array<BuildSpot> buildSpots = new Array<>();

    private PrototypeFactory<MonsterType, Monster> prototypeFactory = new PrototypeFactory<>();

    private final PathGraph pathGraph;
    private final Array<Axis> spawnPoints = new Array<>();

    private final EconomyManager economyManager;
    private final Scenario scenario;

    private boolean isPaused = false;
    private int life;

    private final int cols;
    private final int rows;

    private LevelListener levelListener;

    private final Array<ShotRecord> recentShots = new Array<>();

    private final Array<LifeListener> lifeListeners = new Array<>();

    private final Background background;

    public GameArea(Level level) {
        this.economyManager = new EconomyManager(level.getStartingGold());
        this.life = level.getStartingLife();

        this.pathGraph = level.getPathGraph();
        this.spawnPoints.addAll(pathGraph.getSpawns());
        this.scenario = level.getScenario();

        this.cols = level.getCols();
        this.rows = level.getRows();

        background = level.getBackground();

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
        this.pathGraph = gameArea.pathGraph;
        this.spawnPoints.addAll(gameArea.spawnPoints);
        this.scenario = gameArea.scenario.clone();
        this.levelListener = gameArea.levelListener;

        for (BuildSpot spot : gameArea.buildSpots) {
            this.buildSpots.add(spot.clone());
        }

        for (Monster m : gameArea.monsters) {
            this.monsters.add(m.clone());
        }

        this.background = gameArea.background;

        this.recentShots.addAll(gameArea.recentShots);
    }

    public void update(float delta) {
        if (isPaused || cols == 0) return;

        // Updated: ask scenario to add monsters based on the current time
        if (scenario != null) {
            scenario.update(delta, this);
        }

        for (int i = monsters.size - 1; i >= 0; i--) {
            Monster monster = monsters.get(i);

            monster.update(delta);

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
            if (!recentShots.get(i).isAlive()) {
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

    public PathGraph getPathGraph() {
        return pathGraph;
    }

    public void addLifeListener(LifeListener listener) {
        lifeListeners.add(listener);
    }

    private void notifyLifeChanged() {
        for (LifeListener listener : lifeListeners) {
            listener.onLifeChanged(life);
        }
    }

    public void spawnMonster(Monster monster) {
        if (monster == null) {
            Gdx.app.error("GameArea", "❌ Tentative de spawn d'un monstre null");
            return;
        }

        // Choose a random spawn point from the available spawn points
        if (spawnPoints.size == 0) {
            Gdx.app.error("GameArea", "❌ Aucune position de spawn disponible pour le monstre");
            return;
        }

        Axis spawnPoint = spawnPoints.random();

        monster.setLogicalPos(spawnPoint.getPosition().cpy());

        // Initialize the monster's path
        monster.setPath(pathGraph.getPathPointsStartingFrom(spawnPoint));

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

    public void setPaused(boolean paused) {
        this.isPaused = paused;
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
        notifyLifeChanged(); // Notify life listeners on every change
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

    public Background getBackground() {
        return background;
    }
}
