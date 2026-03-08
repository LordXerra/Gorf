package com.gorf.entities.enemies;

import com.gorf.Constants;
import com.gorf.entities.Entity;
import com.gorf.graphics.SpriteManager;

/**
 * Reactor core of the Flag Ship.
 * Must be hit to destroy the Flag Ship. Worth 1000pts.
 * Protected by ShieldSegments.
 */
public class ReactorCore extends Entity {
    private float parentOffsetX;

    public ReactorCore(SpriteManager sprites, float offsetX) {
        this.parentOffsetX = offsetX;
        currentFrame = sprites.getFrame("reactor_core");
        if (currentFrame == null) {
            currentFrame = sprites.getFrame("shield_1");
        }
        updateBoundsFromFrame();
    }

    public void updatePosition(float parentX, float parentY) {
        this.x = parentX + parentOffsetX;
        this.y = parentY;
    }

    @Override
    public void update(float delta) {
        // Position is updated by the mission
    }

    public int getScoreValue() {
        return Constants.SCORE_FLAGSHIP_DESTROY;
    }
}
