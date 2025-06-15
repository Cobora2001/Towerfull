// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: The class responsible for loading and managing all game assets, including monsters,
//      towers, waves, scenarios, levels, appearances, backgrounds, skin, and music.
// -------------------------------------------------------------------------------------

package io.github.towerfull.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import io.github.towerfull.entities.defenses.Tower;
import io.github.towerfull.entities.ennemies.Monster;
import io.github.towerfull.entities.ennemies.Scenario;
import io.github.towerfull.entities.ennemies.WaveSchedule;
import io.github.towerfull.enumElements.*;
import io.github.towerfull.gameBoard.level.Node;
import io.github.towerfull.gameBoard.level.Background;
import io.github.towerfull.gameBoard.level.Level;
import io.github.towerfull.gameBoard.level.PathGraph;
import io.github.towerfull.gameBoard.level.generators.TowerPlacementGenerator;
import io.github.towerfull.tools.loader.MonsterPrototypeLoader;
import io.github.towerfull.tools.loader.ScenarioPrototypeLoader;
import io.github.towerfull.tools.loader.TowerPrototypeLoader;
import io.github.towerfull.tools.loader.WavePrototypeLoader;
import io.github.towerfull.entities.*;
import io.github.towerfull.tools.data.*;

import java.util.HashMap;
import java.util.Map;

/**
 * GameAssets is a singleton class responsible for loading and managing all game assets.
 * It includes factories for monsters, towers, waves, and scenarios, as well as maps for levels,
 * appearances, and backgrounds. It also handles the game's skin and background music.
 */
public class GameAssets {
    // Singleton instance
    private static final GameAssets instance = new GameAssets();

    // Factories for creating game entities
    public final PrototypeFactory<MonsterType, Monster> monsterFactory = new PrototypeFactory<>();
    public final PrototypeFactory<TowerType, Tower> towerFactory = new PrototypeFactory<>();
    public final PrototypeFactory<WaveId, WaveSchedule> waveFactory = new PrototypeFactory<>();
    public final PrototypeFactory<ScenarioId, Scenario> scenarioFactory = new PrototypeFactory<>();

    // Maps to hold various game assets
    public final Map<LevelId, Level> levels = new HashMap<>();
    public final Map<AppearanceId, Appearance> appearances = new HashMap<>();
    public final Map<BackgroundId, Background> backgrounds = new HashMap<>();

    // Skin for the game's UI
    public Skin skin;

    // Background music for the game
    private Music bgMusic;

    // Flag to indicate if the music is muted
    private boolean muted = false;

    /**
     * Private constructor to prevent instantiation.
     * Use GameAssets.get() to access the singleton instance.
     */
    private GameAssets() {}

    /**
     * Returns the singleton instance of GameAssets.
     *
     * @return the GameAssets instance
     */
    public static GameAssets get() { return instance; }

    /**
     * Loads all game assets including appearances, towers, monsters, waves, scenarios,
     * backgrounds, levels, music, and skin.
     */
    public void loadAll() {
        Json json = new Json();

        loadAppearances(json);
        loadTowers();
        loadMonsters();
        loadWaves();
        loadScenarios();
        loadBackgrounds(json);
        loadLevels(json);
        loadMusic();
        loadSkin();
    }

    /**
     * Loads the skin for the game's UI.
     * The skin is defined in a JSON file and includes styles for various UI components.
     */
    private void loadSkin() {
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        if(skin == null) {
            Gdx.app.error("GameAssets", "Failed to load skin");
        } else {
            Gdx.app.log("GameAssets", "Skin loaded successfully");
        }
    }

    /**
     * Loads appearances from a JSON file and stores them in the appearances map.
     * Each appearance is associated with an AppearanceId.
     *
     * @param json the Json instance used for deserialization
     */
    private void loadAppearances(Json json) {
        ObjectMap<String, AppearanceData> appearanceMap = json.fromJson(ObjectMap.class, AppearanceData.class,
            Gdx.files.internal("appearances.json"));
        for(ObjectMap.Entry<String, AppearanceData> entry : appearanceMap.entries()) {
            AppearanceId id = AppearanceId.valueOf(entry.key);
            Texture texture = new Texture(Gdx.files.internal(entry.value.appearance));
            appearances.put(id, new Appearance(texture, entry.value.width, entry.value.height));
        }
    }

    /**
     * Loads tower prototypes from a JSON file and registers them in the towerFactory.
     * Each tower is associated with a TowerType and TowerData.
     */
    private void loadTowers() {
        TowerPrototypeLoader towerLoader = new TowerPrototypeLoader();
        towerLoader.load("towers/towers.json", TowerType.class, TowerData.class, towerFactory);
    }

    /**
     * Loads monster prototypes from a JSON file and registers them in the monsterFactory.
     * Each monster is associated with a MonsterType and MonsterData.
     */
    private void loadMonsters() {
        MonsterPrototypeLoader monsterLoader = new MonsterPrototypeLoader();
        monsterLoader.load("monsters/monsters.json", MonsterType.class, MonsterData.class, monsterFactory);
    }

