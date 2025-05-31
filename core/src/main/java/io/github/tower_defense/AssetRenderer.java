package io.github.tower_defense;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.tower_defense.Prototype.*;

public class MonsterRenderer {

    private static final MonsterRenderer instance = new MonsterRenderer();

    private MonsterRenderer() {
        // No batch or renderer needed here anymore
    }

    public static MonsterRenderer getInstance() {
        return instance;
    }

    public void renderMonster(Array<Monster> monsters, ShapeRenderer shapeRenderer, GameArea gameArea) {
        for (Monster monster : monsters) {
            MonsterAppearance appearance = monster.getAppearance();
            Vector2 logicalPos = monster.getLogicalPos();
            Vector2 pixelCenter = gameArea.logicalToPixel(logicalPos); // pixelCenter is at center of cell

            if (appearance instanceof ShapeAppearance) {
                ShapeAppearance shapeAppearance = (ShapeAppearance) appearance;

                shapeRenderer.setColor(shapeAppearance.shapeColor);

                switch (shapeAppearance.shapeType) {
                    case 0: // Circle
                        shapeRenderer.circle(
                            pixelCenter.x,
                            pixelCenter.y,
                            Math.min(shapeAppearance.width, shapeAppearance.height) / 2f
                        );
                        break;

                    case 1: // Rectangle (centered)
                        shapeRenderer.rect(
                            pixelCenter.x - shapeAppearance.width / 2f,
                            pixelCenter.y - shapeAppearance.height / 2f,
                            shapeAppearance.width,
                            shapeAppearance.height
                        );
                        break;

                    default: // Fallback to rectangle
                        shapeRenderer.rect(
                            pixelCenter.x - shapeAppearance.width / 2f,
                            pixelCenter.y - shapeAppearance.height / 2f,
                            shapeAppearance.width,
                            shapeAppearance.height
                        );
                        break;
                }

            } else if (appearance instanceof TextureAppearance) {
                System.out.println("⚠ Skipping textured monsters in ShapeRenderer pass.");
            }
        }
    }

}
