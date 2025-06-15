// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: Is the main class and the entry point of the game.
// -------------------------------------------------------------------------------------

package io.github.towerfull;

import com.badlogic.gdx.Game;
import io.github.towerfull.screen.MainMenuScreen;
import io.github.towerfull.tools.GameAssets;

/**
 * The main class of the Tower Defense game.
 * It initializes the game assets and sets the initial screen to the main menu.
 */
public class Main extends Game {
    /**
     * The main entry point of the game.
     * Initializes the game assets and sets the initial screen to the main menu.
     */
    @Override
    public void create() {
        GameAssets.get().loadAll();
        this.setScreen(new MainMenuScreen(this));
    }

    /**
     * Disposes of the game assets when the game is closed.
     * This method is called to clean up resources and prevent memory leaks.
     */
    @Override
    public void dispose() {
        GameAssets.get().dispose(); // Handles disposing of all game assets
        super.dispose();
    }
}
