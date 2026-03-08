package com.gorf.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.gorf.Constants;

public abstract class Entity {
    public float x, y;
    public float width, height;
    public float dx, dy;
    public boolean alive = true;
    public float stateTime = 0;

    protected Animation<TextureRegion> currentAnimation;
    protected TextureRegion currentFrame;

    public abstract void update(float delta);

    public void render(SpriteBatch batch) {
        if (!alive) return;
        if (currentAnimation != null) {
            currentFrame = currentAnimation.getKeyFrame(stateTime);
        }
        if (currentFrame != null) {
            float drawW = currentFrame.getRegionWidth() * Constants.SPRITE_SCALE;
            float drawH = currentFrame.getRegionHeight() * Constants.SPRITE_SCALE;
            batch.draw(currentFrame, x - drawW / 2f, y - drawH / 2f, drawW, drawH);
        }
    }

    public Rectangle getBounds(Rectangle out) {
        out.set(x - width / 2f, y - height / 2f, width, height);
        return out;
    }

    protected void updateBoundsFromFrame() {
        if (currentFrame != null) {
            width = currentFrame.getRegionWidth() * Constants.SPRITE_SCALE * 0.7f;
            height = currentFrame.getRegionHeight() * Constants.SPRITE_SCALE * 0.7f;
        }
    }

    public boolean isOffScreen() {
        return x < -50 || x > Constants.VIRTUAL_WIDTH + 50
            || y < -50 || y > Constants.VIRTUAL_HEIGHT + 50;
    }
}
