package ru.geekbrains.utils;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.math.Rect;
import ru.geekbrains.math.Rnd;
import ru.geekbrains.pool.EnemyPool;
import ru.geekbrains.sprite.EnemyShip;

public class EnemiesEmitter {
    private static final float ENEMY_SMALL_HEIGHT = 0.1f;
    private static final float ENEMY_SMALL_BULLET_HEIGHT = 0.01f;
    private static final float ENEMY_SMALL_BULLET_VY = -0.3f;
    private static final int ENEMY_SMALL_DAMAGE = 1;
    private static final float ENEMY_SMALL_RELOAD_INTERVAL = 2f;
    private static final int ENEMY_SMALL_HP = 1;
    private static final float ENEMY_SMALL_CHANCE = 0.5f;
    private TextureRegion[] enemySmallRegion;
    private Vector2 enemySmallV = new Vector2(0f, -0.15f);

    private static final float ENEMY_MIDDLE_HEIGHT = 0.12f;
    private static final float ENEMY_MIDDLE_BULLET_HEIGHT = 0.02f;
    private static final float ENEMY_MIDDLE_BULLET_VY = -0.2f;
    private static final int ENEMY_MIDDLE_DAMAGE = 3;
    private static final float ENEMY_MIDDLE_RELOAD_INTERVAL = 1.5f;
    private static final int ENEMY_MIDDLE_HP = 3;
    private static final float ENEMY_MIDDLE_CHANCE = 0.35f;
    private TextureRegion[] enemyMiddleRegion;
    private Vector2 enemyMiddleV = new Vector2(0f, -0.1f);

    private static final float ENEMY_BIG_HEIGHT = 0.15f;
    private static final float ENEMY_BIG_BULLET_HEIGHT = 0.04f;
    private static final float ENEMY_BIG_BULLET_VY = -0.1f;
    private static final int ENEMY_BIG_DAMAGE = 9;
    private static final float ENEMY_BIG_RELOAD_INTERVAL = 1f;
    private static final int ENEMY_BIG_HP = 30;
    private static final float ENEMY_BIG_CHANCE = 0.15f;
    private TextureRegion[] enemyBigRegion;
    private Vector2 enemyBigV = new Vector2(0f, -0.03f);
    
    private enum EnemyType{SMALL, MIDDLE, BIG};

    private Rect worldBounds;

    private float generateInterval = 4f;
    private float generateTimer;

    private TextureRegion bulletRegion;

    private EnemyPool enemyPool;

    private int level;

    public int getLevel() {
        return level;
    }

    public EnemiesEmitter(TextureAtlas atlas, Rect worldBounds, EnemyPool enemyPool) {
        this.worldBounds = worldBounds;
        this.enemyPool = enemyPool;        
        this.bulletRegion = atlas.findRegion("bulletEnemy");

        TextureRegion textureRegion0 = atlas.findRegion("enemy0");
        this.enemySmallRegion = Regions.split(textureRegion0, 1, 2, 2);

        TextureRegion textureRegion1 = atlas.findRegion("enemy1");
        this.enemyMiddleRegion = Regions.split(textureRegion1, 1, 2, 2);

        TextureRegion textureRegion2 = atlas.findRegion("enemy2");
        this.enemyBigRegion = Regions.split(textureRegion2, 1, 2, 2);
    }

    public void generate(float delta, int frags) {
        level = frags / 10 + 1;
        generateTimer += delta;
        if (generateTimer >= generateInterval) {
            generateTimer = 0f;
            EnemyShip enemy = enemyPool.obtain();

            setParametres(enemy, RandomEnemyType());

            enemy.pos.x = Rnd.nextFloat(worldBounds.getLeft() + enemy.getHalfWidth(), worldBounds.getRight() - enemy.getHalfWidth());
            enemy.setBottom(worldBounds.getTop());
        }
    }

    private EnemyType RandomEnemyType() {
        EnemyType type;
        float choice = Rnd.nextFloat(0, ENEMY_SMALL_CHANCE + ENEMY_MIDDLE_CHANCE + ENEMY_BIG_CHANCE);

        if (choice <= ENEMY_SMALL_CHANCE){
            type = EnemyType.SMALL;
        }
        else if (choice <= ENEMY_MIDDLE_CHANCE + ENEMY_SMALL_CHANCE) {
            type = EnemyType.MIDDLE;
        }
        else {
            type = EnemyType.BIG;
        }

        return type;
    }

    private void setParametres(EnemyShip enemy, EnemyType type) {
        switch (type){
            case SMALL:
                enemy.set(
                        enemySmallRegion,
                        enemySmallV,
                        bulletRegion,
                        ENEMY_SMALL_BULLET_HEIGHT,
                        ENEMY_SMALL_BULLET_VY * level,
                        ENEMY_SMALL_DAMAGE  * level,
                        ENEMY_SMALL_RELOAD_INTERVAL / (float) level,
                        ENEMY_SMALL_HEIGHT,
                        ENEMY_SMALL_HP
                );
                break;
            case MIDDLE:
                enemy.set(
                        enemyMiddleRegion,
                        enemyMiddleV,
                        bulletRegion,
                        ENEMY_MIDDLE_BULLET_HEIGHT,
                        ENEMY_MIDDLE_BULLET_VY  * level,
                        ENEMY_MIDDLE_DAMAGE,
                        ENEMY_MIDDLE_RELOAD_INTERVAL / (float) level,
                        ENEMY_MIDDLE_HEIGHT,
                        ENEMY_MIDDLE_HP
                );
                break;
            case BIG:
                enemy.set(
                        enemyBigRegion,
                        enemyBigV,
                        bulletRegion,
                        ENEMY_BIG_BULLET_HEIGHT,
                        ENEMY_BIG_BULLET_VY  * level,
                        ENEMY_BIG_DAMAGE,
                        ENEMY_BIG_RELOAD_INTERVAL  / (float) level,
                        ENEMY_BIG_HEIGHT,
                        ENEMY_BIG_HP
                );
                break;
        }
    }
}
