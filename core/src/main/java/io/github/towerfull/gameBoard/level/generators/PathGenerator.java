package io.github.towerfull.gameBoard.level.generators;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class PathGenerator {
    public static Array<Vector2> generatePath(int cols, int rows) {
        Array<Vector2> path = new Array<>();

        int x = 0;
        int y = MathUtils.random(1, rows - 2); // start somewhere vertically in the left column
        path.add(new Vector2(x, y));

        while (x < cols - 1) {
            // ➤ Move horizontally
            int maxHorSteps = Math.min(4, cols - x - 1); // stay in bounds

            int horSteps;
            if (maxHorSteps >= 2) {
                horSteps = MathUtils.random(2, maxHorSteps);
            } else if (maxHorSteps >= 1) {
                horSteps = 1;
            } else {
                break; // no room to move
            }

            x += horSteps;
            path.add(new Vector2(x, y));

            // ➤ Move vertically (optional step if space allows)
            boolean doVertical = MathUtils.randomBoolean();
            if (doVertical) {
                int dir = MathUtils.randomBoolean() ? 1 : -1; // up or down
                int vertSteps = MathUtils.random(1, 3);
                int newY = MathUtils.clamp(y + dir * vertSteps, 1, rows - 2);

                if (newY != y) {
                    y = newY;
                    path.add(new Vector2(x, y));
                }
            }
        }

        return path;
    }
}
