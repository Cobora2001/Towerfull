// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: The board of the game, where all the action takes place
// -------------------------------------------------------------------------------------

package io.github.towerfull.gameBoard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.towerfull.gameBoard.level.Node;
import io.github.towerfull.gameBoard.level.Background;
import io.github.towerfull.gameBoard.level.PathGraph;
import io.github.towerfull.listener.LifeListener;
import io.github.towerfull.tools.Prototype;
import io.github.towerfull.entities.defenses.ShotRecord;
import io.github.towerfull.entities.defenses.BuildSpot;
import io.github.towerfull.entities.defenses.Tower;
import io.github.towerfull.entities.ennemies.Monster;
import io.github.towerfull.entities.ennemies.Scenario;
import io.github.towerfull.gameBoard.level.Level;
import io.github.towerfull.listener.LevelListener;

/**
 * Represents the game area where all the action takes place.
 * It manages monsters, build spots, economy, and the game state.
 */
public class GameArea extends Prototype {
    // List of monsters currently in the game area
    private final Array<Monster> monsters = new Array<>();

    // List of build spots where towers can be placed
    private final Array<BuildSpot> buildSpots = new Array<>();

    // The path graph representing the paths monsters can take
    private final PathGraph pathGraph;

    // List of spawn points where monsters can spawn (getting the list is a heavy operation, so we cache it)
    private final Array<Node> spawnPoints = new Array<>();

    // Economy manager to handle the player's gold and economy-related actions
    private final EconomyManager economyManager;

    // The scenario that defines the monster spawning logic and timing
    private final Scenario scenario;

    // Flag to indicate if the game is paused
    private boolean isPaused = false;

    // The player's remaining life
    private int life;

    // The number of columns and rows in the game area
    private final int cols;
    private final int rows;

    // Listener for level events (e.g., game over, level complete)
    private LevelListener levelListener;

    // Recent shots fired by towers, used for visual effects and tracking
    private final Array<ShotRecord> recentShots = new Array<>();

    // Listeners for life changes (e.g., when the player's life changes)
    private final Array<LifeListener> lifeListeners = new Array<>();

    // Background of the game area, used for rendering
    private final Background background;

    /**
     * Constructs a GameArea from a Level object.
     * Initializes the economy, life, path graph, spawn points, scenario, and build spots.
     *
     * @param level The level configuration to initialize the game area.
     */
    public GameArea(Level level) {
        this.economyManager = new EconomyManager(level.getStartingGold());
        this.life = level.getStartingLife();

        this.pathGraph = level.getPathGraph();
        this.spawnPoints.addAll(pathGraph.getSpawns());
        this.scenario = level.getScenario().clone();

        this.cols = level.getCols();
        this.rows = level.getRows();

        background = level.getBackground();

        buildSpots.clear();

        Array<Vector2> places = level.getBuildableTiles();

        for(Vector2 pos : places) {
            buildSpots.add(new BuildSpot(pos));
        }

        Gdx.app.log("GameArea", "✅ Niveau prêt, scénario initialisé.");
    }

    /**
     * Copy constructor to create a new GameArea from an existing one.
     * This is useful for cloning the game state.
     *
     * @param gameArea The GameArea to clone.
     */
    public GameArea(GameArea gameArea) {
        this.cols = gameArea.cols;
        this.rows = gameArea.rows;
        this.isPaused = gameArea.isPaused;
        this.life = gameArea.life;
        this.economyManager = gameArea.economyManager.clone();
        this.pathGraph = gameArea.pathGraph;
        this.spawnPoints.addAll(gameArea.spawnPoints);
        this.scenario = gameArea.scenario.clone();
        this.levelListener = gameArea.levelListener;

        for(BuildSpot spot : gameArea.buildSpots) {
            this.buildSpots.add(spot.clone());
        }

        for(Monster m : gameArea.monsters) {
            this.monsters.add(m.clone());
        }

        this.background = gameArea.background;

        this.recentShots.addAll(gameArea.recentShots);
    }

    /**
     * Updates the game area state, including monsters, towers, and economy.
     * This method should be called every frame to keep the game running.
     *
     * @param delta The time since the last update in seconds.
     */
    public void update(float delta) {
        if(isPaused || cols == 0) return;

        // Updated: ask scenario to add monsters based on the current time
        if(scenario != null) {
            scenario.update(delta, this);
        }

        for(int i = monsters.size - 1; i >= 0; i--) {
            Monster monster = monsters.get(i);

            monster.update(delta);

            if(monster.hasReachedEnd()) {
                monsters.removeIndex(i);
                loseLife(monster.getDamage());
            } else if(monster.isDead()) {
                monsters.removeIndex(i);
                economyManager.earnGold(monster.getReward());
            }
        }

        for(BuildSpot spot : buildSpots) {
            if(spot.isUsed()) {
                Tower tower = spot.getTower();
                if(tower != null) {
                    tower.update(delta, monsters, this, spot.getLogicalPos());
                }
            }
        }

        for(int i = recentShots.size - 1; i >= 0; i--) {
            recentShots.get(i).updateTime(delta);
            if(!recentShots.get(i).isAlive()) {
                recentShots.removeIndex(i);
            }
        }

        if(life <= 0 && levelListener != null) {
            levelListener.onGameOver();
            return;
        }

        if(monsters.size == 0) {
            assert scenario != null;
            if(scenario.isFinished() && levelListener != null) {
                levelListener.onLevelComplete();
            }
        }
    }

