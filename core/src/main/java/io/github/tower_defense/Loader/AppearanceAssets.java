package io.github.tower_defense.Loader;

import io.github.tower_defense.Prototype.KillableAppearance;

import java.util.HashMap;
import java.util.Map;

public class AppearanceAssets {
    private static AppearanceAssets instance = null;

    private final Map<String, KillableAppearance> appearances = new HashMap<String, KillableAppearance>();

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
    public void registerAppearance(String name, KillableAppearance appearance) {
        if (name == null || appearance == null) {
            throw new IllegalArgumentException("Name and appearance cannot be null");
        }
        appearances.put(name, appearance);
    }

    public KillableAppearance getAppearance(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        return appearances.get(name);
    }
}
