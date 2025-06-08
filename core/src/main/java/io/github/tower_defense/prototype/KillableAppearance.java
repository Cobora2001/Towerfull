package io.github.tower_defense.prototype;

import com.badlogic.gdx.graphics.Texture;

public class KillableAppearance {
    private final Texture texture;
    private final float width, height;

    public KillableAppearance(Texture texture, float width, float height) {
        this.texture = texture;
        this.width = width;
        this.height = height;
    }

    public Texture getTexture() {
        return texture;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
