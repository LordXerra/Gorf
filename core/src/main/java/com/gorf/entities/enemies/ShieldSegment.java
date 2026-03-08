package com.gorf.entities.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.gorf.Constants;
import com.gorf.entities.Entity;

/**
 * Destructible shield segment protecting the Flag Ship's reactor core.
 * Rendered as colored blocks (not sprites) similar to Mission 1's force field.
 * 20pts each. When destroyed, may spawn falling debris.
 */
public class ShieldSegment extends Entity {
    private static Texture pixelTexture;
    private static int instanceCount = 0;

    private static final float BLOCK_W = 16f;
    private static final float BLOCK_H = 10f;

    private static final int MAX_HITS = 3;

    private float parentOffsetX;
    private float parentOffsetY;
    private int segmentIndex;
    private int hitsRemaining = MAX_HITS;
    private DebrisCallback debrisCallback;

    public interface DebrisCallback {
        void onDebris(float x, float y);
    }

    public ShieldSegment(int segmentIndex, float offsetX, float offsetY) {
        this.parentOffsetX = offsetX;
        this.parentOffsetY = offsetY;
        this.segmentIndex = segmentIndex;

        if (pixelTexture == null) {
            Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pm.setColor(Color.WHITE);
            pm.fill();
            pixelTexture = new Texture(pm);
            pm.dispose();
        }
        instanceCount++;

        this.width = BLOCK_W;
        this.height = BLOCK_H;
    }

    public void setDebrisCallback(DebrisCallback callback) {
        this.debrisCallback = callback;
    }

    public void updatePosition(float parentX, float parentY) {
        this.x = parentX + parentOffsetX;
        this.y = parentY + parentOffsetY;
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!alive) return;

        // Shimmering colored blocks - gets dimmer/redder as damage increases
        float health = (float) hitsRemaining / MAX_HITS;
        float shimmer = 0.6f + 0.4f * MathUtils.sin(stateTime * 4f + segmentIndex * 0.6f);
        float r = 0.9f * shimmer;
        float g = 0.5f * shimmer * health;
        float b = 0.1f * shimmer * health;
        batch.setColor(r, g, b, 0.9f);
        batch.draw(pixelTexture, x - BLOCK_W / 2f, y - BLOCK_H / 2f, BLOCK_W, BLOCK_H);
        batch.setColor(Color.WHITE);
    }

    public void onDestroyed() {
        hitsRemaining--;
        if (hitsRemaining <= 0) {
            alive = false;
            if (debrisCallback != null) {
                debrisCallback.onDebris(x, y);
            }
        }
    }

    public int getScoreValue() {
        return Constants.SCORE_HULL_HIT;
    }

    public void disposeStatic() {
        instanceCount--;
        if (instanceCount <= 0 && pixelTexture != null) {
            pixelTexture.dispose();
            pixelTexture = null;
            instanceCount = 0;
        }
    }
}
