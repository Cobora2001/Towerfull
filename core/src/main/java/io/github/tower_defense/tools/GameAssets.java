package io.github.tower_defense.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import io.github.tower_defense.entities.defenses.Tower;
import io.github.tower_defense.entities.ennemies.Monster;
import io.github.tower_defense.entities.ennemies.Scenario;
import io.github.tower_defense.entities.ennemies.WaveSchedule;
import io.github.tower_defense.enumElements.*;
import io.github.tower_defense.gameBoard.level.Axis;
import io.github.tower_defense.gameBoard.level.Background;
import io.github.tower_defense.gameBoard.level.Level;
import io.github.tower_defense.gameBoard.level.PathGraph;
import io.github.tower_defense.gameBoard.level.generators.PathGenerator;
import io.github.tower_defense.gameBoard.level.generators.TowerPlacementGenerator;
import io.github.tower_defense.tools.loader.MonsterPrototypeLoader;
import io.github.tower_defense.tools.loader.ScenarioPrototypeLoader;
import io.github.tower_defense.tools.loader.TowerPrototypeLoader;
import io.github.tower_defense.tools.loader.WavePrototypeLoader;
import io.github.tower_defense.entities.*;
import io.github.tower_defense.tools.data.*;

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
                pathGraph = buildPathGraph(data.pathNodes);
            } else if (data.path != null && !data.path.isEmpty()) {
                pathGraph = buildLinearPathGraph(data.path);
            } else {
                pathGraph = generateDefaultPathGraph(data.cols, data.rows);
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
                    data.cols, data.rows, flattenPathGraph(pathGraph));
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

    private ObjectMap<String, Axis> buildPathGraph(ObjectMap<String, LevelData.PathNode> pathNodes) {
        ObjectMap<String, Axis> graph = new ObjectMap<>();

        // First pass: create Axis nodes without connections
        for (ObjectMap.Entry<String, LevelData.PathNode> entry : pathNodes.entries()) {
            String nodeId = entry.key;
            float x = entry.value.pos[0];
            float y = entry.value.pos[1];
            graph.put(nodeId, new Axis(nodeId, new Vector2(x, y)));
        }

        // Second pass: add connections
        for (ObjectMap.Entry<String, LevelData.PathNode> entry : pathNodes.entries()) {
            Axis axis = graph.get(entry.key);
            for (String nextId : entry.value.next) {
                Axis nextAxis = graph.get(nextId);
                if (nextAxis != null) {
                    axis.addNextAxis(nextAxis);
                } else {
                    Gdx.app.error("GameAssets", "Path node '" + nextId + "' not found for connection from '" + entry.key + "'");
                }
            }
        }
        return graph;
    }

    private ObjectMap<String, Axis> buildLinearPathGraph(List<float[]> path) {
        ObjectMap<String, Axis> graph = new ObjectMap<>();
        Axis prevAxis = null;
        int idx = 0;

        for (float[] point : path) {
            String id = "P" + idx++;
            Axis axis = new Axis(id, new Vector2(point[0], point[1]));
            graph.put(id, axis);
            if (prevAxis != null) {
                prevAxis.addNextAxis(axis);
            }
            prevAxis = axis;
        }

        return graph;
    }

    private ObjectMap<String, Axis> generateDefaultPathGraph(int cols, int rows) {
        // Simple stub for default path graph
        ObjectMap<String, Axis> graph = new ObjectMap<>();
        Axis start = new Axis("Start", new Vector2(0, rows / 2));
        Axis end = new Axis("End", new Vector2(cols - 1, rows / 2));
        start.addNextAxis(end);
        graph.put("Start", start);
        graph.put("End", end);
        return graph;
    }

    private Array<Vector2> flattenPathGraph(ObjectMap<String, Axis> graph) {
        // Flatten graph nodes into ordered path points for things like tower placement generator fallback
        Array<Vector2> pathPoints = new Array<>();
        // Simple BFS or DFS to list all nodes in order (choose BFS for example)
        Array<String> visited = new Array<>();
        Array<Axis> queue = new Array<>();
        Axis start = graph.get("A"); // or first key? You may want to store a designated start node ID in LevelData
        if (start == null && graph.size > 0) start = graph.values().iterator().next();
        if (start == null) return pathPoints;

        queue.add(start);
        while (queue.size > 0) {
            Axis current = queue.removeIndex(0);
            if (visited.contains(current.getId(), false)) continue;
            visited.add(current.getId());
            pathPoints.add(current.getPosition());
            for (Axis next : current.getNextAxes()) {
                if (!visited.contains(next.getId(), false)) {
                    queue.add(next);
                }
            }
        }
        return pathPoints;
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
