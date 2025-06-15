package io.github.tower_defense.screen.accessories;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.tower_defense.entities.defenses.BuildSpot;
import io.github.tower_defense.enumElements.TowerType;
import io.github.tower_defense.gameBoard.EconomyManager;
import io.github.tower_defense.listener.DestructionListener;
import io.github.tower_defense.tools.SaveManager;

public class GameUI {
    private final Stage stage;
    private final Table root;

    private final ConstructionMenu constructionMenu;
    private final DestructionMenu destructionMenu;

    private final Container<ConstructionMenu> constructionMenuContainer;
    private final Container<DestructionMenu> destructionMenuContainer;

    private final Skin skin;
    private final EconomyManager economy;
    private final ConstructionController controller;

    private final Label goldLabel;
    private final Label lifeLabel;

    private final TextButton saveButton;
    private final TextButton menuButton;
    private final TextButton quitButton;
    private final TextButton pauseResumeButton;

    private static final String PAUSE_BUTTON_TEXT = "Pause";
    private static final String RESUME_BUTTON_TEXT = "Resume";

    private boolean isPaused;

    // We store sidebar as a field so we can modify it when toggling menus
    private final Table sideBar;

    public GameUI(Skin skin, Viewport viewport, EconomyManager economy, ConstructionController controller) {
        this.stage = new Stage(viewport);
        this.root = new Table();
        this.root.setFillParent(true);
        this.stage.addActor(root);

        this.skin = skin;
        this.economy = economy;
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

    public static float getSidebarWidth() {
        return 200;
    }

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

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public Stage getStage() {
        return stage;
    }

    public void act(float delta) {
        stage.act(delta);
    }

    public void draw() {
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
    }

    public void updateGold(int amount) {
        goldLabel.setText("Gold: " + amount);
    }

    public void updateLife(int life) {
        lifeLabel.setText("Life: " + life);
    }

    public ConstructionMenu getConstructionMenu() {
        return constructionMenu;
    }

    public DestructionMenu getDestructionMenu() {
        return destructionMenu;
    }

    public void showConstructionMenu() {
        if (constructionMenuContainer.getParent() == null) {
            sideBar.add(constructionMenuContainer).left().row();
        }
    }

    public void hideConstructionMenu() {
        constructionMenuContainer.remove();
    }

    public void showDestructionMenu() {
        if (destructionMenuContainer.getParent() == null) {
            sideBar.add(destructionMenuContainer).left().row();
        }
    }

    public void hideDestructionMenu() {
        destructionMenuContainer.remove();
    }
}