    /**
     * Loads wave prototypes from a JSON file and registers them in the waveFactory.
     * Each wave is associated with a WaveId and WaveData.
     */
    private void loadWaves() {
        WavePrototypeLoader waveLoader = new WavePrototypeLoader();
        waveLoader.load("waves.json", WaveId.class, WaveData.class, waveFactory);
    }

    /**
     * Loads scenario prototypes from a JSON file and registers them in the scenarioFactory.
     * Each scenario is associated with a ScenarioId and ScenarioData.
     */
    private void loadScenarios() {
        ScenarioPrototypeLoader scenarioLoader = new ScenarioPrototypeLoader(waveFactory, monsterFactory);
        scenarioLoader.load("scenarios.json", ScenarioId.class, ScenarioData.class, scenarioFactory);
    }

    /**
     * Loads backgrounds from a JSON file and stores them in the backgrounds map.
     * Each background is associated with a BackgroundId and includes various appearances.
     *
     * @param json the Json instance used for deserialization
     */
    private void loadBackgrounds(Json json) {
        ObjectMap<String, BackgroundData> backgroundMap = json.fromJson(ObjectMap.class, BackgroundData.class,
            Gdx.files.internal("backgrounds.json"));

        for(ObjectMap.Entry<String, BackgroundData> entry : backgroundMap.entries()) {
            Appearance backgroundAppearance = appearances.get(entry.value.backgroundAppearance);
            Appearance pathAppearance = appearances.get(entry.value.pathAppearance);
            Appearance pathStartAppearance = appearances.get(entry.value.pathStartAppearance);
            Appearance pathEndAppearance = appearances.get(entry.value.pathEndAppearance);

            if(backgroundAppearance == null || pathAppearance == null || pathStartAppearance == null || pathEndAppearance == null) {
                Gdx.app.error("GameAssets", "Missing appearance(s) for background: " + entry.key);
                continue;
            }

            BackgroundId backgroundId = BackgroundId.valueOf(entry.key);
            Background background = new Background(
                backgroundAppearance,
                pathAppearance,
                pathStartAppearance,
                pathEndAppearance
            );

            backgrounds.put(backgroundId, background);
        }
    }

    /**
     * Loads levels from a JSON file and constructs Level objects.
     * Each level is associated with a LevelId and includes path graphs, scenarios, buildable tiles, and other properties.
     *
     * @param json the Json instance used for deserialization
     */
    private void loadLevels(Json json) {
        ObjectMap<String, LevelData> levelMap = json.fromJson(ObjectMap.class, LevelData.class,
            Gdx.files.internal("levels.json"));

        for(ObjectMap.Entry<String, LevelData> entry : levelMap.entries()) {
            LevelId id = LevelId.valueOf(entry.key);
            LevelData data = entry.value;

            ObjectMap<String, Node> pathGraph;
            if(data.pathNodes != null && !data.pathNodes.isEmpty()) {
                pathGraph = GraphUtilities.buildPathGraph(data.pathNodes);
            } else if(data.path != null && !data.path.isEmpty()) {
                pathGraph = GraphUtilities.buildLinearPathGraph(data.path);
            } else {
                pathGraph = GraphUtilities.generateDefaultPathGraph(data.cols, data.rows);
            }

            Scenario scenario = (data.scenario != null)
                ? scenarioFactory.create(data.scenario)
                : null;

            Array<Vector2> buildableTiles = new Array<>();
            if(data.buildableTiles != null && !data.buildableTiles.isEmpty()) {
                for(float[] tile : data.buildableTiles) {
                    buildableTiles.add(new Vector2(tile[0], tile[1]));
                }
            } else {
                buildableTiles = TowerPlacementGenerator.generate(
                    data.cols, data.rows, GraphUtilities.flattenPathGraph(pathGraph));
            }

            Background background = backgrounds.get(data.background);

            PathGraph path = new PathGraph(pathGraph);

            Level level = new Level(
                data.cols, data.rows, path, scenario,
                buildableTiles, data.startingGold, data.startingLife, background
            );

            levels.put(id, level);
        }
    }

    /**
     * Loads background music from a file and starts playing it in a loop.
     * The music is set to loop indefinitely and can be toggled on or off.
     */
    private void loadMusic() {
        bgMusic = Gdx.audio.newMusic(Gdx.files.internal("music/Age_of_war_theme.mp3"));
        bgMusic.setLooping(true);
        bgMusic.play();
    }

    /**
     * Is called to dispose of all game assets.
     */
    public void dispose() {
        for(Appearance appearance : appearances.values()) {
            appearance.getTexture().dispose();
        }
        appearances.clear();
        levels.clear();
        backgrounds.clear();

        towerFactory.clear();
        monsterFactory.clear();
        waveFactory.clear();
        scenarioFactory.clear();

        if(bgMusic != null) {
            bgMusic.stop();
            bgMusic.dispose();
        }

        if(skin != null) {
            skin.dispose();
        }
    }

    /**
     * Toggles the background music on or off.
     * If the music is currently playing, it will be muted; otherwise, it will be unmuted.
     */
    public void toggleMusic() {
        if(bgMusic == null) return;

        muted = !muted;
        bgMusic.setVolume(muted ? 0f : 1f);
    }

    /**
     * Checks if the background music is currently muted.
     *
     * @return true if the music is muted, false otherwise
     */
    public boolean isMusicMuted() {
        return muted;
    }
}
