package com.gorf.entities.enemies;

import com.gorf.Constants;
import com.gorf.entities.Entity;
import com.gorf.graphics.SpriteManager;

/**
 * Destructible shield segment protecting the Flag Ship's reactor core.
 * 20pts each. When destroyed, may spawn falling debris.
 */
public class ShieldSegment extends Entity {
    private float parentOffsetX;
    private DebrisCallback debrisCallback;

    public interface DebrisCallback {
        void onDebris(float x, float y);
    }

    public ShieldSegment(SpriteManager sprites, int segmentIndex, float offsetX) {
        this.parentOffsetX = offsetX;

        String frameName = "shield_" + Math.min(segmentIndex + 1, 11);
        currentFrame = sprites.getFrame(frameName);
        if (currentFrame == null) {
            currentFrame = sprites.getFrame("shield_1");
        }
        updateBoundsFromFrame();
    }

    public void setDebrisCallback(DebrisCallback callback) {
        this.debrisCallback = callback;
    }

    public void updatePosition(float parentX, float parentY) {
        this.x = parentX + parentOffsetX;
        this.y = parentY;
    }

    @Override
    public void update(float delta) {
        // Position is updated by the mission via updatePosition
    }

    public void onDestroyed() {
        alive = false;
        if (debrisCallback != null) {
            debrisCallback.onDebris(x, y);
        }
    }

    public int getScoreValue() {
        return Constants.SCORE_HULL_HIT;
    }
}
