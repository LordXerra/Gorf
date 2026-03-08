package com.gorf.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.gorf.Constants;

/**
 * Pseudo-3D wormhole effect for Mission 4 (Space Warp).
 * Concentric rings at screen center that pulse and rotate.
 */
public class WarpEffect {
    private static final int NUM_RINGS = 12;
    private static final float CENTER_X = Constants.VIRTUAL_WIDTH / 2f;
    private static final float CENTER_Y = Constants.VIRTUAL_HEIGHT / 2f;

    private Texture pixelTexture;
    private float time;
    private final float[] ringRadii;
    private final float[] ringPhases;

    public WarpEffect() {
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        pixelTexture = new Texture(pm);
        pm.dispose();

        ringRadii = new float[NUM_RINGS];
        ringPhases = new float[NUM_RINGS];
        for (int i = 0; i < NUM_RINGS; i++) {
            ringRadii[i] = 20f + i * 28f;
            ringPhases[i] = MathUtils.random(MathUtils.PI2);
        }
    }

    public void update(float delta) {
        time += delta;
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < NUM_RINGS; i++) {
            float radius = ringRadii[i] + MathUtils.sin(time * 2f + ringPhases[i]) * 5f;
            float alpha = 0.3f + 0.2f * MathUtils.sin(time * 3f + i * 0.5f);
            float hue = (time * 0.1f + i * 0.08f) % 1f;

            // Convert hue to RGB (simplified HSV->RGB)
            float r, g, b;
            int sector = (int)(hue * 6) % 6;
            float f = hue * 6 - sector;
            switch (sector) {
                case 0: r = 1; g = f; b = 0; break;
                case 1: r = 1-f; g = 1; b = 0; break;
                case 2: r = 0; g = 1; b = f; break;
                case 3: r = 0; g = 1-f; b = 1; break;
                case 4: r = f; g = 0; b = 1; break;
                default: r = 1; g = 0; b = 1-f; break;
            }

            batch.setColor(r, g, b, alpha);

            // Draw ring as segments around a circle
            int segments = 32;
            float segAngle = MathUtils.PI2 / segments;
            float rotation = time * (0.5f + i * 0.1f);

            for (int s = 0; s < segments; s++) {
                float angle = s * segAngle + rotation;
                float px = CENTER_X + MathUtils.cos(angle) * radius;
                float py = CENTER_Y + MathUtils.sin(angle) * radius;
                batch.draw(pixelTexture, px - 2, py - 2, 4, 4);
            }
        }

        batch.setColor(Color.WHITE);
    }

    public void dispose() {
        if (pixelTexture != null) {
            pixelTexture.dispose();
        }
    }
}
