package io.github.tower_defense.loader;

import io.github.tower_defense.prototype.Appearance;

import java.util.HashMap;
import java.util.Map;

public class AppearanceAssets {
    private static AppearanceAssets instance = null;

    private final Map<String, Appearance> appearances = new HashMap<String, Appearance>();

    public static AppearanceAssets getInstance() {
        if (instance == null) {
            instance = new AppearanceAssets();
        }
        return instance;
    }

    private AppearanceAssets() {
        // Private constructor to prevent instantiation
    }

    // Add methods to load and manage appearance assets here
    public void registerAppearance(String name, Appearance appearance) {
        if (name == null || appearance == null) {
            throw new IllegalArgumentException("Name and appearance cannot be null");
        }
        appearances.put(name, appearance);
    }

    public Appearance getAppearance(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        return appearances.get(name);
    }
}
