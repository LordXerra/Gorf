package com.gorf.entities.enemies;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gorf.Constants;
import com.gorf.entities.Entity;
import com.gorf.graphics.SpriteManager;

/**
 * Bonus UFO that flies across the top of the screen in Mission 1.
 * Large UFO = 100 points, Small UFO = 200 points.
 */
public class UFO extends Entity {
    private final boolean isSmall;
    private final float speed;

    public UFO(SpriteManager sprites, boolean isSmall, float startX, float startY, int direction) {
        this.isSmall = isSmall;
        this.x = startX;
        this.y = startY;
        this.speed = (isSmall ? 180f : 140f);
        this.dx = speed * direction;

        currentFrame = sprites.getFrame(isSmall ? "ufo_small" : "ufo_large");
        updateBoundsFromFrame();
    }

    @Override
    public void update(float delta) {
        if (!alive) return;
        stateTime += delta;
        x += dx * delta;

        if (isOffScreen()) {
            alive = false;
        }
    }

    public int getScoreValue() {
        return isSmall ? Constants.SCORE_SMALL_UFO : Constants.SCORE_LARGE_UFO;
    }

    public boolean isSmall() { return isSmall; }
}
