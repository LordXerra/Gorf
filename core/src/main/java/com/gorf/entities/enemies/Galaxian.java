package com.gorf.entities.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.gorf.Constants;
import com.gorf.entities.Entity;
import com.gorf.graphics.SpriteManager;

/**
 * Galaxian enemy for Mission 3 (Galaxians).
 * Color hierarchy: yellow (60pts), blue (80pts), red (100pts), flagship/white (300pts).
 * Dives at player along Bezier curves.
 */
public class Galaxian extends Entity {
    public enum GalaxianColor {
        YELLOW(Constants.SCORE_GALAXIAN_YELLOW),
        BLUE(Constants.SCORE_GALAXIAN_BLUE),
        RED(Constants.SCORE_GALAXIAN_RED),
        FLAGSHIP(Constants.SCORE_GALAXIAN_FLAGSHIP);

        public final int score;
        GalaxianColor(int score) { this.score = score; }
    }

    private enum State { FORMATION, DIVING, RETURNING }

    private State state = State.FORMATION;
    private final GalaxianColor color;
    private float formationX, formationY;

    // Dive Bezier curve
    private float diveTimer;
    private float diveCooldown;
    private float diveT; // 0..1 along Bezier
    private float diveSpeed;
    private final Vector2 bezP0 = new Vector2();
    private final Vector2 bezP1 = new Vector2();
    private final Vector2 bezP2 = new Vector2();
    private final Vector2 bezP3 = new Vector2();

    private float targetX, targetY;
    private FireCallback fireCallback;

    private final Animation<TextureRegion> normalAnim;
    private final Animation<TextureRegion> diveAnim;

    public interface FireCallback {
        void onFire(float x, float y, float targetX, float targetY);
    }

    public Galaxian(SpriteManager sprites, GalaxianColor color, float formX, float formY, float difficulty) {
        this.color = color;
        this.formationX = formX;
        this.formationY = formY;
        this.x = formX;
        this.y = formY;

        String colorName = getColorName();
        boolean isFlagship = (color == GalaxianColor.FLAGSHIP);

        normalAnim = sprites.getAnimation(isFlagship ? "galaxian_white" : "galaxian_" + colorName);
        diveAnim = sprites.getAnimation(isFlagship ? "galaxian_white_dive" : "galaxian_" + colorName + "_dive");

        if (normalAnim != null) {
            currentAnimation = normalAnim;
            currentFrame = normalAnim.getKeyFrame(0);
        } else {
            currentFrame = sprites.getFrame("galaxian_" + colorName + "_1");
        }
        updateBoundsFromFrame();

        this.diveCooldown = MathUtils.random(8f, 16f) / difficulty;
        this.diveTimer = MathUtils.random(3f, diveCooldown);
        this.diveSpeed = 0.3f * difficulty; // Bezier T speed
    }

    private String getColorName() {
        return switch (color) {
            case YELLOW -> "yellow";
            case BLUE -> "blue";
            case RED -> "red";
            case FLAGSHIP -> "white";
        };
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

                currentAnimation = normalAnim;

                diveTimer -= delta;
                if (diveTimer <= 0) {
                    startDive();
                }
                break;

            case DIVING:
                currentAnimation = diveAnim;
                diveT += diveSpeed * delta;

                if (diveT >= 1f) {
                    // Dive complete, return to formation
                    state = State.RETURNING;
                } else {
                    // Evaluate cubic Bezier
                    float t = diveT;
                    float u = 1f - t;
                    x = u*u*u*bezP0.x + 3*u*u*t*bezP1.x + 3*u*t*t*bezP2.x + t*t*t*bezP3.x;
                    y = u*u*u*bezP0.y + 3*u*u*t*bezP1.y + 3*u*t*t*bezP2.y + t*t*t*bezP3.y;
                }

                // Fire midway through dive
                if (diveT > 0.3f && diveT < 0.35f && fireCallback != null) {
                    fireCallback.onFire(x, y, targetX, targetY);
                }
                break;

            case RETURNING:
                float rx = formationX - x;
                float ry = formationY - y;
                float rdist = (float) Math.sqrt(rx * rx + ry * ry);

                if (rdist > 5f) {
                    float returnSpeed = 200f;
                    x += (rx / rdist) * returnSpeed * delta;
                    y += (ry / rdist) * returnSpeed * delta;
                } else {
                    state = State.FORMATION;
                    diveTimer = diveCooldown;
                    currentAnimation = normalAnim;
                }
                break;
        }

        // Update current frame from animation
        if (currentAnimation != null) {
            currentFrame = currentAnimation.getKeyFrame(stateTime);
        }
    }

    private void startDive() {
        state = State.DIVING;
        diveT = 0;

        // Build a Bezier curve from current position toward player, then swoop back up
        bezP0.set(x, y);

        // Control point 1: swing to one side
        float side = MathUtils.randomBoolean() ? -1 : 1;
        bezP1.set(x + side * MathUtils.random(100f, 200f),
                  y - (y - targetY) * 0.3f);

        // Control point 2: near player, opposite side
        bezP2.set(targetX - side * MathUtils.random(50f, 120f),
                  targetY + MathUtils.random(20f, 60f));

        // End point: below screen, will trigger return
        bezP3.set(targetX + side * MathUtils.random(30f, 80f),
                  -40f);

        // Fire at player when starting dive
        if (fireCallback != null) {
            fireCallback.onFire(x, y, targetX, targetY);
        }
    }

    public int getScoreValue() {
        return color.score;
    }

    public GalaxianColor getGalaxianColor() {
        return color;
    }
}
