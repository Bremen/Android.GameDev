package ru.geekbrains.sprite;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.base.Sprite;
import ru.geekbrains.math.Rect;
import ru.geekbrains.math.Rnd;

public class Star extends Sprite {

    protected Vector2 v;
    private Rect worldBounds;
    protected float starHeight;
    protected float starHeightMax;
    protected float blinkSizeStep;
    private Color color;

    public Star(TextureAtlas atlas) {
        super(atlas.findRegion("star"));
        float vX = Rnd.nextFloat(-0.005f, 0.005f);
        float vY = Rnd.nextFloat(-0.3f, -0.1f);
        v = new Vector2(vX, vY);
        starHeightMax = Rnd.nextFloat(0.008f, 0.015f);
        blinkSizeStep = Rnd.nextFloat(-0.001f, 0.001f);
        starHeight = starHeightMax;
        setHeightProportion(starHeightMax);

        color = new Color(Rnd.nextFloat(0.2f, 1f), Rnd.nextFloat(0.2f, 1f), Rnd.nextFloat(0.2f, 1f), 1f);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        pos.mulAdd(v, delta);
        checkAndHandleBounds();
        if (starHeight >= starHeightMax || starHeight <= 0.005f){
            blinkSizeStep *= -1f;
        }
        starHeight += blinkSizeStep;
        setHeightProportion(starHeight);
    }

    @Override
    public void resize(Rect worldBounds) {
        this.worldBounds = worldBounds;
        float posX = Rnd.nextFloat(worldBounds.getLeft(), worldBounds.getRight());
        float posY = Rnd.nextFloat(worldBounds.getBottom(), worldBounds.getTop());
        pos.set(posX,posY);
    }

    protected void checkAndHandleBounds(){
        if(getLeft() < worldBounds.getLeft()){
            setLeft(worldBounds.getRight());
        }
        else if(getRight() > worldBounds.getRight()){
            setRight(worldBounds.getLeft());
        }
        if(getBottom() < worldBounds.getBottom()){
            setBottom(worldBounds.getTop());
        }
    }

    public Color getColor() {
        return color;
    }
}
