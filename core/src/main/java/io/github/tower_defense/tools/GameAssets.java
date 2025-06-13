package io.github.tower_defense.tools;

import com.badlogic.gdx.Gdx;
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
import io.github.tower_defense.gameBoard.level.Level;
import io.github.tower_defense.gameBoard.level.generators.PathGenerator;
import io.github.tower_defense.gameBoard.level.generators.TowerPlacementGenerator;
import io.github.tower_defense.tools.loader.MonsterPrototypeLoader;
import io.github.tower_defense.tools.loader.ScenarioPrototypeLoader;
import io.github.tower_defense.tools.loader.TowerPrototypeLoader;
import io.github.tower_defense.tools.loader.WavePrototypeLoader;
import io.github.tower_defense.entities.*;
import io.github.tower_defense.tools.data.*;

import java.util.HashMap;
import java.util.Map;

public class GameAssets {
    private static final GameAssets instance = new GameAssets();

    public final PrototypeFactory<MonsterType, Monster> monsterFactory = new PrototypeFactory<>();
    public final PrototypeFactory<TowerType, Tower> towerFactory = new PrototypeFactory<>();
    public final PrototypeFactory<WaveId, WaveSchedule> waveFactory = new PrototypeFactory<>();
    public final PrototypeFactory<ScenarioId, Scenario> scenarioFactory = new PrototypeFactory<>();

    public final Map<LevelId, Level> levels = new HashMap<>();
    public final Map<AppearanceId, Appearance> appearances = new HashMap<>();

    private GameAssets() {}
    public static GameAssets get() { return instance; }

    public void loadAll() {
        Json json = new Json();

        // Load appearances
        ObjectMap<String, AppearanceData> appearanceMap = json.fromJson(ObjectMap.class, AppearanceData.class,
            Gdx.files.internal("appearances.json"));
        for (ObjectMap.Entry<String, AppearanceData> entry : appearanceMap.entries()) {
            AppearanceId id = AppearanceId.valueOf(entry.key);
            Texture texture = new Texture(Gdx.files.internal(entry.value.appearance));
            appearances.put(id, new Appearance(texture, entry.value.width, entry.value.height));
        }

        // Load towers using the loader
        TowerPrototypeLoader towerLoader = new TowerPrototypeLoader();
        towerLoader.load("towers/towers.json", TowerType.class, TowerData.class, towerFactory);

        // Load monsters using the loader
        MonsterPrototypeLoader monsterLoader = new MonsterPrototypeLoader();
        monsterLoader.load("monsters/monsters.json", MonsterType.class, MonsterData.class, monsterFactory);

        // Load waves using the loader
        WavePrototypeLoader waveLoader = new WavePrototypeLoader();
        waveLoader.load("waves.json", WaveId.class, WaveData.class, waveFactory);

        // Load scenarios
        ScenarioPrototypeLoader scenarioLoader =
            new ScenarioPrototypeLoader(waveFactory, monsterFactory);
        scenarioLoader.load("scenarios.json", ScenarioId.class, ScenarioData.class, scenarioFactory);

        // Load levels
        ObjectMap<String, LevelData> levelMap = json.fromJson(ObjectMap.class, LevelData.class,
            Gdx.files.internal("levels.json"));

        for (ObjectMap.Entry<String, LevelData> entry : levelMap.entries()) {
            LevelId id = LevelId.valueOf(entry.key);
            LevelData data = entry.value;

            Array<Vector2> path;
            if (data.survival || data.path == null || data.path.isEmpty()) {
                path = PathGenerator.generatePath(data.cols, data.rows);
            } else {
                path = new Array<>();
                for (float[] point : data.path) {
                    path.add(new Vector2(point[0], point[1]));
                }
            }

            Scenario scenario = null;
            if (data.scenario != null) {
                scenario = scenarioFactory.create(data.scenario);
            }

            Array<Vector2> buildableTiles = new Array<>();
            if (data.buildableTiles != null && !data.buildableTiles.isEmpty()) {
                for (float[] tile : data.buildableTiles) {
                    buildableTiles.add(new Vector2(tile[0], tile[1]));
                }
            } else {
                buildableTiles = TowerPlacementGenerator.generate(data.cols, data.rows, path);
            }

            Level level = new Level(data.cols, data.rows, path, scenario,
                                    buildableTiles, data.startingGold, data.startingLife);

            if (data.survival) {
                level.setSurvival();
            }

            levels.put(id, level);
        }
    }

    public void dispose() {
        for (Appearance appearance : appearances.values()) {
            appearance.getTexture().dispose();
        }
        appearances.clear();

        towerFactory.clear();
        monsterFactory.clear();
        waveFactory.clear();
        scenarioFactory.clear();
    }
}
