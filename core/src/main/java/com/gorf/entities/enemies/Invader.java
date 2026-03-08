package com.gorf.entities.enemies;

import com.badlogic.gdx.math.MathUtils;
import com.gorf.Constants;
import com.gorf.entities.Entity;
import com.gorf.graphics.SpriteManager;

/**
 * Space Invaders-style enemy for Mission 1 (Astro Battles).
 * Moves in a grid formation: lateral movement, then drops down at edges.
 * Movement is controlled by AstroBattlesMission (formation-wide).
 */
public class Invader extends Entity {
    private int gridCol, gridRow;
    private float fireTimer;
    private float fireCooldown;
    private FireCallback fireCallback;
    private final int type; // 0-2 for different invader sprites

    public interface FireCallback {
        void onFire(float x, float y);
    }

    public Invader(SpriteManager sprites, int gridCol, int gridRow, int type, float difficulty) {
        this.gridCol = gridCol;
        this.gridRow = gridRow;
        this.type = type;

        // Pick animation based on type
        String animName = switch (type) {
            case 0 -> "small_invader_1";
            case 1 -> "small_invader_2";
            default -> "small_invader_3";
        };
        currentAnimation = sprites.getAnimation(animName);
        if (currentAnimation != null) {
            currentFrame = currentAnimation.getKeyFrame(0);
        } else {
            currentFrame = sprites.getFrame("invader_1");
        }
        updateBoundsFromFrame();

        // Random fire cooldown, faster with higher difficulty
        fireCooldown = MathUtils.random(3f, 8f) / difficulty;
        fireTimer = MathUtils.random(0, fireCooldown);
    }

    public void setFireCallback(FireCallback callback) {
        this.fireCallback = callback;
    }

    @Override
    public void update(float delta) {
        if (!alive) return;
        stateTime += delta;

        // Firing logic
        fireTimer -= delta;
        if (fireTimer <= 0 && fireCallback != null) {
            fireCallback.onFire(x, y - height / 2f);
            fireTimer = fireCooldown;
        }
    }

    public int getGridCol() { return gridCol; }
    public int getGridRow() { return gridRow; }
    public int getType() { return type; }

    public int getScoreValue() {
        return Constants.SCORE_INVADER;
    }
}
