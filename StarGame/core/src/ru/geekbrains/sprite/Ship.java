package ru.geekbrains.sprite;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.base.Sprite;
import ru.geekbrains.math.Rect;
import ru.geekbrains.pool.BulletPool;
import ru.geekbrains.pool.ExplosionPool;

public class Ship extends Sprite {
    protected Vector2 v = new Vector2();
    protected Vector2 bulletV = new Vector2();

    protected Rect worldBounds;
    protected BulletPool bulletPool;
    protected TextureRegion bulletRegion;
    protected float bulletHeight;
    protected int damage;
    protected int health;

    protected ExplosionPool explosionPool;

    protected Sound shootSound;

    protected float reloadInterval;
    protected float reloadTimer;

    private float damageAnimateInterval = 0.1f;
    private float damageAnimateTimer;

    public Ship(TextureRegion region, int rows, int cols, int frames) {
        super(region, rows, cols, frames);
    }

    public Ship() {
    }

    @Override
    public void resize(Rect worldBounds) {
        this.worldBounds = worldBounds;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        pos.mulAdd(v, delta);

        damageAnimateTimer += delta;
        if (damageAnimateTimer >= damageAnimateInterval) {
            frame = 0;
        }
    }

    public void damage(int damage) {
        frame = 1;
        damageAnimateTimer = 0f;
        health -= damage;
        if (health <= 0 ) {
            destroy();
        }
    }

    public void shoot() {
        Bullet bullet = bulletPool.obtain();
        bullet.set(this, bulletRegion, pos, bulletV, bulletHeight, worldBounds, damage);
        shootSound.play();
    }

    public void boom() {
        Explosion explosion = explosionPool.obtain();
        explosion.set(this.getHeight(), this.pos);
    }

    public int getHealth() {
        return health;
    }

    @Override
    public void destroy() {
        super.destroy();
        boom();
    }
}
