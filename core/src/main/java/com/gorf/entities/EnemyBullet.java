package com.gorf.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gorf.Constants;
import com.gorf.graphics.SpriteManager;

public class EnemyBullet extends Entity {
    public EnemyBullet(SpriteManager sprites, float startX, float startY, float velX, float velY) {
        this.x = startX;
        this.y = startY;
        this.dx = velX;
        this.dy = velY;
        currentAnimation = sprites.getAnimation("enemy_bullet");
        if (currentAnimation != null) {
            currentFrame = currentAnimation.getKeyFrame(0);
        }
        width = 3 * Constants.SPRITE_SCALE;
        height = 8 * Constants.SPRITE_SCALE;
    }

    @Override
    public void update(float delta) {
        if (!alive) return;
        stateTime += delta;
        x += dx * delta;
        y += dy * delta;

        if (isOffScreen()) {
            alive = false;
        }
    }
}
