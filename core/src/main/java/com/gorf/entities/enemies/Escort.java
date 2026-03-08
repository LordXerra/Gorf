package com.gorf.entities.enemies;

import com.badlogic.gdx.math.MathUtils;
import com.gorf.Constants;
import com.gorf.entities.Entity;
import com.gorf.graphics.SpriteManager;

/**
 * Escort ship flanking the Flag Ship in Mission 5.
 * Flies alongside the flagship and fires at the player. 100pts.
 */
public class Escort extends Entity {
    private float offsetX, offsetY;
    private float fireTimer;
    private float fireCooldown;
    private FireCallback fireCallback;

    public interface FireCallback {
        void onFire(float x, float y, float targetX, float targetY);
    }

    public Escort(SpriteManager sprites, float offX, float offY, float difficulty) {
        this.offsetX = offX;
        this.offsetY = offY;
        this.fireCooldown = MathUtils.random(2f, 5f) / difficulty;
        this.fireTimer = MathUtils.random(1f, fireCooldown);

        currentFrame = sprites.getFrame("escort_1");
        updateBoundsFromFrame();
    }

    public void setFireCallback(FireCallback callback) {
        this.fireCallback = callback;
    }

    public void updatePosition(float parentX, float parentY, float targetX, float targetY) {
        this.x = parentX + offsetX;
        this.y = parentY + offsetY;
    }

    @Override
    public void update(float delta) {
        if (!alive) return;
        stateTime += delta;

        fireTimer -= delta;
        if (fireTimer <= 0) {
            fireTimer = fireCooldown;
            if (fireCallback != null) {
                // Fire at a general downward angle
                fireCallback.onFire(x, y, x + MathUtils.random(-50f, 50f), -10f);
            }
        }
    }

    public int getScoreValue() {
        return Constants.SCORE_ESCORT;
    }
}
