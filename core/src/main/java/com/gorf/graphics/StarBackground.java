package com.gorf.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.gorf.Constants;

public class StarBackground {
    private final float[] x, y, phase;
    private final int[] size; // 0=small(1px), 1=large(2px)
    private Texture pixelTexture;
    private float globalTime;

    private static final float SMALL_SPEED = 20f;
    private static final float LARGE_SPEED = 40f;

    public StarBackground() {
        int count = Constants.STAR_COUNT;
        x = new float[count];
        y = new float[count];
        phase = new float[count];
        size = new int[count];

        for (int i = 0; i < count; i++) {
            x[i] = MathUtils.random(Constants.VIRTUAL_WIDTH);
            y[i] = MathUtils.random(Constants.VIRTUAL_HEIGHT);
            size[i] = i % 2; // alternate: 0=small, 1=large
            phase[i] = MathUtils.random(MathUtils.PI2);
        }

        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        pixelTexture = new Texture(pm);
        pm.dispose();
    }

    public void update(float delta) {
        globalTime += delta;
        for (int i = 0; i < Constants.STAR_COUNT; i++) {
            // Scroll downward; larger stars move faster (parallax)
            float speed = size[i] == 0 ? SMALL_SPEED : LARGE_SPEED;
            y[i] -= speed * delta;

            // Wrap around when going off bottom
            if (y[i] < 0) {
                y[i] += Constants.VIRTUAL_HEIGHT;
                x[i] = MathUtils.random(Constants.VIRTUAL_WIDTH);
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < Constants.STAR_COUNT; i++) {
            // Alternating brightness: small and large groups pulse at different rates
            float rate = size[i] == 0 ? 1.5f : 2.3f;
            float brightness = 0.2f + 0.8f * Math.abs(MathUtils.sin(phase[i] + globalTime * rate));

            // Slight color tint: small=white, large=light blue
            float r, g, b;
            if (size[i] == 0) {
                r = brightness;
                g = brightness;
                b = brightness;
            } else {
                r = brightness * 0.85f;
                g = brightness * 0.9f;
                b = brightness;
            }

            batch.setColor(r, g, b, 1f);
            float s = size[i] + 1; // 1px or 2px
            batch.draw(pixelTexture, x[i], y[i], s, s);
        }
        batch.setColor(Color.WHITE);
    }

    public void dispose() {
        if (pixelTexture != null) pixelTexture.dispose();
    }
}
