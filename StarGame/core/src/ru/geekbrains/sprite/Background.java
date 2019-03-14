package ru.geekbrains.sprite;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ru.geekbrains.base.Sprite;

public class Background extends Sprite {
    public Background(TextureRegion region) {
        super(region);
        setSize(100.0f, 100.0f);
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
    }
}
