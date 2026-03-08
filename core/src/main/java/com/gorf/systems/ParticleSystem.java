package com.gorf.systems;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class ParticleSystem {
    private final Array<Particle> active = new Array<>();
    private final Array<Particle> pool = new Array<>();
    private Texture pixelTexture;

    private static class Particle {
        float x, y, dx, dy;
        float life, maxLife;
        float r, g, b;
        float size;
    }

    public ParticleSystem() {
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        pixelTexture = new Texture(pm);
        pm.dispose();

        for (int i = 0; i < 500; i++) {
            pool.add(new Particle());
        }
    }

    private Particle obtain() {
        return pool.size > 0 ? pool.pop() : new Particle();
    }

    private void free(Particle p) {
        pool.add(p);
    }

    /** Spawn an explosion for an invader-type enemy (red/orange). */
    public void spawnInvaderExplosion(float x, float y) {
        emitParticles(x, y, 20, 1f, 0.4f, 0.1f, 30f, 150f, 0.3f, 0.8f);
    }

    /** Spawn an explosion for a Galaxian enemy based on color. */
    public void spawnGalaxianExplosion(float x, float y, String color) {
        switch (color) {
            case "yellow" -> emitParticles(x, y, 25, 1f, 1f, 0.2f, 30f, 150f, 0.3f, 0.9f);
            case "blue" -> emitParticles(x, y, 25, 0.3f, 0.3f, 1f, 30f, 150f, 0.3f, 0.9f);
            case "red" -> emitParticles(x, y, 25, 1f, 0.2f, 0.2f, 30f, 150f, 0.3f, 0.9f);
            default -> emitParticles(x, y, 25, 1f, 1f, 1f, 30f, 150f, 0.3f, 0.9f);
        }
    }

    /** UFO explosion (rainbow). */
    public void spawnUFOExplosion(float x, float y) {
        emitParticles(x, y, 30, 1f, 0.5f, 0f, 40f, 180f, 0.4f, 1.0f);
        emitParticles(x, y, 15, 0.2f, 0.8f, 1f, 30f, 160f, 0.3f, 0.8f);
    }

    /** Flagship destruction (massive). */
    public void spawnFlagshipExplosion(float x, float y) {
        emitParticles(x, y, 80, 1f, 0.6f, 0.1f, 50f, 250f, 0.5f, 1.5f);
        emitParticles(x, y, 40, 0.3f, 0.5f, 1f, 40f, 200f, 0.4f, 1.2f);
        emitParticles(x, y, 30, 1f, 1f, 1f, 60f, 280f, 0.3f, 1.0f);
    }

    /** Shield segment break (metallic). */
    public void spawnShieldExplosion(float x, float y) {
        emitParticles(x, y, 15, 0.6f, 0.6f, 0.8f, 20f, 100f, 0.2f, 0.6f);
    }

    /** Player death (extra dramatic). */
    public void spawnPlayerExplosion(float x, float y) {
        emitParticles(x, y, 60, 1f, 0.3f, 0.1f, 40f, 200f, 0.5f, 1.5f);
        emitParticles(x, y, 30, 1f, 1f, 0.3f, 30f, 180f, 0.4f, 1.2f);
        emitParticles(x, y, 20, 1f, 1f, 1f, 50f, 220f, 0.3f, 0.8f);
    }

    /** Generic small explosion for projectiles. */
    public void spawnSmallExplosion(float x, float y) {
        emitParticles(x, y, 8, 1f, 1f, 0.5f, 20f, 80f, 0.1f, 0.4f);
    }

    /** Warp enemy explosion (purple/cyan). */
    public void spawnWarpExplosion(float x, float y) {
        emitParticles(x, y, 20, 0.6f, 0.2f, 1f, 30f, 150f, 0.3f, 0.8f);
    }

    /** Generic enemy explosion. */
    public void spawnEnemyExplosion(float x, float y) {
        emitParticles(x, y, 25, 1f, 0.5f, 0.1f, 30f, 150f, 0.3f, 0.9f);
    }

    private void emitParticles(float x, float y, int count,
                                float baseR, float baseG, float baseB,
                                float minSpeed, float maxSpeed,
                                float minLife, float maxLife) {
        for (int i = 0; i < count; i++) {
            Particle p = obtain();
            p.x = x;
            p.y = y;
            float angle = MathUtils.random(MathUtils.PI2);
            float speed = MathUtils.random(minSpeed, maxSpeed);
            p.dx = MathUtils.cos(angle) * speed;
            p.dy = MathUtils.sin(angle) * speed;
            p.maxLife = MathUtils.random(minLife, maxLife);
            p.life = p.maxLife;
            p.size = MathUtils.random(1, 3) * 2;
            p.r = MathUtils.clamp(baseR + MathUtils.random(-0.2f, 0.2f), 0, 1);
            p.g = MathUtils.clamp(baseG + MathUtils.random(-0.2f, 0.2f), 0, 1);
            p.b = MathUtils.clamp(baseB + MathUtils.random(-0.2f, 0.2f), 0, 1);
            active.add(p);
        }
    }

    public void update(float delta) {
        for (int i = active.size - 1; i >= 0; i--) {
            Particle p = active.get(i);
            p.x += p.dx * delta;
            p.y += p.dy * delta;
            p.dx *= 0.97f;
            p.dy *= 0.97f;
            p.life -= delta;
            if (p.life <= 0) {
                free(p);
                active.removeIndex(i);
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < active.size; i++) {
            Particle p = active.get(i);
            float alpha = p.life / p.maxLife;
            batch.setColor(p.r, p.g, p.b, alpha);
            batch.draw(pixelTexture, p.x - p.size / 2f, p.y - p.size / 2f, p.size, p.size);
        }
        batch.setColor(Color.WHITE);
    }

    public void clear() {
        for (int i = active.size - 1; i >= 0; i--) {
            free(active.get(i));
        }
        active.clear();
    }

    public void dispose() {
        if (pixelTexture != null) pixelTexture.dispose();
    }
}
