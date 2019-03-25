package ru.geekbrains.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.base.Base2DScreen;
import ru.geekbrains.math.Rect;
import ru.geekbrains.pool.BulletPool;
import ru.geekbrains.pool.EnemyPool;
import ru.geekbrains.sprite.Background;
import ru.geekbrains.sprite.SpaceShip;
import ru.geekbrains.sprite.Star;
import ru.geekbrains.utils.EnemiesEmitter;

public class GameScreen extends Base2DScreen {

    private static final int STAR_COUNT = 64;

    private Texture img_background;
    private Background background;
    private TextureAtlas atlas;
    private Star starList[];
    private SpaceShip ship;

    private BulletPool bulletPool;
    private EnemyPool enemyPool;

    private EnemiesEmitter enemiesEmitter;

    private Music music;
    private Sound laserSound;
    private Sound bulletSound;

    @Override
    public void show() {
        super.show();
        music = Gdx.audio.newMusic(Gdx.files.internal("sounds/music.mp3"));
        music.setLooping(true);
        music.play();
        img_background = new Texture("textures/bg.png");
        background = new Background(new TextureRegion(img_background));
        atlas = new TextureAtlas("textures/mainAtlas.tpack");
        starList = new Star[STAR_COUNT];
        for (int i = 0; i < STAR_COUNT; i++) {
            starList[i] = new Star(atlas);
        }
        bulletPool = new BulletPool();
        laserSound = Gdx.audio.newSound(Gdx.files.internal("sounds/laser.wav"));
        ship = new SpaceShip(atlas, bulletPool, laserSound);

        bulletSound  = Gdx.audio.newSound(Gdx.files.internal("sounds/bullet.wav"));
        enemyPool = new EnemyPool(bulletPool, worldBounds, bulletSound);
        enemiesEmitter = new EnemiesEmitter(atlas, worldBounds, enemyPool);
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);

        background.resize(worldBounds);
        for (Star star : starList) {
            star.resize(worldBounds);
        }

        ship.resize(worldBounds);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        update(delta);
        deleteAllDestroed();
        draw();
    }

    private void deleteAllDestroed() {
        bulletPool.freeAllDestroedActiveSprites();
        enemyPool.freeAllDestroedActiveSprites();
    }

    private void update(float delta) {
        for (Star star : starList) {
            star.update(delta);
        }
        ship.update(delta);
        bulletPool.updateActiveSprites(delta);
        enemyPool.updateActiveSprites(delta);
        enemiesEmitter.generate(delta);
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
        ship.draw(batch);
        bulletPool.drawActiveSprites(batch);
        enemyPool.drawActiveSprites(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        img_background.dispose();
        atlas.dispose();
        music.dispose();
        bulletPool.dispose();
        laserSound.dispose();
        enemyPool.dispose();
        bulletSound.dispose();
        super.dispose();
    }

    @Override
    public boolean touchDown(Vector2 touch, int pointer) {
        ship.touchDown(touch, pointer);
        return false;
    }

    @Override
    public boolean touchUp(Vector2 touch, int pointer) {
        ship.touchUp(touch, pointer);
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        ship.keyDown(keycode);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        ship.keyUp(keycode);
        return false;
    }
}
