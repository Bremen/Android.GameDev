package ru.geekbrains.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.base.Base2DScreen;

public class MenuScreen extends Base2DScreen{
    private SpriteBatch batch;
    private Texture img;
    private Vector2 touch;
    private Vector2 pos;
    private Vector2 v;
    private int k;

    @Override
    public void show() {
        super.show();

        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");

        touch = new Vector2();
        pos = new Vector2(0,0);
        v = new Vector2(0, 0);
        k = 1;
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(img, pos.x, pos.y);
        batch.end();
        pos.add(v);

        float distance2 = touch.cpy().sub(pos).len2();
        if (distance2 < v.len2()){
            v.setLength2(distance2);
        }

        if (distance2 < 1e-6){
            v.setZero();
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
        super.dispose();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touch.set(screenX, Gdx.graphics.getHeight() - screenY);
        changeVelocity(touch);

        System.out.println("touch x = " + touch.x + " y = " + touch.y);
        return super.touchDown(screenX, screenY, pointer, button);
    }

    private void changeVelocity(Vector2 dest) {
        v.set(dest);
        v.sub(pos);
        v.nor();
        v.setLength(k);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode >= 8 && keycode <= 16){
            k = keycode - 7;
            v.setLength(k);
        }

        switch (keycode){
            case 19:
                pos.add(0, k*10);
                break;
            case 20:
                pos.add(0, -k*10);
                break;
            case 21:
                pos.add(-k*10, 0);
                break;
            case 22:
                pos.add(k*10, 0);
                break;
        }
        if (!v.isZero()){
            changeVelocity(touch);
        }

        return super.keyDown(keycode);
    }

    @Override
    public boolean keyTyped(char character) {
        int code = Character.getNumericValue(character);
        System.out.println(code);
        switch (code){
            case 19:
                pos.add(0, k*10);
                break;
            case 20:
                pos.add(0, -k*10);
                break;
            case 21:
                pos.add(-k*10, 0);
                break;
            case 22:
                pos.add(k*10, 0);
                break;
        }

        return super.keyTyped(character);
    }
}
