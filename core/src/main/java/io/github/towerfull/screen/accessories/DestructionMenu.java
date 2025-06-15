// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: Allows the UI to display a menu for tower destruction.
// -------------------------------------------------------------------------------------

package io.github.towerfull.screen.accessories;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import io.github.towerfull.entities.defenses.BuildSpot;
import io.github.towerfull.listener.DestructionListener;

/**
 * A menu for tower destruction, allowing players to sell towers.
 * It provides a label for information and buttons for selling or canceling.
 */
public class DestructionMenu extends Table {
    // Label to display information about the selected tower
    private final Label infoLabel;

    // The current build spot that the menu is associated with
    private BuildSpot currentSpot;

    /**
     * The constructor for the DestructionMenu.
     * @param skin the skin to style the menu
     * @param listener the listener to handle destruction events
     */
    public DestructionMenu(Skin skin, DestructionListener listener) {
        super(skin);

        pad(6);
        align(Align.topLeft);
        setBackground("default-round");

        infoLabel = new Label("", skin);
        infoLabel.setAlignment(Align.center);
        add(infoLabel).padBottom(6).fillX().row();

        TextButton sell = new TextButton("Sell", skin);
        sell.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                listener.onSellTower(currentSpot);
            }
        });
        add(sell).fillX().padBottom(4).row(); // Explicit row after sell

        TextButton cancelBtn = new TextButton("Cancel", skin);
        cancelBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                listener.onCancel();
                setVisible(false);
            }
        });
        add(cancelBtn).fillX().padTop(4).row();

        setTransform(true);
        setOrigin(Align.topLeft);
        setVisible(false);
    }

    /**
     * Shows the destruction menu for a specific tower at the given build spot.
     * @param spot the build spot where the tower is located
     * @param labelText optional label text to display
     */
    public void showForTower(BuildSpot spot, String labelText) {
        if(spot == null || !spot.isUsed()) {
            hide();
            return;
        }
        this.currentSpot = spot;
        this.infoLabel.setText(labelText != null ? labelText : "Tower Selected");
        setVisible(true);
    }

    /**
     * Hides the destruction menu.
     */
    public void hide() {
        this.currentSpot = null;
        setVisible(false);
    }

    /**
     * Sets the scale of the menu and its components.
     * @param scale the scale factor to apply
     */
    public void setScale(float scale) {
        infoLabel.getStyle().font.getData().setScale(scale);
        for(Actor actor : getChildren()) {
            if(actor instanceof TextButton) {
                ((TextButton) actor).getLabel().setFontScale(scale);
            }
        }
        invalidateHierarchy();
    }
}
