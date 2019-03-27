package ru.geekbrains.sprite;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ru.geekbrains.base.ScaledButton;
import ru.geekbrains.math.Rect;

public class ButtonNewGame extends ScaledButton {
    public ButtonNewGame(TextureAtlas atlas) {
        super(atlas.findRegion("button_new_game"));
        setHeightProportion(0.05f);
    }

    @Override
    public void resize(Rect worldBounds) {
        pos.sub(0,0.1f);
    }

    @Override
    protected void action() {

    }
}
