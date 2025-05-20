package io.github.tower_defense.Level;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class LevelGenerator {
    public static Array<Vector2> generatePath(int cols, int rows) {
        Array<Vector2> path = new Array<>();

        int x = 0;
        int y = 1; // start bas gauche
        path.add(new Vector2(x, y));

        int maxHeight = rows - 2;
        int minHeight = 1;

        while (x < cols - 1) {
            // ➤ Phase horizontale (2 à 4 cases)
            int horSteps = MathUtils.random(2, 4);
            for (int i = 0; i < horSteps && x < cols - 1; i++) {
                x++;
                path.add(new Vector2(x, y));
            }

            // ➤ Phase verticale (montée ou descente 2 à 3 cases)
            int vertDir = MathUtils.randomBoolean() ? 1 : -1; // 1 = up, -1 = down
            int vertSteps = MathUtils.random(2, 3);

            for (int i = 0; i < vertSteps; i++) {
                int newY = y + vertDir;
                if (newY >= minHeight && newY <= maxHeight) {
                    y = newY;
                    path.add(new Vector2(x, y));
                } else {
                    break; // arrête si on sort des limites
                }
            }
        }

        // termine à la hauteur maximale si pas atteint
        while (y < maxHeight && x < cols - 1) {
            y++;
            path.add(new Vector2(x, y));
        }

        return path;
    }
}
