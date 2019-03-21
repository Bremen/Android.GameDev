package ru.geekbrains.sprite;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.base.Sprite;
import ru.geekbrains.math.Rect;
import ru.geekbrains.utils.Regions;

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

public class EnemyShip extends Sprite {
    private Vector2 v;
    private int health;
    private int damage;
    private Vector2 bullet_v0;
    private Rect worldBounds;

    public EnemyShip() {
        v = new Vector2();
        bullet_v0 = new Vector2();
    }

    public void set(
            int health,
            TextureRegion region,
            Vector2 pos0,
            Vector2 v0,
            Vector2 bullet_v0,
            float height,
            Rect worldBounds,
            int damage
    ) {
        this.health = health;
        this.regions = Regions.split(region, 1, 2, 2);
        this.pos.set(pos0);
        this.bullet_v0.set(bullet_v0);
        this.v.set(v0);
        setHeightProportion(height);
        this.worldBounds = worldBounds;
        this.damage = damage;
    }

    @Override
    public void resize(Rect worldBounds) {
        this.worldBounds = worldBounds;
        setBottom(worldBounds.getTop() - 0.05f);
    }

    public void update(float delta) {
        pos.mulAdd(v, delta);
        if (getRight() > worldBounds.getRight()) {
            setRight(worldBounds.getRight());
            stop();
        }
        if (getLeft() < worldBounds.getLeft()) {
            setLeft(worldBounds.getLeft());
            stop();
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
    }

    private void moveRight(){
        v.set(v);
    }

    private void moveLeft(){
        v.set(v).rotate(180);
    }

    private void moveDown(){
        v.set(v).rotate(90);
    }

    private void stop() {
        v.setZero();
    }

    public void shoot() {
    }
}
