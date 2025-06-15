// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: A UI for the game that includes construction and destruction menus,
//      economy and life information, and control buttons.
// -------------------------------------------------------------------------------------

package io.github.towerfull.screen.accessories;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.towerfull.entities.defenses.BuildSpot;
import io.github.towerfull.enumElements.TowerType;
import io.github.towerfull.listener.DestructionListener;
import io.github.towerfull.tools.SaveManager;

/**
 * GameUI is responsible for managing the user interface of the game,
 * including construction and destruction menus, economy and life information,
 * and control buttons for saving, exiting, and pausing/resuming the game.
 */
public class GameUI {
    // The stage where all UI elements are drawn
    private final Stage stage;

    // The root table that contains all UI elements
    private final Table root;

    // Menus for construction and destruction of towers
    private final ConstructionMenu constructionMenu;
    private final DestructionMenu destructionMenu;

    // Containers for the construction and destruction menus
    private final Container<ConstructionMenu> constructionMenuContainer;
    private final Container<DestructionMenu> destructionMenuContainer;

    // The skin used for styling UI elements
    private final Skin skin;

    // The controller that manages game logic and interactions
    private final UiController controller;

    // Labels for displaying gold and life information
    private final Label goldLabel;
    private final Label lifeLabel;

    // Buttons for saving the game, accessing the menu, quitting, and pausing/resuming
    private final TextButton saveButton;
    private final TextButton menuButton;
    private final TextButton quitButton;
    private final TextButton pauseResumeButton;

    // Constants for button text
    private static final String PAUSE_BUTTON_TEXT = "Pause";
    private static final String RESUME_BUTTON_TEXT = "Resume";

    // Flag to track if the game is currently paused
    private boolean isPaused;

    // We store sidebar as a field so we can modify it when toggling menus
    private final Table sideBar;

    /**
     * Constructor for GameUI.
     *
     * @param skin       The skin used for styling UI elements.
     * @param viewport   The viewport for the stage.
     * @param controller The controller that manages game logic and interactions.
     */
    public GameUI(Skin skin, Viewport viewport, UiController controller) {
        this.stage = new Stage(viewport);
        this.root = new Table();
        this.root.setFillParent(true);
        this.stage.addActor(root);

        this.skin = skin;
        this.controller = controller;
        this.isPaused = controller.isPaused();

        goldLabel = new Label("Gold: 0", skin);
        lifeLabel = new Label("Life: 0", skin);

        saveButton = new TextButton("Save", skin);
        menuButton = new TextButton("Menu", skin);
        quitButton = new TextButton("Quit", skin);
        pauseResumeButton = new TextButton(isPaused ? RESUME_BUTTON_TEXT : PAUSE_BUTTON_TEXT, skin);

        constructionMenu = createConstructionMenu();
        constructionMenuContainer = new Container<>(constructionMenu);

        destructionMenu = createDestructionMenu();
        destructionMenuContainer = new Container<>(destructionMenu);

        sideBar = new Table(); // initialize here
        setupListeners();
        setupStaticUILayout();
    }

    /**
     * Returns the width of the sidebar.
     *
     * @return The width of the sidebar.
     */
    public static float getSidebarWidth() {
        return 200;
    }

