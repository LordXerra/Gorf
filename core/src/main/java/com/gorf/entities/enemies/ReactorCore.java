package com.gorf.entities.enemies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gorf.Constants;
import com.gorf.entities.Entity;
import com.gorf.graphics.SpriteManager;

/**
 * Reactor core of the Flag Ship.
 * Invisible collision target overlapping the flagship center.
 * Must be hit to destroy the Flag Ship. Worth 1000pts.
 * Protected by ShieldSegments.
 */
public class ReactorCore extends Entity {
    private float parentOffsetX;

    public ReactorCore(SpriteManager sprites, float offsetX) {
        this.parentOffsetX = offsetX;
        // Use flagship frame just to set collision bounds, but don't render it
        currentFrame = sprites.getFrame("flagship_1");
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

    @Override
    public void render(SpriteBatch batch) {
        // Invisible - the flagship sprite covers this area visually
    }

    public int getScoreValue() {
        return Constants.SCORE_FLAGSHIP_DESTROY;
    }
}
