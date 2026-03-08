package com.gorf.entities.enemies;

import com.badlogic.gdx.math.MathUtils;
import com.gorf.Constants;
import com.gorf.entities.Entity;
import com.gorf.graphics.SpriteManager;

/**
 * Laser cannon enemy for Mission 2 (Laser Attack).
 * Fires long vertical laser beams that act as barriers.
 */
public class LaserCannon extends Entity {
    private float formationX, formationY;
    private float fireTimer;
    private float fireCooldown;
    private LaserFireCallback callback;

    public interface LaserFireCallback {
        void onFireLaser(float x, float y);
    }

    public LaserCannon(SpriteManager sprites, float formX, float formY, float difficulty) {
        this.formationX = formX;
        this.formationY = formY;
        this.x = formX;
        this.y = formY;

        fireCooldown = MathUtils.random(4f, 8f) / difficulty;
        fireTimer = MathUtils.random(1f, fireCooldown);

        currentAnimation = sprites.getAnimation("laser_cannon");
        if (currentAnimation != null) {
            currentFrame = currentAnimation.getKeyFrame(0);
        } else {
            currentFrame = sprites.getFrame("laser_cannon_1");
        }
        updateBoundsFromFrame();
    }

    public void setFireCallback(LaserFireCallback callback) {
        this.callback = callback;
    }

    public void updateFormationPos(float fx, float fy) {
        this.formationX = fx;
        this.formationY = fy;
    }

    @Override
    public void update(float delta) {
        if (!alive) return;
        stateTime += delta;

        // Drift toward formation position
        x += (formationX - x) * 3f * delta;
        y += (formationY - y) * 3f * delta;

        fireTimer -= delta;
        if (fireTimer <= 0) {
            fireTimer = fireCooldown;
            if (callback != null) {
                callback.onFireLaser(x, y - height / 2f);
            }
        }
    }

    public int getScoreValue() {
        return Constants.SCORE_LASER_CANNON;
    }
}
