package com.gorf.entities.enemies;

import com.badlogic.gdx.math.MathUtils;
import com.gorf.Constants;
import com.gorf.entities.Entity;
import com.gorf.graphics.SpriteManager;

/**
 * Falling debris from destroyed Flag Ship shield segments.
 * Falls downward with slight horizontal drift. 150pts if shot.
 */
public class Debris extends Entity {
    private float fallSpeed;
    private float drift;
    private float rotation;
    private float rotationSpeed;

    public Debris(SpriteManager sprites, float startX, float startY) {
        this.x = startX;
        this.y = startY;
        this.fallSpeed = MathUtils.random(80f, 160f);
        this.drift = MathUtils.random(-30f, 30f);
        this.rotationSpeed = MathUtils.random(-200f, 200f);

        int debrisType = MathUtils.random(1, 4);
        currentFrame = sprites.getFrame("debris_" + debrisType);
        if (currentFrame == null) {
            currentFrame = sprites.getFrame("debris_1");
        }
        updateBoundsFromFrame();
    }

    @Override
    public void update(float delta) {
        if (!alive) return;
        stateTime += delta;

        y -= fallSpeed * delta;
        x += drift * delta;
        rotation += rotationSpeed * delta;

        if (y < -30) {
            alive = false;
        }
    }

    @Override
    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        if (!alive || currentFrame == null) return;
        float scale = Constants.SPRITE_SCALE;
        float w = currentFrame.getRegionWidth() * scale;
        float h = currentFrame.getRegionHeight() * scale;
        batch.draw(currentFrame, x - w / 2f, y - h / 2f,
            w / 2f, h / 2f, w, h, 1, 1, rotation);
    }

    public int getScoreValue() {
        return Constants.SCORE_DEBRIS;
    }
}
