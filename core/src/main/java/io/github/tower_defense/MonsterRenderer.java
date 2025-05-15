package io.github.tower_defense;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.tower_defense.Prototype.Monster;

import java.util.List;

public class MonsterRenderer {

    private static final MonsterRenderer instance = new MonsterRenderer();
    private final ShapeRenderer shapeRenderer;

    private MonsterRenderer() {
        shapeRenderer = new ShapeRenderer();
    }

    public static MonsterRenderer getInstance() {
        return instance;
    }

    public ShapeRenderer getShapeRenderer() {return shapeRenderer;}

    public void renderMonster(List<Monster> monsters) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 0, 0, 1); // Red

        for (Monster monster : monsters) {
            shapeRenderer.circle(monster.getLogicalPos().x, monster.getLogicalPos().y, 10);
        }

        shapeRenderer.end();
    }
}
