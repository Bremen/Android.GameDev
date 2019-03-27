package ru.geekbrains.sprite;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.math.Rect;
import ru.geekbrains.pool.BulletPool;
import ru.geekbrains.pool.ExplosionPool;

/*
Необходимо реализовать настраиваемый класс для вражеского корабля.
Так как объекты вражеских кораблей будут использоваться повторно,
то необходимо сделать возможность задавать кораблю
-текстуру,
-скорость,
-количество жизней,
-наносимый урон,
-скорость пули корабля и
-размер.
Реализовать для корабля все необходимые методы (update, draw, resize).
Отображать вражеский корабль пока не нужно, так как он будет инициализироваться из пула объектов
(тема следующего вебинара)
 */

public class EnemyShip extends Ship {

    private enum State{DESCENT, FIGHT};
    private State state;
    private Vector2 v0 = new Vector2();
    private Vector2 descentV = new Vector2(0, -0.15f);

    public EnemyShip(BulletPool bulletPool, ExplosionPool explosionPool, Sound shootSound, Rect worldBounds) {
        this.bulletPool = bulletPool;
        this.explosionPool = explosionPool;
        this.shootSound = shootSound;
        this.worldBounds = worldBounds;
    }

    public void set(
            TextureRegion[] regions,
            Vector2 v0,
            TextureRegion bulletRegion,
            float bulletHeight,
            float bulletVY,
            int damage,
            float reloadInterval,
            float height,
            int hp
    ) {
        this.regions = regions;
        this.v0.set(v0);
        this.bulletRegion = bulletRegion;
        this.bulletHeight = bulletHeight;
        this.bulletV.set(0, bulletVY);
        this.damage = damage;
        this.reloadInterval = reloadInterval;
        this.health = hp;
        setHeightProportion(height);
        reloadTimer = reloadInterval;
        this.v.set(descentV);
        state = State.DESCENT;
    }

    @Override
    public void resize(Rect worldBounds) {
        this.worldBounds = worldBounds;
        setBottom(worldBounds.getTop() - 0.05f);
    }

    public void update(float delta) {
        super.update(delta);
        switch (state) {
            case DESCENT:
                if (getTop() <= worldBounds.getTop()) {
                    v.set(v0);
                    state = State.FIGHT;
                }
                break;
            case FIGHT:
                reloadTimer += delta;
                if (reloadTimer >= reloadInterval) {
                    reloadTimer = 0f;
                    shoot();
                }
                if (getBottom() <= worldBounds.getBottom()) {
                    this.destroy();
                }
                break;
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
    }

    public  boolean isBulletCollision(Rect bullet) {
        return !(   bullet.getRight() < getLeft()
                ||  bullet.getLeft() > getRight()
                ||  bullet.getBottom() > getTop()
                ||  bullet.getTop() < pos.y
                );

    }
}
