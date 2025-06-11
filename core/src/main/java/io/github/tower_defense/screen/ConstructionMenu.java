package io.github.tower_defense.screen;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import io.github.tower_defense.prototype.EconomyManager;
import io.github.tower_defense.prototype.PrototypeFactory;
import io.github.tower_defense.prototype.Tower;
import io.github.tower_defense.prototype.TowerType;

import java.util.EnumMap;

public class ConstructionMenu extends Table {
    public interface TowerSelectionListener {
        void onTowerSelected(TowerType type);
        void onCancel();
    }

    private final EnumMap<TowerType, TextButton> buttons = new EnumMap<>(TowerType.class);
    private final PrototypeFactory<TowerType, Tower> factory;

    public ConstructionMenu(
        Skin skin,
        EconomyManager economy,
        PrototypeFactory<TowerType, Tower> factory,
        TowerSelectionListener listener
    ) {
        super(skin);
        this.factory = factory;

        pad(6);
        align(Align.topLeft);
        setBackground("default-round");

        add(new Label("Build Menu", skin)).padBottom(6).row();

        // Sort TowerTypes by the prototype's cost without cloning
        java.util.List<TowerType> sortedTypes = java.util.Arrays.stream(TowerType.values())
            .sorted(java.util.Comparator.comparingInt(type -> factory.getPrototype(type).getCost()))
            .collect(java.util.stream.Collectors.toList());

        for (TowerType type : sortedTypes) {
            Tower prototype = factory.getPrototype(type);
            String label = type.name() + " (" + prototype.getCost() + "g)";
            TextButton btn = new TextButton(label, skin);
            btn.getLabel().setFontScale(0.8f);
            btn.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    listener.onTowerSelected(type);
                    setVisible(false);
                }
            });
            buttons.put(type, btn);
            add(btn).padBottom(4).width(140).fillX().row();
        }

        setTransform(true); // important !
        setOrigin(Align.topLeft); // origine pour shrink

        TextButton cancel = new TextButton("Cancel", skin);
        cancel.getLabel().setFontScale(0.8f);
        cancel.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                listener.onCancel();
                setVisible(false);
            }
        });
        add(cancel).padTop(8).width(140).fillX();
        setVisible(false); // par défaut caché
    }

    public void setEnabled(TowerType type, boolean enabled) {
        if (buttons.containsKey(type)) {
            buttons.get(type).setDisabled(!enabled);
        }
    }
}
