package io.github.tower_defense.service;

import com.badlogic.gdx.Gdx;
import io.github.tower_defense.Loader.GameTextureAssets;
import io.github.tower_defense.Loader.JsonLoader;
import io.github.tower_defense.Prototype.*;

public class AssetLoaderService {

    private final PrototypeFactory<MonsterType, Monster> monsterFactory;
    private final PrototypeFactory<TowerType, Tower> towerFactory;

    public AssetLoaderService() {
        this.monsterFactory = new PrototypeFactory<>();
        this.towerFactory = new PrototypeFactory<>();
    }

    public void loadAllAssets() {
        Gdx.app.log("AssetLoader", "🔄 Début du chargement des assets...");

        // Charge les textures (.png) via libGDX AssetManager
        GameTextureAssets.load();
        GameTextureAssets.finishLoading(); // important : attendre le chargement complet

        // Apparences (textures + tailles) à partir d’un fichier JSON
        JsonLoader.get().loadAppearancePrototypes("appearances.json");

        // Prototypes de monstres et tours
        JsonLoader.get().loadMonsterPrototypes("monsters/monsters.json", monsterFactory);
        JsonLoader.get().loadTowerPrototypes("towers/towers.json", towerFactory);

        Gdx.app.log("AssetLoader", "✅ Tous les assets ont été chargés.");
    }

    public PrototypeFactory<MonsterType, Monster> getMonsterFactory() {
        return monsterFactory;
    }

    public PrototypeFactory<TowerType, Tower> getTowerFactory() {
        return towerFactory;
    }
}
