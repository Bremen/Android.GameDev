package ru.geekbrains.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

import ru.geekbrains.base.Base2DScreen;
import ru.geekbrains.base.Sprite;
import ru.geekbrains.math.Rect;
import ru.geekbrains.pool.BulletPool;
import ru.geekbrains.pool.EnemyPool;
import ru.geekbrains.pool.ExplosionPool;
import ru.geekbrains.sprite.Background;
import ru.geekbrains.sprite.Bullet;
import ru.geekbrains.sprite.ButtonNewGame;
import ru.geekbrains.sprite.ButtonPlay;
import ru.geekbrains.sprite.EnemyShip;
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
    private ExplosionPool explosionPool;

    private EnemiesEmitter enemiesEmitter;

    private Music music;
    private Sound laserSound;
    private Sound bulletSound;
    private Sound explosionSound;

    private ButtonNewGame buttonNewGame;
    private Sprite labelGameOver;

    @Override
    public void show() {
        super.show();
        music = Gdx.audio.newMusic(Gdx.files.internal("sounds/music.mp3"));
        music.setLooping(true);
        music.play();
        img_background = new Texture("textures/bg.png");
        background = new Background(new TextureRegion(img_background));
        atlas = new TextureAtlas("textures/mainAtlas.tpack");

        buttonNewGame = new ButtonNewGame(atlas);
        labelGameOver = new Sprite(atlas.findRegion("message_game_over"));
        labelGameOver.setHeightProportion(0.07f);

        starList = new Star[STAR_COUNT];
        for (int i = 0; i < STAR_COUNT; i++) {
            starList[i] = new Star(atlas);
        }
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.wav"));
        explosionPool = new ExplosionPool(atlas, explosionSound);
        bulletPool = new BulletPool();
        laserSound = Gdx.audio.newSound(Gdx.files.internal("sounds/laser.wav"));
        ship = new SpaceShip(atlas, bulletPool, explosionPool, laserSound);

        bulletSound = Gdx.audio.newSound(Gdx.files.internal("sounds/bullet.wav"));
        enemyPool = new EnemyPool(bulletPool, explosionPool, worldBounds, bulletSound);
        enemiesEmitter = new EnemiesEmitter(atlas, worldBounds, enemyPool);
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);

        background.resize(worldBounds);
        for (Star star : starList) {
            star.resize(worldBounds);
        }

        if (!ship.isDestroyed()) {
            ship.resize(worldBounds);
            buttonNewGame.resize(worldBounds);
            labelGameOver.resize(worldBounds);
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        update(delta);
        checkCollisions();
        deleteAllDestroed();
        draw();
    }

    private void deleteAllDestroed() {
        bulletPool.freeAllDestroedActiveSprites();
        enemyPool.freeAllDestroedActiveSprites();
        explosionPool.freeAllDestroedActiveSprites();
    }

    private void update(float delta) {
        for (Star star : starList) {
            star.update(delta);
        }
        if (!ship.isDestroyed()) {
            ship.update(delta);
            bulletPool.updateActiveSprites(delta);
            enemyPool.updateActiveSprites(delta);
            enemiesEmitter.generate(delta);
        }
        explosionPool.updateActiveSprites(delta);
    }

    private void checkCollisions() {
        if (ship.isDestroyed()) {
            return;
        }

        List<EnemyShip> enemyList = enemyPool.getActiveObjects();
        for (EnemyShip enemy : enemyList) {
            if (enemy.isDestroyed()) {
                continue;
            }
            float minDist = enemy.getHalfWidth() + ship.getHalfWidth();

            if (enemy.pos.dst(ship.pos) <= minDist) {
                enemy.destroy();
                ship.damage(ship.getHealth());
            }

        }

        List<Bullet> bulletList = bulletPool.getActiveObjects();
        for (EnemyShip enemy : enemyList) {
            if (enemy.isDestroyed()) {
                continue;
            }
            for (Bullet bullet : bulletList) {
                if (bullet.getOwner() != ship || bullet.isDestroyed()) {
                    continue;
                }

                if (enemy.isBulletCollision(bullet)) {
                    enemy.damage(bullet.getDamage());
                    bullet.destroy();
                }
            }
        }


        for (Bullet bullet : bulletList) {
            if (bullet.getOwner() == ship || bullet.isDestroyed()) {
                continue;
            }

            if (ship.isBulletCollision(bullet)) {
                ship.damage(bullet.getDamage());
                bullet.destroy();
            }
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
        if (!ship.isDestroyed()) {
            ship.draw(batch);
            bulletPool.drawActiveSprites(batch);
            enemyPool.drawActiveSprites(batch);
        }else {
            buttonNewGame.draw(batch);
            labelGameOver.draw(batch);
        }
        explosionPool.drawActiveSprites(batch);
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
        explosionPool.dispose();
        super.dispose();
    }

    @Override
    public boolean touchDown(Vector2 touch, int pointer) {
        if (!ship.isDestroyed()) {
            ship.touchDown(touch, pointer);
        }else{
            buttonNewGame.touchDown(touch, pointer);
        }
        return false;
    }

    @Override
    public boolean touchUp(Vector2 touch, int pointer) {
        if (!ship.isDestroyed()) {
            ship.touchUp(touch, pointer);
        }else{
            buttonNewGame.touchUp(touch, pointer);
        }
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (!ship.isDestroyed()) {
            ship.keyDown(keycode);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (!ship.isDestroyed()) {
            ship.keyUp(keycode);
        }
        return false;
    }
}