    /**
     * Sets up listeners for the buttons in the UI.
     * This includes pause/resume, save, menu, and quit actions.
     */
    private void setupListeners() {
        pauseResumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isPaused = !isPaused;
                controller.setPaused(isPaused);
                pauseResumeButton.setText(isPaused ? RESUME_BUTTON_TEXT : PAUSE_BUTTON_TEXT);
            }
        });

        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SaveManager.getInstance().saveGameWithTimestamp(controller.getGameArea());
            }
        });

        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.exitToMenu();
            }
        });

        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
    }

    /**
     * Sets up the static layout of the UI, including the sidebar and its contents.
     * This method is called during initialization to set up the initial UI structure.
     */
    private void setupStaticUILayout() {
        sideBar.top().pad(10);
        sideBar.defaults().pad(2);

        sideBar.add(lifeLabel).left().row();
        sideBar.add(goldLabel).left().row();

        sideBar.add(pauseResumeButton).width(100).height(20).left().row();
        sideBar.add(saveButton).width(100).height(20).left().row();
        sideBar.add(menuButton).width(100).height(20).left().row();
        sideBar.add(quitButton).width(100).height(20).left().row();

        root.add().expand().fill(); // Game area
        root.add(sideBar).width(getSidebarWidth()).top(); // Sidebar
    }

    /**
     * Creates the construction menu with a listener for tower selection.
     *
     * @return The constructed ConstructionMenu.
     */
    private ConstructionMenu createConstructionMenu() {
        return new ConstructionMenu(skin, new ConstructionMenu.TowerSelectionListener() {
            @Override
            public void onTowerSelected(TowerType type) {
                controller.handleSelection(type, constructionMenu);
            }

            @Override
            public void onCancel() {
                controller.cancel(constructionMenu);
            }
        });
    }

    /**
     * Creates the destruction menu with a listener for tower selling and cancellation.
     *
     * @return The constructed DestructionMenu.
     */
    private DestructionMenu createDestructionMenu() {
        return new DestructionMenu(skin, new DestructionListener() {
            @Override
            public void onSellTower(BuildSpot spot) {
                controller.sellTower(spot);
                hideDestructionMenu();
            }

            @Override
            public void onCancel() {
                hideDestructionMenu();
            }
        });
    }

    /**
     * Resizes the stage viewport to fit the new width and height.
     *
     * @param width  The new width of the viewport.
     * @param height The new height of the viewport.
     */
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /**
     * Returns the stage associated with this GameUI.
     *
     * @return The stage where all UI elements are drawn.
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Updates the UI elements based on the current game state.
     * This method is called every frame to ensure the UI reflects the latest game data.
     *
     * @param delta The time in seconds since the last frame.
     */
    public void act(float delta) {
        stage.act(delta);
    }

    /**
     * Draws the UI elements on the stage.
     * This method is called every frame to render the UI.
     */
    public void draw() {
        stage.draw();
    }

    /**
     * Disposes of the stage and its resources.
     * This method should be called when the game is closed or the UI is no longer needed.
     */
    public void dispose() {
        stage.dispose();
    }

    /**
     * Updates the gold label to reflect the current amount of gold.
     *
     * @param amount The new amount of gold to display.
     */
    public void updateGold(int amount) {
        goldLabel.setText("Gold: " + amount);
    }

    /**
     * Updates the life label to reflect the current amount of life.
     *
     * @param life The new amount of life to display.
     */
    public void updateLife(int life) {
        lifeLabel.setText("Life: " + life);
    }

    /**
     * Returns the construction menu.
     *
     * @return The ConstructionMenu instance.
     */
    public ConstructionMenu getConstructionMenu() {
        return constructionMenu;
    }

    /**
     * Returns the destruction menu.
     *
     * @return The DestructionMenu instance.
     */
    public DestructionMenu getDestructionMenu() {
        return destructionMenu;
    }

    /**
     * Returns the sidebar table.
     *
     * @return The Table instance representing the sidebar.
     */
    public void showConstructionMenu() {
        if(constructionMenuContainer.getParent() == null) {
            sideBar.add(constructionMenuContainer).left().row();
        }
    }

    /**
     * Hides the construction menu from the sidebar.
     */
    public void hideConstructionMenu() {
        constructionMenuContainer.remove();
    }

    /**
     * Shows the destruction menu in the sidebar.
     * This method adds the destruction menu container to the sidebar if it is not already present.
     */
    public void showDestructionMenu() {
        if(destructionMenuContainer.getParent() == null) {
            sideBar.add(destructionMenuContainer).left().row();
        }
    }

    /**
     * Hides the destruction menu from the sidebar.
     * This method removes the destruction menu container from the sidebar.
     */
    public void hideDestructionMenu() {
        destructionMenuContainer.remove();
    }
}
