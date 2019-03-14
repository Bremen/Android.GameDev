package ru.geekbrains.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.base.Base2DScreen;
import ru.geekbrains.sprite.Background;
import ru.geekbrains.sprite.BadLogic;

public class MenuScreen extends Base2DScreen {

    private BadLogic badLogic;
    private Texture img;
    private Texture img_background;
    private Background background;

    @Override
    public void show() {
        super.show();
        img = new Texture("E:\\Geekbrains\\6.Android.GameDevBasic\\StarGame\\android\\assets\\badlogic.jpg");
        badLogic = new BadLogic(new TextureRegion(img));

        img_background = new Texture("E:\\Geekbrains\\6.Android.GameDevBasic\\StarGame\\android\\assets\\space_background.jpg");
        background = new Background(new TextureRegion(img_background));
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClearColor(0.51f, 0.34f, 0.64f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        background.draw(batch);
        badLogic.draw(batch);
        batch.end();
        badLogic.update();
    }

    @Override
    public void dispose() {
        img.dispose();
        img_background.dispose();
        super.dispose();
    }

    @Override
    public boolean touchDown(Vector2 touch, int pointer) {
        badLogic.touchDown(touch, pointer);
        return false;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        float aspect = width / (float) height;
        background.setWidth(100f*aspect);
    }
}