    /**
     * Returns the path graph used in this game area.
     *
     * @return The path graph.
     */
    public PathGraph getPathGraph() {
        return pathGraph;
    }

    /**
     * Adds a listener to be notified when the player's life changes.
     *
     * @param listener The listener to add.
     */
    public void addLifeListener(LifeListener listener) {
        lifeListeners.add(listener);
    }

    /**
     * Notifies all registered life listeners about a change in the player's life.
     */
    private void notifyLifeChanged() {
        for(LifeListener listener : lifeListeners) {
            listener.onLifeChanged(life);
        }
    }

    /**
     * Spawns a monster at a random spawn point in the game area.
     * The monster's position is set to the spawn point's position,
     * and its path is initialized based on the path graph.
     *
     * @param monster The monster to spawn.
     */
    public void spawnMonster(Monster monster) {
        if(monster == null) {
            Gdx.app.error("GameArea", "Tentative de spawn d'un monstre null");
            return;
        }

        // Choose a random spawn point from the available spawn points
        if(spawnPoints.size == 0) {
            Gdx.app.error("GameArea", "Aucune position de spawn disponible pour le monstre");
            return;
        }

        Node spawnPoint = spawnPoints.random();

        monster.setLogicalPos(spawnPoint.getPosition().cpy());

        // Initialize the monster's path
        monster.setPath(pathGraph.getPathPointsStartingFrom(spawnPoint));

        monsters.add(monster);
    }

    /**
     * Returns the list of build spots where towers can be placed.
     *
     * @return The array of build spots.
     */
    public Array<BuildSpot> getBuildSpots() {
        return buildSpots;
    }

    /**
     * Returns the list of monsters currently in the game area.
     *
     * @return The array of monsters.
     */
    public Array<Monster> getMonsters() {
        return monsters;
    }

    /**
     * Adds a shot record to the recent shots list.
     * This is used to track shots fired by towers for visual effects and damage tracking.
     *
     * @param from The starting position of the shot.
     * @param to The ending position of the shot.
     * @param damage The damage dealt by the shot.
     */
    public void addShot(Vector2 from, Vector2 to, int damage) {
        recentShots.add(new ShotRecord(from.cpy(), to.cpy(), damage));
    }

    /**
     * Returns the list of recent shots fired by towers.
     * This is used for visual effects and tracking damage dealt.
     *
     * @return The array of recent shot records.
     */
    public Array<ShotRecord> getRecentShots() {
        return recentShots;
    }

    /**
     * Returns the list of built spots where towers have been placed.
     * This is used to track which build spots are currently occupied by towers.
     *
     * @return The array of built spots.
     */
    public Array<BuildSpot> getBuiltSpots() {
        Array<BuildSpot> builtSpots = new Array<>();
        for(BuildSpot spot : buildSpots) {
            if(spot.isUsed()) {
                builtSpots.add(spot);
            }
        }
        return builtSpots;
    }

    /**
     * Pauses the game area, stopping all updates and actions.
     * This is useful for when the player wants to pause the game.
     */
    public void pause() {
        isPaused = true;
    }

    /**
     * Resumes the game area, allowing updates and actions to continue.
     * This is useful for when the player wants to resume the game after pausing.
     */
    public void resume() {
        isPaused = false;
    }

    /**
     * Sets the paused state of the game area.
     * This can be used to pause or resume the game programmatically.
     *
     * @param paused True to pause the game, false to resume it.
     */
    public void setPaused(boolean paused) {
        this.isPaused = paused;
    }

    /**
     * Checks if the game area is currently paused.
     *
     * @return True if the game is paused, false otherwise.
     */
    public boolean isPaused() {
        return isPaused;
    }

    /**
     * Returns the number of columns in the game area.
     *
     * @return The number of columns.
     */
    public int getCols() {
        return cols;
    }

    /**
     * Returns the number of rows in the game area.
     *
     * @return The number of rows.
     */
    public int getRows() {
        return rows;
    }

    /**
     * Returns the current life of the player.
     * This is used to track how many lives the player has left.
     *
     * @return The player's current life.
     */
    public int getLife() {
        return life;
    }

    /**
     * Reduces the player's life by a specified amount.
     * If the life reaches zero, it triggers a game over event.
     *
     * @param amount The amount of life to lose.
     */
    public void loseLife(int amount) {
        life -= amount;
        if(life <= 0) {
            life = 0;
            System.out.println("Game Over!");
            if(levelListener != null) {
                levelListener.onGameOver();
            }
        }
        notifyLifeChanged(); // Notify life listeners on every change
    }

    /**
     * Returns the economy manager for this game area.
     * The economy manager handles the player's gold and economy-related actions.
     *
     * @return The economy manager.
     */
    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    /**
     * Sets the level listener for this game area.
     * The level listener is notified of events such as game over or level completion.
     *
     * @param listener The level listener to set.
     */
    public void setLevelListener(LevelListener listener) {
        this.levelListener = listener;
    }

    /**
     * Creates a clone of this GameArea.
     * This is useful for saving the game state or creating a new instance with the same properties.
     *
     * @return A new GameArea instance that is a clone of this one.
     */
    public GameArea clone() {
        return new GameArea(this);
    }

    /**
     * Returns the background of the game area.
     * The background is used for rendering and visual effects.
     *
     * @return The background of the game area.
     */
    public Background getBackground() {
        return background;
    }
}
