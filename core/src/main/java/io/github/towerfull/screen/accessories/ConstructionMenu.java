// Authors: Thomas Vuilleumier, Sebastian Diaz, Lionel Pollien
// Date of creation: 2025-06-15
// Aim: Allows the UI to display a menu for tower construction.
// -------------------------------------------------------------------------------------

package io.github.towerfull.screen.accessories;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import io.github.towerfull.entities.defenses.Tower;
import io.github.towerfull.enumElements.TowerType;
import io.github.towerfull.tools.GameAssets;

import java.util.EnumMap;

/**
 * ConstructionMenu is a UI component that allows players to select and build towers.
 * It displays a list of available tower types, their costs, and provides buttons for selection.
 */
public class ConstructionMenu extends Table {
    /**
     * Listener interface for tower selection events.
     */
    public interface TowerSelectionListener {
        /**
         * Called when a tower type is selected.
         *
         * @param type The TowerType that was selected.
         */
        void onTowerSelected(TowerType type);

        /**
         * Called when the cancel button is pressed.
         */
        void onCancel();
    }

    // Map to hold buttons for each TowerType
    private final EnumMap<TowerType, TextButton> buttons = new EnumMap<>(TowerType.class);

    /**
     * Constructs a ConstructionMenu with the specified skin and listener.
     *
     * @param skin The skin to use for the UI elements.
     * @param listener The listener to handle tower selection and cancellation events.
     */
    public ConstructionMenu(Skin skin, TowerSelectionListener listener) {
        super(skin);

        pad(6);
        align(Align.topLeft);
        setBackground("default-round");

        add(new Label("Build Menu", skin)).padBottom(6).row();

        // Sort TowerTypes by the prototype's cost without cloning
        java.util.List<TowerType> sortedTypes = java.util.Arrays.stream(TowerType.values())
            .sorted(java.util.Comparator.comparingInt(type -> GameAssets.get().towerFactory.getPrototype(type).getCost()))
            .collect(java.util.stream.Collectors.toList());

        for(TowerType type : sortedTypes) {
            Tower prototype = GameAssets.get().towerFactory.getPrototype(type);
            String label = type.name() + " (" + prototype.getCost() + "g)";
            TextButton btn = new TextButton(label, skin);
            btn.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    listener.onTowerSelected(type);
                    setVisible(false);
                }
            });
            buttons.put(type, btn);
            add(btn).padBottom(4).fillX().growX().row();
        }

        setTransform(true);
        setOrigin(Align.topLeft);

        TextButton cancel = new TextButton("Cancel", skin);
        cancel.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                listener.onCancel();
                setVisible(false);
            }
        });
        add(cancel).padTop(8).fillX().growX();
        setVisible(false);
    }

    /**
     * Sets the enabled state of a specific tower type button.
     *
     * @param type The TowerType to enable or disable.
     * @param enabled True to enable the button, false to disable it.
     */
    public void setEnabled(TowerType type, boolean enabled) {
        if(buttons.containsKey(type)) {
            buttons.get(type).setDisabled(!enabled);
        }
    }

    /**
     * Sets the scale of all buttons in the menu.
     *
     * @param scale The scale factor to apply to the button labels.
     */
    public void setScale(float scale) {
        for(TextButton button : buttons.values()) {
            button.getLabel().setFontScale(scale);
        }
        invalidateHierarchy(); // Update layout after changing scale
    }
}
