package io.github.tower_defense.screen.accessories;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import io.github.tower_defense.entities.defenses.BuildSpot;
import io.github.tower_defense.listener.DestructionListener;

public class DestructionMenu extends Table {
    private final Label infoLabel;
    private BuildSpot currentSpot;

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

    public void showForTower(BuildSpot spot, String labelText) {
        if (spot == null || !spot.isUsed()) {
            hide();
            return;
        }
        this.currentSpot = spot;
        this.infoLabel.setText(labelText != null ? labelText : "Tower Selected");
        setVisible(true);
    }

    public void hide() {
        this.currentSpot = null;
        setVisible(false);
    }

    public void setScale(float scale) {
        infoLabel.getStyle().font.getData().setScale(scale);
        for (Actor actor : getChildren()) {
            if (actor instanceof TextButton) {
                ((TextButton) actor).getLabel().setFontScale(scale);
            }
        }
        invalidateHierarchy();
    }
}
