package io.github.towerfull.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import io.github.towerfull.entities.defenses.Tower;
import io.github.towerfull.entities.ennemies.Monster;
import io.github.towerfull.entities.ennemies.Scenario;
import io.github.towerfull.entities.ennemies.WaveSchedule;
import io.github.towerfull.enumElements.*;
import io.github.towerfull.gameBoard.level.Axis;
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
import java.util.List;
import java.util.Map;

public class GameAssets {
    private static final GameAssets instance = new GameAssets();

    public final PrototypeFactory<MonsterType, Monster> monsterFactory = new PrototypeFactory<>();
    public final PrototypeFactory<TowerType, Tower> towerFactory = new PrototypeFactory<>();
    public final PrototypeFactory<WaveId, WaveSchedule> waveFactory = new PrototypeFactory<>();
    public final PrototypeFactory<ScenarioId, Scenario> scenarioFactory = new PrototypeFactory<>();

    public final Map<LevelId, Level> levels = new HashMap<>();
    public final Map<AppearanceId, Appearance> appearances = new HashMap<>();
    public final Map<BackgroundId, Background> backgrounds = new HashMap<>();

    private Music bgMusic;
    private boolean muted = false;

    private GameAssets() {}
    public static GameAssets get() { return instance; }

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
    }

    private void loadAppearances(Json json) {
        ObjectMap<String, AppearanceData> appearanceMap = json.fromJson(ObjectMap.class, AppearanceData.class,
            Gdx.files.internal("appearances.json"));
        for (ObjectMap.Entry<String, AppearanceData> entry : appearanceMap.entries()) {
            AppearanceId id = AppearanceId.valueOf(entry.key);
            Texture texture = new Texture(Gdx.files.internal(entry.value.appearance));
            appearances.put(id, new Appearance(texture, entry.value.width, entry.value.height));
        }
    }

    private void loadTowers() {
        TowerPrototypeLoader towerLoader = new TowerPrototypeLoader();
        towerLoader.load("towers/towers.json", TowerType.class, TowerData.class, towerFactory);
    }

    private void loadMonsters() {
        MonsterPrototypeLoader monsterLoader = new MonsterPrototypeLoader();
        monsterLoader.load("monsters/monsters.json", MonsterType.class, MonsterData.class, monsterFactory);
    }

    private void loadWaves() {
        WavePrototypeLoader waveLoader = new WavePrototypeLoader();
        waveLoader.load("waves.json", WaveId.class, WaveData.class, waveFactory);
    }

    private void loadScenarios() {
        ScenarioPrototypeLoader scenarioLoader = new ScenarioPrototypeLoader(waveFactory, monsterFactory);
        scenarioLoader.load("scenarios.json", ScenarioId.class, ScenarioData.class, scenarioFactory);
    }

    private void loadBackgrounds(Json json) {
        ObjectMap<String, BackgroundData> backgroundMap = json.fromJson(ObjectMap.class, BackgroundData.class,
            Gdx.files.internal("backgrounds.json"));

        for (ObjectMap.Entry<String, BackgroundData> entry : backgroundMap.entries()) {
            Appearance backgroundAppearance = appearances.get(entry.value.backgroundAppearance);
            Appearance pathAppearance = appearances.get(entry.value.pathAppearance);
            Appearance pathStartAppearance = appearances.get(entry.value.pathStartAppearance);
            Appearance pathEndAppearance = appearances.get(entry.value.pathEndAppearance);

            if (backgroundAppearance == null || pathAppearance == null || pathStartAppearance == null || pathEndAppearance == null) {
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

    private void loadLevels(Json json) {
        ObjectMap<String, LevelData> levelMap = json.fromJson(ObjectMap.class, LevelData.class,
            Gdx.files.internal("levels.json"));

        for (ObjectMap.Entry<String, LevelData> entry : levelMap.entries()) {
            LevelId id = LevelId.valueOf(entry.key);
            LevelData data = entry.value;

            ObjectMap<String, Axis> pathGraph;
            if (data.pathNodes != null && !data.pathNodes.isEmpty()) {
                pathGraph = GraphUtilities.buildPathGraph(data.pathNodes);
            } else if (data.path != null && !data.path.isEmpty()) {
                pathGraph = GraphUtilities.buildLinearPathGraph(data.path);
            } else {
                pathGraph = GraphUtilities.generateDefaultPathGraph(data.cols, data.rows);
            }

            Scenario scenario = (data.scenario != null)
                ? scenarioFactory.create(data.scenario)
                : null;

            Array<Vector2> buildableTiles = new Array<>();
            if (data.buildableTiles != null && !data.buildableTiles.isEmpty()) {
                for (float[] tile : data.buildableTiles) {
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

    private void loadMusic() {
        bgMusic = Gdx.audio.newMusic(Gdx.files.internal("music/Age_of_war_theme.mp3"));
        bgMusic.setLooping(true);
        bgMusic.play();
    }

    public void dispose() {
        for (Appearance appearance : appearances.values()) {
            appearance.getTexture().dispose();
        }
        appearances.clear();
        levels.clear();
        backgrounds.clear();

        towerFactory.clear();
        monsterFactory.clear();
        waveFactory.clear();
        scenarioFactory.clear();

        if (bgMusic != null) {
            bgMusic.stop();
            bgMusic.dispose();
        }
    }

    public void toggleMusic() {
        if (bgMusic == null) return;

        muted = !muted;
        bgMusic.setVolume(muted ? 0f : 1f);
    }

    public boolean isMusicMuted() {
        return muted;
    }
}
