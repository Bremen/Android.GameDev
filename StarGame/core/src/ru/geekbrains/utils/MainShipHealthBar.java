package ru.geekbrains.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class MainShipHealthBar {
    private Color healthBarColor = new Color();
    private final float GREEN_ZONE = 0.95f;
    private final float YELLOW_ZONE = 0.7f;
    private final float RED_ZONE = 0.3f;

    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    public void draw(float hp_percent) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        setHealthBarColor(hp_percent);
        shapeRenderer.setColor(healthBarColor);
        float width = 0.5f * hp_percent;
        shapeRenderer.rect(-width/2f, 0.485f, width, 0.01f);
        shapeRenderer.end();
    }

    private void setHealthBarColor(float hp_percent) {
        if (hp_percent >= GREEN_ZONE){
            healthBarColor = healthBarColor.set(0, 1f, 0f, 1f);;
        } else if (hp_percent >= YELLOW_ZONE) {
            float red = (GREEN_ZONE -hp_percent)/(GREEN_ZONE - YELLOW_ZONE);
            healthBarColor.set(red, 1f, 0f, 1f);
        } else if (hp_percent >= RED_ZONE) {
            float green = (hp_percent - RED_ZONE)/(YELLOW_ZONE - RED_ZONE);
            healthBarColor.set(1f, green, 0f, 1f);
        } else {
            healthBarColor.set(1f, 0f, 0f, 1f);
        }
    }

    public void resize (SpriteBatch batch) {
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
