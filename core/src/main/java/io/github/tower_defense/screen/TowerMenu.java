package io.github.tower_defense.screen;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class TowerMenu extends Table {
    public interface TowerActionListener {
        void onDestroy();
    }

    public TowerMenu(Skin skin, int refund, TowerActionListener listener) {
        super(skin);
        pad(8);
        setBackground("default-round");

        add(new Label("Tower Options", skin)).padBottom(10).row();
        add(new Label("Refund: " + refund + " gold", skin)).padBottom(10).row();

        TextButton destroyButton = new TextButton("Destroy", skin);
        destroyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.onDestroy();
            }
        });

        add(destroyButton).fillX();
    }
}
