package com.gorf.entities.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.gorf.Constants;

/**
 * Curved destructible force field protecting the player in Mission 1.
 * Composed of individual segments arranged in a parabolic arc.
 */
public class ForceField {
    private final Array<Segment> segments = new Array<>();
    private Texture pixelTexture;
    private float shimmerTime;

    public static class Segment {
        public float x, y;
        public float width, height;
        public boolean alive = true;

        public Segment(float x, float y, float w, float h) {
            this.x = x;
            this.y = y;
            this.width = w;
            this.height = h;
        }

        public Rectangle getBounds(Rectangle out) {
            out.set(x - width / 2f, y - height / 2f, width, height);
            return out;
        }
    }

    public ForceField() {
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        pixelTexture = new Texture(pm);
        pm.dispose();

        createArc();
    }

    private void createArc() {
        // Create a parabolic arc of segments above the player zone
        float centerX = Constants.VIRTUAL_WIDTH / 2f;
        float baseY = Constants.PLAYER_ZONE_TOP + 20;
        float arcWidth = Constants.VIRTUAL_WIDTH * 0.6f;
        int numSegments = 30;
        float segW = 12;
        float segH = 8;

        for (int i = 0; i < numSegments; i++) {
            float t = (float) i / (numSegments - 1); // 0 to 1
            float normalizedX = t * 2f - 1f; // -1 to 1

            float sx = centerX + normalizedX * arcWidth / 2f;
            // Parabolic curve: higher at edges, lower in center
            float sy = baseY + (1f - normalizedX * normalizedX) * 40f;

            segments.add(new Segment(sx, sy, segW, segH));
        }
    }

    public void update(float delta) {
        shimmerTime += delta;
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < segments.size; i++) {
            Segment seg = segments.get(i);
            if (!seg.alive) continue;

            // Shimmer effect: brightness oscillates per segment
            float brightness = 0.5f + 0.5f * MathUtils.sin(shimmerTime * 4f + i * 0.4f);
            batch.setColor(0.2f * brightness, 0.6f * brightness, 1f * brightness, 0.8f);
            batch.draw(pixelTexture,
                seg.x - seg.width / 2f, seg.y - seg.height / 2f,
                seg.width, seg.height);
        }
        batch.setColor(Color.WHITE);
    }

    public Array<Segment> getSegments() {
        return segments;
    }

    public int getAliveCount() {
        int count = 0;
        for (Segment s : segments) {
            if (s.alive) count++;
        }
        return count;
    }

    public void dispose() {
        if (pixelTexture != null) pixelTexture.dispose();
    }
}
