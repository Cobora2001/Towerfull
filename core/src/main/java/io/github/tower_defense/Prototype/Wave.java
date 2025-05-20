package io.github.tower_defense.Prototype;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Wave extends Prototype {
    private final List<WaveEntry> entries;
    private final PrototypeFactory<MonsterType, Monster> factory;
    private final Array<Monster> activeMonsters; // Liste des monstres actifs dans le jeu


    private Vector2 spawnPoint;
    private float elapsedTime = 0f;
    private int currentIndex = 0;
    private boolean finished = false;

    /**
     * Constructeur d'une vague.
     * @param entries liste ordonnée des monstres à faire apparaître
     * @param factory fabrique de prototypes de monstres
     * @param activeMonsters liste partagée de monstres actifs (gérée dans GameArea ou Scenario)
     */
    public Wave(List<WaveEntry> entries,
                PrototypeFactory<MonsterType, Monster> factory,
                Array<Monster> activeMonsters,
                Vector2 spawnPoint) {
        this.entries = entries;
        this.factory = factory;
        this.activeMonsters = activeMonsters;
        this.spawnPoint = spawnPoint;
    }

    /**
     * Mise à jour de la vague : fait apparaître les monstres au bon moment.
     * @param deltaTime temps écoulé depuis le dernier appel (Gdx.graphics.getDeltaTime())
     */
    public void update(float deltaTime) {
        if (finished) return;

        elapsedTime += deltaTime;

        while (currentIndex < entries.size()
                && entries.get(currentIndex).getSpawnTime() <= elapsedTime) {

            WaveEntry entry = entries.get(currentIndex);
            Monster m = factory.create(entry.getType());
            m.setLogicalPos(spawnPoint);
            activeMonsters.add(m);

            System.out.println("👾 Spawned: " + entry.getType() + " at " + elapsedTime);

            currentIndex++;
        }

        if (currentIndex >= entries.size()) {
            finished = true;
        }
    }


    /**
     * Indique si tous les monstres de la vague ont été générés.
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Réinitialise la vague (utile en cas de redémarrage).
     */
    public void reset() {
        elapsedTime = 0f;
        currentIndex = 0;
        finished = false;
    }


    @Override
    public Wave clone() {
        return new Wave(
                new ArrayList<>(entries), // ou copie plus profonde si nécessaire
                factory,
                activeMonsters,// même référence (attention si besoin d’isoler)
                spawnPoint
        );
    }
}
