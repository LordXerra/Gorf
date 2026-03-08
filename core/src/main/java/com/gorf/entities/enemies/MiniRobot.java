package com.gorf.entities.enemies;

import com.badlogic.gdx.math.MathUtils;
import com.gorf.Constants;
import com.gorf.entities.Entity;
import com.gorf.graphics.SpriteManager;

/**
 * Small Gorfian robot in Mission 2 (Laser Attack) formations.
 * Occasionally swoops in to attack the player.
 */
public class MiniRobot extends Entity {
    private float formationX, formationY;
    private float attackTimer;
    private boolean attacking;
    private float attackTargetX, attackTargetY;
    private float speed;

    public MiniRobot(SpriteManager sprites, float formX, float formY, float difficulty) {
        this.formationX = formX;
        this.formationY = formY;
        this.x = formX;
        this.y = formY;
        this.speed = 180f * difficulty;
        this.attackTimer = MathUtils.random(4f, 10f) / difficulty;

        currentAnimation = sprites.getAnimation("mini_robot");
        if (currentAnimation != null) {
            currentFrame = currentAnimation.getKeyFrame(0);
        } else {
            currentFrame = sprites.getFrame("mini_robot_1");
        }
        updateBoundsFromFrame();
    }

    public void setPlayerTarget(float px, float py) {
        this.attackTargetX = px;
        this.attackTargetY = py;
    }

    public void updateFormationPos(float fx, float fy) {
        this.formationX = fx;
        this.formationY = fy;
    }

    @Override
    public void update(float delta) {
        if (!alive) return;
        stateTime += delta;

        if (!attacking) {
            // Stay in formation
            x += (formationX - x) * 3f * delta;
            y += (formationY - y) * 3f * delta;

            attackTimer -= delta;
            if (attackTimer <= 0) {
                attacking = true;
            }
        } else {
            // Swoop toward player
            float dx = attackTargetX - x;
            float dy = attackTargetY - y;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);

            if (dist > 5f) {
                x += (dx / dist) * speed * delta;
                y += (dy / dist) * speed * delta;
            }

            // Return to formation if below screen or close to target
            if (y < -20 || dist < 30f) {
                attacking = false;
                attackTimer = MathUtils.random(4f, 8f);
            }
        }
    }

    public int getScoreValue() {
        return Constants.SCORE_MINI_ROBOT;
    }
}
