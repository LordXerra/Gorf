package com.gorf.entities.enemies;

import com.badlogic.gdx.math.MathUtils;
import com.gorf.Constants;
import com.gorf.entities.Entity;
import com.gorf.graphics.SpriteManager;

/**
 * Dive bomber enemy for Mission 2 (Laser Attack).
 * Starts in formation, periodically peels off to dive at the player.
 */
public class DiveBomber extends Entity {
    private enum State { FORMATION, DIVING, RETURNING }

    private State state = State.FORMATION;
    private float formationX, formationY;
    private float targetX, targetY;
    private float diveSpeed;
    private float diveTimer;
    private float diveCooldown;
    private FireCallback fireCallback;

    public interface FireCallback {
        void onFire(float x, float y, float targetX, float targetY);
    }

    public DiveBomber(SpriteManager sprites, float formX, float formY, float difficulty) {
        this.formationX = formX;
        this.formationY = formY;
        this.x = formX;
        this.y = formY;
        this.diveSpeed = 200f * difficulty;
        this.diveCooldown = MathUtils.random(3f, 7f) / difficulty;
        this.diveTimer = MathUtils.random(1f, diveCooldown);

        currentAnimation = sprites.getAnimation("dive_bomber");
        if (currentAnimation != null) {
            currentFrame = currentAnimation.getKeyFrame(0);
        } else {
            currentFrame = sprites.getFrame("dive_bomber_1");
        }
        updateBoundsFromFrame();
    }

    public void setFireCallback(FireCallback callback) {
        this.fireCallback = callback;
    }

    public void setPlayerTarget(float px, float py) {
        this.targetX = px;
        this.targetY = py;
    }

    public void updateFormationPos(float fx, float fy) {
        this.formationX = fx;
        this.formationY = fy;
    }

    @Override
    public void update(float delta) {
        if (!alive) return;
        stateTime += delta;

        switch (state) {
            case FORMATION:
                // Drift toward formation position
                x += (formationX - x) * 3f * delta;
                y += (formationY - y) * 3f * delta;

                diveTimer -= delta;
                if (diveTimer <= 0) {
                    state = State.DIVING;
                    // Fire at player when starting dive
                    if (fireCallback != null) {
                        fireCallback.onFire(x, y, targetX, targetY);
                    }
                }
                break;

            case DIVING:
                // Dive toward player position
                float dx = targetX - x;
                float dy = targetY - y;
                float dist = (float) Math.sqrt(dx * dx + dy * dy);

                if (dist > 5f) {
                    x += (dx / dist) * diveSpeed * delta;
                    y += (dy / dist) * diveSpeed * delta;
                }

                // If past bottom of screen or reached target, return
                if (y < -20 || dist < 30f) {
                    state = State.RETURNING;
                }
                break;

            case RETURNING:
                // Return to formation
                float rx = formationX - x;
                float ry = formationY - y;
                float rdist = (float) Math.sqrt(rx * rx + ry * ry);

                if (rdist > 5f) {
                    x += (rx / rdist) * diveSpeed * 0.8f * delta;
                    y += (ry / rdist) * diveSpeed * 0.8f * delta;
                } else {
                    state = State.FORMATION;
                    diveTimer = diveCooldown;
                }
                break;
        }
    }

    public int getScoreValue() {
        return Constants.SCORE_DIVE_BOMBER;
    }
}
