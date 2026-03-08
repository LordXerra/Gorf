package com.gorf.entities.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gorf.Constants;
import com.gorf.entities.Entity;
import com.gorf.graphics.SpriteManager;

/**
 * The Gorfian Robot that flies across the top of the screen in Mission 1.
 * Distributes invaders into formation. Worth 250 points.
 */
public class GorfianRobot extends Entity {
    private float speed;
    private int direction; // 1 = right, -1 = left

    public GorfianRobot(SpriteManager sprites, float startX, float startY, float difficulty) {
        this.x = startX;
        this.y = startY;
        this.speed = 120f * difficulty;
        this.direction = 1;

        currentAnimation = sprites.getAnimation("gorfian_robot");
        if (currentAnimation != null) {
            currentFrame = currentAnimation.getKeyFrame(0);
        } else {
            currentFrame = sprites.getFrame("gorfian_robot_1");
        }
        updateBoundsFromFrame();
    }

    @Override
    public void update(float delta) {
        if (!alive) return;
        stateTime += delta;

        x += speed * direction * delta;

        // Bounce off screen edges
        if (x > Constants.VIRTUAL_WIDTH - width / 2f) {
            direction = -1;
        } else if (x < width / 2f) {
            direction = 1;
        }
    }

    public int getScoreValue() {
        return Constants.SCORE_GORFIAN_ROBOT;
    }
}
