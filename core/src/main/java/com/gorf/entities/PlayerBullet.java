package com.gorf.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gorf.Constants;
import com.gorf.graphics.SpriteManager;

public class PlayerBullet extends Entity {
    public PlayerBullet(SpriteManager sprites, float startX, float startY) {
        this.x = startX;
        this.y = startY;
        this.dy = Constants.BULLET_SPEED;
        this.dx = 0;
        currentFrame = sprites.getFrame("player_bullet");
        if (currentFrame != null) {
            // Bullet is very thin, give it a reasonable hitbox
            width = 4 * Constants.SPRITE_SCALE;
            height = currentFrame.getRegionHeight() * Constants.SPRITE_SCALE * 0.7f;
        } else {
            width = 4;
            height = 16;
        }
    }

    @Override
    public void update(float delta) {
        if (!alive) return;
        stateTime += delta;
        y += dy * delta;
        x += dx * delta;

        // Kill if off top of screen
        if (y > Constants.VIRTUAL_HEIGHT + 20) {
            alive = false;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!alive) return;
        if (currentFrame != null) {
            float drawW = currentFrame.getRegionWidth() * Constants.SPRITE_SCALE;
            float drawH = currentFrame.getRegionHeight() * Constants.SPRITE_SCALE;
            batch.draw(currentFrame, x - drawW / 2f, y - drawH / 2f, drawW, drawH);
        }
    }
}
