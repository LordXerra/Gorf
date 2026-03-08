package com.gorf.entities.enemies;

import com.badlogic.gdx.math.MathUtils;
import com.gorf.Constants;
import com.gorf.entities.Entity;
import com.gorf.graphics.SpriteManager;

/**
 * Flag Ship boss for Mission 5.
 * Large multi-segment ship moving horizontally at top of screen.
 * Must destroy shield segments to expose reactor core for the kill shot (1000pts).
 */
public class FlagShip extends Entity {
    private float speed;
    private int dir = 1;
    private float fireTimer;
    private float fireCooldown;
    private FireCallback fireCallback;

    public interface FireCallback {
        void onFire(float x, float y);
    }

    public FlagShip(SpriteManager sprites, float difficulty) {
        this.x = Constants.VIRTUAL_WIDTH / 2f;
        this.y = Constants.VIRTUAL_HEIGHT - 80;
        this.speed = 80f * difficulty;
        this.fireCooldown = 2.0f / difficulty;
        this.fireTimer = fireCooldown;

        currentAnimation = sprites.getAnimation("flagship");
        if (currentAnimation != null) {
            currentFrame = currentAnimation.getKeyFrame(0);
        } else {
            currentFrame = sprites.getFrame("flagship_1");
        }
        updateBoundsFromFrame();
    }

    public void setFireCallback(FireCallback callback) {
        this.fireCallback = callback;
    }

    @Override
    public void update(float delta) {
        if (!alive) return;
        stateTime += delta;

        // Move horizontally
        x += speed * dir * delta;

        // Bounce at screen edges
        if (x > Constants.VIRTUAL_WIDTH - 100) {
            dir = -1;
        } else if (x < 100) {
            dir = 1;
        }

        // Periodic firing
        fireTimer -= delta;
        if (fireTimer <= 0) {
            fireTimer = fireCooldown;
            if (fireCallback != null) {
                fireCallback.onFire(x, y - height / 2f);
            }
        }

        if (currentAnimation != null) {
            currentFrame = currentAnimation.getKeyFrame(stateTime);
        }
    }

    public int getScoreValue() {
        return Constants.SCORE_FLAGSHIP_DESTROY;
    }
}
