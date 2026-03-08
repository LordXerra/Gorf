package com.gorf.missions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.gorf.Constants;
import com.gorf.entities.Entity;
import com.gorf.entities.enemies.WarpEnemy;
import com.gorf.graphics.SpriteManager;
import com.gorf.graphics.WarpEffect;
import com.gorf.systems.EntityManager;

/**
 * Mission 4: Space Warp.
 * Player flies through a wormhole. Enemies spiral outward from center.
 * Survive the wave - dodge or shoot enemies worth 100pts each.
 */
public class SpaceWarpMission extends Mission {
    private static final int TOTAL_ENEMIES = 20;

    private WarpEffect warpEffect;
    private SpriteManager sprites;

    private float spawnTimer;
    private float spawnInterval;
    private int spawned;
    private float nextAngle;

    @Override
    public void init(EntityManager em, SpriteManager sprites, float difficulty) {
        this.difficulty = difficulty;
        this.sprites = sprites;
        this.complete = false;

        warpEffect = new WarpEffect();

        spawnInterval = 1.2f / difficulty;
        spawnTimer = 0.5f; // Short initial delay
        spawned = 0;
        nextAngle = MathUtils.random(MathUtils.PI2);
    }

    @Override
    public void update(float delta, EntityManager em) {
        warpEffect.update(delta);

        if (spawned < TOTAL_ENEMIES) {
            spawnTimer -= delta;
            if (spawnTimer <= 0) {
                spawnTimer = spawnInterval;
                spawnEnemy(em);
            }
        }
    }

    private void spawnEnemy(EntityManager em) {
        // Spawn at semi-random angles for variety
        nextAngle += MathUtils.random(0.8f, 2.5f);
        WarpEnemy enemy = new WarpEnemy(sprites, nextAngle, difficulty);
        em.enemies.add(enemy);
        spawned++;
    }

    @Override
    public boolean isComplete(EntityManager em) {
        if (spawned < TOTAL_ENEMIES) return false;

        // All spawned and none alive
        for (Entity e : em.enemies) {
            if (e.alive && e instanceof WarpEnemy) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getMissionName() {
        return "SPACE WARP";
    }

    @Override
    public int getMissionNumber() {
        return 4;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (warpEffect != null) {
            warpEffect.render(batch);
        }
    }

    @Override
    public void dispose() {
        if (warpEffect != null) {
            warpEffect.dispose();
        }
    }
}
