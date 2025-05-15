package io.github.tower_defense.Prototype;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.tower_defense.Level.Level;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class Wave extends Prototype {

    private final Map<String, Integer> monsterCounts;
    private Iterator<Map.Entry<String, Integer>> iterator;
    private String currentType;
    private int remainingOfCurrent;

    private final float interval = 1.5f;
    private float lastSpawn = 0f;

    private final PrototypeFactory<Monster> factory;

    public Wave(PrototypeFactory<Monster> factory) {
        this.monsterCounts = new LinkedHashMap<>();
        this.factory = factory;
    }

    public Wave(Wave other) {
        this.monsterCounts = new LinkedHashMap<>(other.monsterCounts);
        this.factory = other.factory;
        this.lastSpawn = 0f; // reset pour nouvelle vague
    }

    @Override
    public Wave clone() {
        return new Wave(this);
    }

    public void addMonster(String type, int count) {
        monsterCounts.put(type, monsterCounts.getOrDefault(type, 0) + count);
    }

    public void update(float delta, GameArea area, Level level) {
        if (iterator == null) {
            initIterator(); // ✅ D'abord initialiser
        }

        if (isFinished()) return; // ✅ Ensuite vérifier

        lastSpawn += delta;

        if (lastSpawn >= interval && currentType != null) {
            lastSpawn = 0f;

            Monster m = factory.create(currentType);
            if (m != null) {
                m.setLogicalPos(level.getPathPoints().first().cpy());
                area.addMonster(m);
            } else {
                System.out.println("❌ Prototype not found for type: " + currentType);
                nextType();
                return;
            }

            remainingOfCurrent--;
            if (remainingOfCurrent <= 0) {
                nextType();
            }
        }
    }

    private void initIterator() {
        this.iterator = monsterCounts.entrySet().iterator();
        nextType();
    }

    private void nextType() {
        if (iterator != null && iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            currentType = entry.getKey();
            remainingOfCurrent = entry.getValue();
        } else {
            currentType = null;
        }
    }

    public boolean isFinished() {
        return currentType == null && (iterator == null || !iterator.hasNext());
    }

    public Map<String, Integer> getMonsterCounts() {
        return monsterCounts;
    }
}
