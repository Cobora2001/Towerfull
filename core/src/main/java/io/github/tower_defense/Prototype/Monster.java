package io.github.tower_defense.Prototype;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Monster extends Killable {
    private int speed;
    private int damage;
    private int reward;
    private int pathIndex = 0;
    private float speedMultiplier = 60f;

    private Vector2 pixelPos; // position réelle en pixel pour le déplacement fluide

    public Monster(int pv, int maxPv, Vector2 logicalPos, int speed, int damage, int reward) {
        super(pv, maxPv, logicalPos, null);
        this.speed = speed;
        this.damage = damage;
        this.reward = reward;
        this.pixelPos = null; // sera initialisée à la première update
    }

    public Monster(Monster m) {
        super(m);
        this.speed = m.speed;
        this.damage = m.damage;
        this.reward = m.reward;
        this.pathIndex = m.pathIndex;
        this.pixelPos = m.pixelPos.cpy();
    }

    @Override
    public Monster clone() {
        return new Monster(this);
    }

    public void update(float delta, Array<Vector2> path, GameArea area) {
        if (pathIndex >= path.size) return;

        // Initialisation de la position pixel à la première frame
        if (pixelPos == null) {
            pixelPos = area.logicalToPixel(logicalPos);
        }

        Vector2 targetLogical = path.get(pathIndex);
        Vector2 targetPixel = area.logicalToPixel(targetLogical);

        Vector2 direction = targetPixel.cpy().sub(pixelPos);
        float distance = direction.len();

        if (distance < 2f) { // Seuil de changement de case
            pathIndex++;
        } else {
            direction.nor().scl(speed * delta * speedMultiplier);
            pixelPos.add(direction);
            this.logicalPos = area.pixelToLogical(pixelPos);
        }
    }

    public int getSpeed() {
        return speed;
    }

    public int getDamage() {
        return damage;
    }

    public int getReward() {
        return reward;
    }

    public int getPathIndex() {
        return pathIndex;
    }
}
