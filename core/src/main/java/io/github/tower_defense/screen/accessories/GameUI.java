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
    public enum LayoutDirection { VERTICAL_TOP, VERTICAL_BOTTOM, HORIZONTAL_LEFT, HORIZONTAL_RIGHT }

    private final Stage stage;
    private final Table root;

    private ConstructionMenu constructionMenu;
    private DestructionMenu destructionMenu;

    private final Skin skin;
    private final EconomyManager economy;
    private final ConstructionController controller;

    private LayoutDirection currentDirection;

    private final Label goldLabel;
    private final Label lifeLabel;

    private final TextButton saveButton;
    private final TextButton menuButton;
    private final TextButton quitButton;
    private final TextButton pauseResumeButton;

    private boolean isPaused;
    private float scale;

    public GameUI(Skin skin, Viewport viewport, EconomyManager economy, ConstructionController controller) {
        this.stage = new Stage(viewport);
        this.root = new Table();
        this.root.setFillParent(true);
        this.stage.addActor(root);

        this.skin = skin;
        this.economy = economy;
        this.controller = controller;

        this.isPaused = controller.isPaused();

        // Compute scale based on viewport
        scale = computeScale(viewport.getWorldWidth(), viewport.getWorldHeight());

        // Create menus with initial scale
        constructionMenu = createConstructionMenu();
        destructionMenu = createDestructionMenu();

        // Labels
        goldLabel = new Label("Gold: 0", skin);
        lifeLabel = new Label("Life: 0", skin);

        // Buttons
        saveButton = new TextButton("Save", skin);
        menuButton = new TextButton("Menu", skin);
        quitButton = new TextButton("Quit", skin);
        pauseResumeButton = new TextButton("Pause", skin);

        // Set scaling
        applyScale();

        // Listeners
        pauseResumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isPaused = !isPaused;
                controller.setPaused(isPaused);
                pauseResumeButton.setText(isPaused ? "Resume" : "Pause");
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

    private float computeScale(float width, float height) {
        return Math.min(width, height) / 720f;
    }

    private void applyScale() {
        goldLabel.setFontScale(scale);
        lifeLabel.setFontScale(scale);
        pauseResumeButton.getLabel().setFontScale(scale);
        saveButton.getLabel().setFontScale(scale);
        menuButton.getLabel().setFontScale(scale);
        quitButton.getLabel().setFontScale(scale);
    }

    private ConstructionMenu createConstructionMenu() {
        return new ConstructionMenu(skin, economy, new ConstructionMenu.TowerSelectionListener() {
            @Override
            public void onTowerSelected(TowerType type) {
                controller.handleSelection(type, constructionMenu);
            }

            @Override
            public void onCancel() {
                controller.cancel(constructionMenu);
            }
        }, scale);
    }

    private DestructionMenu createDestructionMenu() {
        return new DestructionMenu(skin, new DestructionListener() {
            @Override
            public void onSellTower(BuildSpot spot) {
                controller.sellTower(spot);
                destructionMenu.setVisible(false);
            }

            @Override
            public void onCancel() {
                destructionMenu.setVisible(false);
            }
        }, scale);
    }

    public void layout(float availableWidth, float availableHeight) {
        boolean isHorizontal = availableWidth > availableHeight;

        root.clear();

        Table statusTable = new Table();
        statusTable.add(goldLabel).left().pad(10 * scale);
        statusTable.add(lifeLabel).left().pad(10 * scale);

        Table buttonsTable = new Table();
        buttonsTable.defaults().pad(5 * scale).fillX().expandX();

        buttonsTable.add(pauseResumeButton).row();
        buttonsTable.add(saveButton).row();
        buttonsTable.add(menuButton).row();
        buttonsTable.add(quitButton).row();

        Table menuTable = new Table();
        if (isHorizontal) {
            float uiWidth = availableWidth / 6f;
            float gameWidth = availableWidth - uiWidth;

            setLayoutDirection(LayoutDirection.HORIZONTAL_RIGHT);
            root.clear();

            Table menuGroup = new Table();
            menuGroup.defaults().width(uiWidth - 20 * scale).pad(5 * scale); // Ensures no overflow

            // Status indicators
            menuGroup.add(goldLabel).left().row();
            menuGroup.add(lifeLabel).left().row();

            // Buttons (fixed width, no expand)
            menuGroup.add(pauseResumeButton).fillX().row();
            menuGroup.add(saveButton).fillX().row();
            menuGroup.add(menuButton).fillX().row();
            menuGroup.add(quitButton).fillX().row();

            // Menus (constrain width!)
            constructionMenu.setWidth(uiWidth - 20 * scale);
            destructionMenu.setWidth(uiWidth - 20 * scale);
            menuGroup.add(constructionMenu).left().row();
            menuGroup.add(destructionMenu).left().row();

            // Final layout in root
            root.add().width(gameWidth).expand().fill(); // Game area
            root.add(menuGroup).width(uiWidth).fillY(); // UI panel
        } else {
            setLayoutDirection(LayoutDirection.VERTICAL_BOTTOM);

            // In bottom layout, place everything horizontally
            Table bottomRow = new Table();
            bottomRow.defaults().pad(5 * scale).fillY();

            bottomRow.add(goldLabel).left();
            bottomRow.add(lifeLabel).left();
            bottomRow.add(pauseResumeButton);
            bottomRow.add(saveButton);
            bottomRow.add(menuButton);
            bottomRow.add(quitButton);
            bottomRow.add(constructionMenu).left();
            bottomRow.add(destructionMenu).left();

            float uiHeight = availableHeight / 6f;
            float gameHeight = availableHeight - uiHeight;

            root.row();
            root.add().height(gameHeight).expand().fill();
            root.row();
            root.add(bottomRow).height(uiHeight).fillX().bottom();
        }
    }

    private void setLayoutDirection(LayoutDirection direction) {
        if (direction == currentDirection) return;
        currentDirection = direction;
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);

        this.scale = computeScale(width, height);
        applyScale();

        // Re-create menus with new scale
        root.removeActor(constructionMenu);
        root.removeActor(destructionMenu);
        constructionMenu = createConstructionMenu();
        destructionMenu = createDestructionMenu();

        // Re-layout with new scale
        layout(stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
    }

    public LayoutDirection getLayoutDirection() {
        return currentDirection;
    }

    public Table getRootTable() {
        return root;
    }

    public Stage getStage() {
        return stage;
    }

    public ConstructionMenu getConstructionMenu() {
        return constructionMenu;
    }

    public DestructionMenu getDestructionMenu() {
        return destructionMenu;
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
}
