package ru.geekbrains.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ru.geekbrains.base.Base2DScreen;
import ru.geekbrains.math.Rect;
import ru.geekbrains.sprite.Background;
import ru.geekbrains.sprite.Star;

public class GameScreen extends Base2DScreen {

    private static final int STAR_COUNT = 256;

    private Texture img_background;
    private Background background;
    private TextureAtlas atlas;
    private Star starList[];

    @Override
    public void show() {
        super.show();

        img_background = new Texture("textures/bg.png");
        background = new Background(new TextureRegion(img_background));
        atlas = new TextureAtlas("textures/menuAtlas.tpack");
        starList = new Star[STAR_COUNT];
        for (int i = 0; i < STAR_COUNT; i++) {
            starList[i] = new Star(atlas);
        }
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);

        background.resize(worldBounds);
        for (Star star : starList) {
            star.resize(worldBounds);
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        update(delta);
        draw();
    }

    private void update(float delta) {
        for (Star star : starList) {
            star.update(delta);
        }
    }

    private void draw() {
        Gdx.gl.glClearColor(0.51f, 0.34f, 0.64f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);
        background.draw(batch);
        for (Star star : starList) {
            batch.setColor(star.getColor());
            star.draw(batch);
        }
        batch.setColor(1f, 1f, 1f, 1f);
        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
