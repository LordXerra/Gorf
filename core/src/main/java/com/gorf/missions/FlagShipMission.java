package com.gorf.missions;

import com.gorf.Constants;
import com.gorf.entities.Entity;
import com.gorf.entities.EnemyBullet;
import com.gorf.entities.enemies.Debris;
import com.gorf.entities.enemies.Escort;
import com.gorf.entities.enemies.FlagShip;
import com.gorf.entities.enemies.ReactorCore;
import com.gorf.entities.enemies.ShieldSegment;
import com.gorf.graphics.SpriteManager;
import com.gorf.systems.EntityManager;

/**
 * Mission 5: Flag Ship.
 * Large boss with destructible shield segments protecting a reactor core.
 * Flanked by escort ships. Destroy shields to expose core for 1000pts kill shot.
 */
public class FlagShipMission extends Mission {
    private static final int NUM_SHIELDS = 10;

    private FlagShip flagShip;
    private ReactorCore reactorCore;
    private final ShieldSegment[] shields = new ShieldSegment[NUM_SHIELDS];
    private Escort leftEscort, rightEscort;
    private SpriteManager sprites;
    private float playerX, playerY;

    @Override
    public void init(EntityManager em, SpriteManager sprites, float difficulty) {
        this.difficulty = difficulty;
        this.sprites = sprites;
        this.complete = false;
        playerX = Constants.VIRTUAL_WIDTH / 2f;
        playerY = 60f;

        // Create flagship
        flagShip = new FlagShip(sprites, difficulty);
        flagShip.setFireCallback((fx, fy) -> {
            // Fire aimed bullet at player
            float dx = playerX - fx;
            float dy = playerY - fy;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            if (dist > 0) {
                float bSpeed = 180f * difficulty;
                EnemyBullet bullet = new EnemyBullet(sprites, fx, fy,
                    (dx / dist) * bSpeed, (dy / dist) * bSpeed);
                em.enemyProjectiles.add(bullet);
            }
        });
        em.enemies.add(flagShip);

        // Create shield segments as a colored barrier below the flagship
        float shieldSpacing = 19f;
        float shieldStartOffset = -(NUM_SHIELDS - 1) / 2f * shieldSpacing;
        float shieldYOffset = -40f; // positioned below the flagship

        for (int i = 0; i < NUM_SHIELDS; i++) {
            float offset = shieldStartOffset + i * shieldSpacing;
            shields[i] = new ShieldSegment(i, offset, shieldYOffset);
            shields[i].setDebrisCallback((debX, debY) -> {
                Debris debris = new Debris(sprites, debX, debY);
                em.enemies.add(debris);
            });
            shields[i].updatePosition(flagShip.x, flagShip.y);
            em.enemies.add(shields[i]);
        }

        // Create reactor core (hidden behind shields)
        reactorCore = new ReactorCore(sprites, 0);
        reactorCore.updatePosition(flagShip.x, flagShip.y);
        em.enemies.add(reactorCore);

        // Create escort ships
        leftEscort = new Escort(sprites, -140, -30, difficulty);
        leftEscort.setFireCallback((fx, fy, tx, ty) -> {
            float dx = playerX - fx;
            float dy = playerY - fy;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            if (dist > 0) {
                float bSpeed = 160f * difficulty;
                em.enemyProjectiles.add(new EnemyBullet(sprites, fx, fy,
                    (dx / dist) * bSpeed, (dy / dist) * bSpeed));
            }
        });
        leftEscort.updatePosition(flagShip.x, flagShip.y, playerX, playerY);
        em.enemies.add(leftEscort);

        rightEscort = new Escort(sprites, 140, -30, difficulty);
        rightEscort.setFireCallback((fx, fy, tx, ty) -> {
            float dx = playerX - fx;
            float dy = playerY - fy;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            if (dist > 0) {
                float bSpeed = 160f * difficulty;
                em.enemyProjectiles.add(new EnemyBullet(sprites, fx, fy,
                    (dx / dist) * bSpeed, (dy / dist) * bSpeed));
            }
        });
        rightEscort.updatePosition(flagShip.x, flagShip.y, playerX, playerY);
        em.enemies.add(rightEscort);
    }

    @Override
    public void update(float delta, EntityManager em) {
        // Track player
        if (em.player != null && em.player.alive) {
            playerX = em.player.x;
            playerY = em.player.y;
        }

        // Update shield and reactor positions to follow flagship
        if (flagShip.alive) {
            for (ShieldSegment shield : shields) {
                if (shield != null && shield.alive) {
                    shield.updatePosition(flagShip.x, flagShip.y);
                }
            }
            if (reactorCore != null && reactorCore.alive) {
                reactorCore.updatePosition(flagShip.x, flagShip.y);
            }

            // Update escort positions
            if (leftEscort != null && leftEscort.alive) {
                leftEscort.updatePosition(flagShip.x, flagShip.y, playerX, playerY);
            }
            if (rightEscort != null && rightEscort.alive) {
                rightEscort.updatePosition(flagShip.x, flagShip.y, playerX, playerY);
            }
        }
    }

    @Override
    public boolean isComplete(EntityManager em) {
        // Complete when reactor core is destroyed
        // The flagship itself is not directly destroyable - must hit the core
        if (reactorCore != null && !reactorCore.alive) {
            // Kill everything - wave is over
            if (flagShip.alive) flagShip.alive = false;
            for (ShieldSegment s : shields) {
                if (s != null) s.alive = false;
            }
            if (leftEscort != null) leftEscort.alive = false;
            if (rightEscort != null) rightEscort.alive = false;
            return true;
        }
        return false;
    }

    @Override
    public String getMissionName() {
        return "FLAG SHIP";
    }

    @Override
    public int getMissionNumber() {
        return 5;
    }
}
