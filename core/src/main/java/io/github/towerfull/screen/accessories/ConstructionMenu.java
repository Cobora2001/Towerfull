package io.github.towerfull.screen.accessories;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import io.github.towerfull.entities.defenses.Tower;
import io.github.towerfull.enumElements.TowerType;
import io.github.towerfull.tools.GameAssets;

import java.util.EnumMap;

public class ConstructionMenu extends Table {
    public interface TowerSelectionListener {
        void onTowerSelected(TowerType type);
        void onCancel();
    }

    private final EnumMap<TowerType, TextButton> buttons = new EnumMap<>(TowerType.class);

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

        for (TowerType type : sortedTypes) {
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

        setTransform(true); // important !
        setOrigin(Align.topLeft); // origine pour shrink

        TextButton cancel = new TextButton("Cancel", skin);
        cancel.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                listener.onCancel();
                setVisible(false);
            }
        });
        add(cancel).padTop(8).fillX().growX();
        setVisible(false); // par défaut caché
    }

    public void setEnabled(TowerType type, boolean enabled) {
        if (buttons.containsKey(type)) {
            buttons.get(type).setDisabled(!enabled);
        }
    }

    public void setScale(float scale) {
        for (TextButton button : buttons.values()) {
            button.getLabel().setFontScale(scale);
        }
        invalidateHierarchy(); // Update layout after changing scale
    }
}
