package io.github.tower_defense.Level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.HashSet;

public class TowerPlacementGenerator {

    public static Array<Vector2> generate(Level level) {
        int cols = level.getCols();
        int rows = level.getRows();
        Array<Vector2> path = level.getPathPoints();

        // Set des positions du chemin, coordonnées entières
        HashSet<Vector2> pathCells = new HashSet<>();
        for (Vector2 p : path) {
            pathCells.add(new Vector2((int) p.x, (int) p.y));
        }

        // Positions autour de l'entrée/sortie à exclure
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

        // Pour chaque voisin du chemin
        for (Vector2 point : path) {
            int x = (int) point.x;
            int y = (int) point.y;

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) continue;

                    int nx = x + dx;
                    int ny = y + dy;

                    if (nx < 0 || ny < 0 || nx >= cols || ny >= rows) continue;

                    Vector2 candidate = new Vector2(nx, ny);

                    if (containsVector(pathCells, candidate)) continue;
                    if (containsVector(entryExit, candidate)) continue;
                    if (containsVector(validSpots, candidate)) continue;

                    validSpots.add(candidate);
                }
            }
        }

        validSpots.shuffle();
        if (validSpots.size > 4) validSpots.truncate(4);

        return validSpots;
    }

    private static boolean containsVector(Iterable<Vector2> list, Vector2 target) {
        for (Vector2 v : list) {
            if ((int) v.x == (int) target.x && (int) v.y == (int) target.y) return true;
        }
        return false;
    }
}
