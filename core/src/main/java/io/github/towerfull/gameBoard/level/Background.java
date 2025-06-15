package io.github.towerfull.gameBoard.level;

import io.github.towerfull.entities.Appearance;

public class Background {
    private final Appearance backgroundAppearance;
    private final Appearance pathAppearance;
    private final Appearance pathStartAppearance;
    private final Appearance pathEndAppearance;

    public Background(Appearance backgroundAppearance, Appearance pathAppearance,
                      Appearance pathStartAppearance, Appearance pathEndAppearance) {
        this.backgroundAppearance = backgroundAppearance;
        this.pathAppearance = pathAppearance;
        this.pathStartAppearance = pathStartAppearance;
        this.pathEndAppearance = pathEndAppearance;
    }

    public Appearance getBackgroundAppearance() {
        return backgroundAppearance;
    }

    public Appearance getPathAppearance() {
        return pathAppearance;
    }

    public Appearance getPathStartAppearance() {
        return pathStartAppearance;
    }

    public Appearance getPathEndAppearance() {
        return pathEndAppearance;
    }
}
