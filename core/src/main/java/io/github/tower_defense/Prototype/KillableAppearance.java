package io.github.tower_defense.Prototype;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MonsterAppearance {
    public TextureRegion texture;
    public float width, height;

    public MonsterAppearance(TextureRegion texture, float width, float height) {
        this.texture = texture;
        this.width = width;
        this.height = height;
    }
}
