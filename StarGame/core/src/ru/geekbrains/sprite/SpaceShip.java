package ru.geekbrains.sprite;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.base.Sprite;

public class SpaceShip extends Sprite {

    private static float V_LEN = 0.6f;

    private Vector2 touch;
    private Vector2 v;
    private Vector2 buf;

    public SpaceShip(TextureAtlas atlas) {
        super(atlas.findRegion("main_ship"), 195, 287);

        setHeightProportion(0.2f);
        touch = new Vector2();
        v = new Vector2();
        buf = new Vector2();
    }

    public void update(float delta) {
        super.update(delta);
        buf.set(touch);
        if (buf.sub(pos).len() <= 0.01*V_LEN) {
            pos.set(touch);
        } else {
            pos.mulAdd(v, delta);
        }
    }

    public boolean touchDown(Vector2 touch, int pointer) {
        this.touch = touch;
        v = touch.cpy().sub(pos).setLength(V_LEN);
        return false;
    }

    public boolean keyDown(int keycode) {
        float shift = V_LEN / 60f;
        switch (keycode){
            case 19:
                pos.add(0, shift);
                break;
            case 20:
                pos.add(0, -shift);
                break;
            case 21:
                pos.add(-shift, 0);
                break;
            case 22:
                pos.add(shift, 0);
                break;
        }
        return false;
    }
}
