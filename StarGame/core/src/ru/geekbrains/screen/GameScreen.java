package ru.geekbrains.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

import java.util.List;

import ru.geekbrains.base.Base2DScreen;
import ru.geekbrains.base.Font;
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
import ru.geekbrains.sprite.MessageGameOver;
import ru.geekbrains.sprite.SpaceShip;
import ru.geekbrains.sprite.Star;
import ru.geekbrains.sprite.TrackingStar;
import ru.geekbrains.utils.EnemiesEmitter;

public class GameScreen extends Base2DScreen {

    private static final float FONT_SIZE = 0.02f;
    private static final String FRAGS = "Frags: ";
    private static final String HP = "HP: ";
    private static final String LEVEL = "Level: ";
    private static final int STAR_COUNT = 64;

    private enum State {PLAYING, PAUSE, GAME_OVER}

    private Texture img_background;
    private Background background;
    private TextureAtlas atlas;
    private TrackingStar starList[];
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
    private MessageGameOver labelGameOver;

    private int frags;

    private State state;
    private State stateBuf;

    private Font font;
    private StringBuilder sbFrags;
    private StringBuilder sbHP;
    private StringBuilder sbLevel;

    @Override
    public void show() {
        super.show();
        music = Gdx.audio.newMusic(Gdx.files.internal("sounds/music.mp3"));
        music.setLooping(true);
        music.play();
        img_background = new Texture("textures/bg.png");
        background = new Background(new TextureRegion(img_background));
        atlas = new TextureAtlas("textures/mainAtlas.tpack");

        buttonNewGame = new ButtonNewGame(atlas, this);
        labelGameOver = new MessageGameOver(atlas);

        explosionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.wav"));
        explosionPool = new ExplosionPool(atlas, explosionSound);
        bulletPool = new BulletPool();
        laserSound = Gdx.audio.newSound(Gdx.files.internal("sounds/laser.wav"));
        ship = new SpaceShip(atlas, bulletPool, explosionPool, laserSound);

        starList = new TrackingStar[STAR_COUNT];
        for (int i = 0; i < STAR_COUNT; i++) {
            starList[i] = new TrackingStar(atlas, ship.getV());
        }

        bulletSound = Gdx.audio.newSound(Gdx.files.internal("sounds/bullet.wav"));
        enemyPool = new EnemyPool(bulletPool, explosionPool, worldBounds, bulletSound);
        enemiesEmitter = new EnemiesEmitter(atlas, worldBounds, enemyPool);

        font = new Font("font/font.fnt", "font/font.png");
        font.setSize(FONT_SIZE);
        sbFrags = new StringBuilder();
        sbHP = new StringBuilder();
        sbLevel = new StringBuilder();

        startNewGame();
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);

        background.resize(worldBounds);
        for (Star star : starList) {
            star.resize(worldBounds);
        }

        switch (state) {
            case PLAYING:
                ship.resize(worldBounds);
                break;
            case GAME_OVER:
                buttonNewGame.resize(worldBounds);
                labelGameOver.resize(worldBounds);
                break;
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
        if (state == State.PLAYING) {
            ship.update(delta);
            bulletPool.updateActiveSprites(delta);
            enemyPool.updateActiveSprites(delta);
            enemiesEmitter.generate(delta, frags);
        }
        explosionPool.updateActiveSprites(delta);
    }

    private void checkCollisions() {
        if (state == State.GAME_OVER) {
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
                state = State.GAME_OVER;
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

            if (enemy.isDestroyed()) {
                frags++;
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

            if (ship.isDestroyed()) {
                state = State.GAME_OVER;
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
        if (state == State.PLAYING) {
            ship.draw(batch);
            bulletPool.drawActiveSprites(batch);
            enemyPool.drawActiveSprites(batch);
        }else if (state == State.GAME_OVER) {
            buttonNewGame.draw(batch);
            labelGameOver.draw(batch);
        }
        explosionPool.drawActiveSprites(batch);
        printInfo();
        batch.end();
    }

    public void printInfo() {
        sbFrags.setLength(0);
        sbHP.setLength(0);
        sbLevel.setLength(0);
        font.draw(batch, sbFrags.append(FRAGS).append(frags), worldBounds.getLeft(), worldBounds.getTop());
        font.draw(batch, sbHP.append(HP).append(ship.getHealth()), worldBounds.pos.x, worldBounds.getTop(), Align.center);
        font.draw(batch, sbLevel.append(LEVEL).append(enemiesEmitter.getLevel()), worldBounds.getRight(), worldBounds.getTop(), Align.right);
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
        font.dispose();
        super.dispose();
    }

    @Override
    public boolean touchDown(Vector2 touch, int pointer) {
        if (state == State.PLAYING) {
            ship.touchDown(touch, pointer);
        }else{
            buttonNewGame.touchDown(touch, pointer);
        }
        return false;
    }

    @Override
    public boolean touchUp(Vector2 touch, int pointer) {
        if (state == State.PLAYING) {
            ship.touchUp(touch, pointer);
        }else{
            buttonNewGame.touchUp(touch, pointer);
        }
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (state == State.PLAYING) {
            ship.keyDown(keycode);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (state == State.PLAYING) {
            ship.keyUp(keycode);
        }
        return false;
    }

    public void startNewGame() {
        state = State.PLAYING;
        frags = 0;

        bulletPool.freeAllActiveObjects();
        enemyPool.freeAllActiveObjects();
        explosionPool.freeAllActiveObjects();

        ship.startNewGame(worldBounds);
    }

    @Override
    public void pause() {
        super.pause();

        stateBuf = state;
        state = State.PAUSE;
    }

    @Override
    public void resume() {
        super.resume();

        state = stateBuf;
    }
}
