package io.github.tower_defense.gameBoard.level.generators;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.HashSet;

public class TowerPlacementGenerator {

    public static Array<Vector2> generate(int width, int height, Array<Vector2> path) {
        // Collect all path cells
        HashSet<Vector2> pathCells = new HashSet<>();
        for (int i = 0; i < path.size - 1; i++) {
            Vector2 start = path.get(i);
            Vector2 end = path.get(i + 1);

            int x1 = (int) start.x;
            int y1 = (int) start.y;
            int x2 = (int) end.x;
            int y2 = (int) end.y;

            int dx = Integer.signum(x2 - x1);
            int dy = Integer.signum(y2 - y1);

            int x = x1, y = y1;
            while (x != x2 || y != y2) {
                pathCells.add(new Vector2(x, y));
                if (x != x2) x += dx;
                if (y != y2) y += dy;
            }
            pathCells.add(new Vector2(x2, y2)); // include the final point
        }

        // Exclude area around the entry and exit
        HashSet<Vector2> entryExit = new HashSet<>();
        Vector2 start = path.first();
        Vector2 end = path.peek();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                entryExit.add(new Vector2((int) start.x + dx, (int) start.y + dy));
                entryExit.add(new Vector2((int) end.x + dx, (int) end.y + dy));
            }
        }

        Array<Vector2> validSpots = new Array<>();

        // Generate buildable spots adjacent to the path
        for (Vector2 point : pathCells) {
            int x = (int) point.x;
            int y = (int) point.y;

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) continue;

                    int nx = x + dx;
                    int ny = y + dy;

                    if (nx < 0 || ny < 0 || nx >= width || ny >= height) continue;

                    Vector2 candidate = new Vector2(nx, ny);

                    if (containsVector(pathCells, candidate)) continue;
                    if (containsVector(entryExit, candidate)) continue;
                    if (containsVector(validSpots, candidate)) continue;

                    validSpots.add(candidate);
                }
            }
        }

        // Limit number of spots based on path length
        int pathLength = pathCells.size();
        int maxSpots = Math.min(Math.max(4, pathLength / 4), 20);

        validSpots.shuffle();
        validSpots.truncate(maxSpots);

        return validSpots;
    }

    private static boolean containsVector(Iterable<Vector2> list, Vector2 target) {
        for (Vector2 v : list) {
            if ((int) v.x == (int) target.x && (int) v.y == (int) target.y) return true;
        }
        return false;
    }
}
