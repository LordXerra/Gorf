package com.gorf.entities.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.gorf.Constants;
import com.gorf.entities.Entity;

/**
 * Tall vertical laser beam projectile for Mission 2 (Laser Attack).
 * Acts as a barrier - a solid wall of energy extending downward.
 */
public class LaserBeam extends Entity {
    private static Texture pixelTexture;
    private float beamHeight;
    private float lifetime;
    private float maxLifetime;
    private float pulseTime;

    public LaserBeam(float startX, float startY, float difficulty) {
        this.x = startX;
        this.y = startY;
        this.beamHeight = 300f;
        this.maxLifetime = 2.0f / difficulty;
        this.lifetime = maxLifetime;

        width = 6;
        height = beamHeight;

        if (pixelTexture == null) {
            Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pm.setColor(Color.WHITE);
            pm.fill();
            pixelTexture = new Texture(pm);
            pm.dispose();
        }
    }

    @Override
    public void update(float delta) {
        if (!alive) return;
        pulseTime += delta;
        lifetime -= delta;

        // Beam extends downward from cannon
        dy = -100f * delta; // slight downward drift

        if (lifetime <= 0) {
            alive = false;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!alive) return;

        float alpha = Math.min(1f, lifetime / (maxLifetime * 0.3f));
        float pulse = 0.7f + 0.3f * MathUtils.sin(pulseTime * 20f);

        // Draw the beam as a tall glowing rectangle
        batch.setColor(1f * pulse, 1f * pulse, 0.2f, alpha * 0.8f);
        batch.draw(pixelTexture, x - width / 2f, y - beamHeight, width, beamHeight);

        // Inner brighter core
        batch.setColor(1f, 1f, 0.8f, alpha * 0.9f);
        batch.draw(pixelTexture, x - 1, y - beamHeight, 2, beamHeight);

        batch.setColor(Color.WHITE);
    }
}
