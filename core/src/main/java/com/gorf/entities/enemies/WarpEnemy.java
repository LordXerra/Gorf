package com.gorf.entities.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.gorf.Constants;
import com.gorf.entities.Entity;
import com.gorf.graphics.SpriteManager;

/**
 * Warp enemy for Mission 4 (Space Warp).
 * Spawns at screen center and spirals outward, growing in size.
 * 100pts each.
 */
public class WarpEnemy extends Entity {
    private float angle;
    private float radius;
    private float angularSpeed;
    private float radialSpeed;
    private float centerX, centerY;
    private float scale;

    private final Animation<TextureRegion> growAnim;

    public WarpEnemy(SpriteManager sprites, float startAngle, float difficulty) {
        this.centerX = Constants.VIRTUAL_WIDTH / 2f;
        this.centerY = Constants.VIRTUAL_HEIGHT / 2f;
        this.angle = startAngle;
        this.radius = 10f;
        this.angularSpeed = 3f * difficulty;
        this.radialSpeed = 60f * difficulty;
        this.scale = 0.5f;

        this.x = centerX;
        this.y = centerY;

        growAnim = sprites.getAnimation("warp_enemy_grow");
        if (growAnim != null) {
            currentAnimation = growAnim;
            currentFrame = growAnim.getKeyFrame(0);
        } else {
            currentFrame = sprites.getFrame("warp_enemy_0");
        }
        updateBoundsFromFrame();
    }

    @Override
    public void update(float delta) {
        if (!alive) return;
        stateTime += delta;

        // Spiral outward
        angle += angularSpeed * delta;
        radius += radialSpeed * delta;

        // Scale grows with radius
        scale = 0.5f + (radius / 400f) * 2.5f;
        scale = Math.min(scale, 3f);

        // Position on spiral
        x = centerX + MathUtils.cos(angle) * radius;
        y = centerY + MathUtils.sin(angle) * radius;

        // Update animation frame based on growth
        if (growAnim != null) {
            // Use radius to determine which frame (progresses through growth anim)
            float animTime = radius / 80f; // spread frames over radius growth
            currentFrame = growAnim.getKeyFrame(animTime);
        }

        // Die when off screen
        if (x < -50 || x > Constants.VIRTUAL_WIDTH + 50 ||
            y < -50 || y > Constants.VIRTUAL_HEIGHT + 50) {
            alive = false;
        }
    }

    @Override
    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        if (!alive || currentFrame == null) return;
        float drawWidth = currentFrame.getRegionWidth() * scale;
        float drawHeight = currentFrame.getRegionHeight() * scale;
        batch.draw(currentFrame, x - drawWidth / 2f, y - drawHeight / 2f, drawWidth, drawHeight);
    }

    public int getScoreValue() {
        return Constants.SCORE_WARP_ENEMY;
    }
}
