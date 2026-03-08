package com.gorf.missions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.gorf.Constants;
import com.gorf.entities.Entity;
import com.gorf.entities.EnemyBullet;
import com.gorf.entities.PlayerBullet;
import com.gorf.entities.enemies.ForceField;
import com.gorf.entities.enemies.GorfianRobot;
import com.gorf.entities.enemies.Invader;
import com.gorf.entities.enemies.UFO;
import com.gorf.graphics.SpriteManager;
import com.gorf.systems.EntityManager;

/**
 * Mission 1: Astro Battles - Space Invaders clone.
 * 24 invaders in 6x4 grid, curved force field, Gorfian Robot, UFO bonuses.
 */
public class AstroBattlesMission extends Mission {
    private static final int COLS = 6;
    private static final int ROWS = 4;
    private static final float COL_SPACING = 60f;
    private static final float ROW_SPACING = 45f;

    // Formation movement
    private float formationX; // offset from center
    private float formationSpeed;
    private int formationDir = 1; // 1=right, -1=left
    private boolean needsDrop;
    private float dropAmount = 20f;
    private float formationBaseY;

    // UFO spawning
    private float ufoTimer;
    private float ufoInterval;

    // Gorfian Robot
    private GorfianRobot robot;
    private boolean robotSpawned;

    // Force field
    private ForceField forceField;
    private SpriteManager sprites;

    private final Rectangle tempRect1 = new Rectangle();
    private final Rectangle tempRect2 = new Rectangle();

    @Override
    public void init(EntityManager em, SpriteManager sprites, float difficulty) {
        this.difficulty = difficulty;
        this.sprites = sprites;
        this.complete = false;

        formationX = 0;
        formationSpeed = 40f * difficulty;
        formationBaseY = Constants.VIRTUAL_HEIGHT - 120;

        // Create force field
        forceField = new ForceField();

        // Spawn 24 invaders in a 6x4 grid
        float startX = Constants.VIRTUAL_WIDTH / 2f - (COLS - 1) * COL_SPACING / 2f;
        for (int row = 0; row < ROWS; row++) {
            int invaderType = Math.min(row, 2); // row 0=type0, row 1=type1, rows 2-3=type2
            for (int col = 0; col < COLS; col++) {
                float ix = startX + col * COL_SPACING;
                float iy = formationBaseY - row * ROW_SPACING;

                Invader invader = new Invader(sprites, col, row, invaderType, difficulty);
                invader.x = ix;
                invader.y = iy;

                // Set fire callback
                invader.setFireCallback((fx, fy) -> {
                    // Only bottom-most alive invader in each column should fire
                    EnemyBullet bullet = new EnemyBullet(sprites, fx, fy, 0, -180f * difficulty);
                    em.enemyProjectiles.add(bullet);
                });

                em.enemies.add(invader);
            }
        }

        // Spawn Gorfian Robot at top
        robot = new GorfianRobot(sprites, Constants.VIRTUAL_WIDTH / 2f,
            Constants.VIRTUAL_HEIGHT - 40, difficulty);
        em.enemies.add(robot);
        robotSpawned = true;

        // UFO timing
        ufoInterval = MathUtils.random(8f, 15f) / difficulty;
        ufoTimer = ufoInterval;
    }

    @Override
    public void update(float delta, EntityManager em) {
        // Update force field shimmer
        if (forceField != null) {
            forceField.update(delta);
        }

        // Formation lateral movement
        formationX += formationSpeed * formationDir * delta;

        // Check if formation has reached screen edges
        float leftMost = Constants.VIRTUAL_WIDTH;
        float rightMost = 0;
        boolean anyAlive = false;

        for (Entity e : em.enemies) {
            if (e instanceof Invader && e.alive) {
                anyAlive = true;
                if (e.x < leftMost) leftMost = e.x;
                if (e.x > rightMost) rightMost = e.x;
            }
        }

        if (anyAlive) {
            if (rightMost + formationSpeed * delta > Constants.VIRTUAL_WIDTH - 30) {
                formationDir = -1;
                needsDrop = true;
            } else if (leftMost - formationSpeed * delta < 30) {
                formationDir = 1;
                needsDrop = true;
            }
        }

        // Move all invaders
        for (Entity e : em.enemies) {
            if (e instanceof Invader && e.alive) {
                e.x += formationSpeed * formationDir * delta;
                if (needsDrop) {
                    e.y -= dropAmount * delta * 2;
                }
            }
        }
        needsDrop = false;

        // UFO spawning
        ufoTimer -= delta;
        if (ufoTimer <= 0) {
            ufoTimer = MathUtils.random(8f, 15f) / difficulty;
            boolean isSmall = MathUtils.randomBoolean();
            int dir = MathUtils.randomBoolean() ? 1 : -1;
            float startX = dir == 1 ? -30 : Constants.VIRTUAL_WIDTH + 30;
            UFO ufo = new UFO(sprites, isSmall, startX,
                Constants.VIRTUAL_HEIGHT - 50, dir);
            em.enemies.add(ufo);
        }

        // Check player bullets vs force field segments
        checkBulletForceFieldCollisions(em);

        // Check enemy bullets vs force field segments
        checkEnemyBulletForceFieldCollisions(em);
    }

    private void checkBulletForceFieldCollisions(EntityManager em) {
        if (forceField == null) return;

        for (int bi = em.playerBullets.size - 1; bi >= 0; bi--) {
            PlayerBullet bullet = em.playerBullets.get(bi);
            if (!bullet.alive) continue;
            bullet.getBounds(tempRect1);

            for (ForceField.Segment seg : forceField.getSegments()) {
                if (!seg.alive) continue;
                seg.getBounds(tempRect2);

                if (tempRect1.overlaps(tempRect2)) {
                    // Player bullet passes through the field (it opens momentarily)
                    // But damage the segment slightly - destroy if hit from below
                    seg.alive = false;
                    break;
                }
            }
        }
    }

    private void checkEnemyBulletForceFieldCollisions(EntityManager em) {
        if (forceField == null) return;

        for (int pi = em.enemyProjectiles.size - 1; pi >= 0; pi--) {
            Entity proj = em.enemyProjectiles.get(pi);
            if (!proj.alive) continue;
            proj.getBounds(tempRect1);

            for (ForceField.Segment seg : forceField.getSegments()) {
                if (!seg.alive) continue;
                seg.getBounds(tempRect2);

                if (tempRect1.overlaps(tempRect2)) {
                    // Enemy bullet destroys the segment and itself
                    seg.alive = false;
                    proj.alive = false;
                    break;
                }
            }
        }
    }

    @Override
    public boolean isComplete(EntityManager em) {
        // Complete when all invaders and robot are dead (UFOs don't count)
        for (Entity e : em.enemies) {
            if (!e.alive) continue;
            if (e instanceof Invader || e instanceof GorfianRobot) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getMissionName() {
        return "ASTRO BATTLES";
    }

    @Override
    public int getMissionNumber() {
        return 1;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (forceField != null) {
            forceField.render(batch);
        }
    }

    @Override
    public void dispose() {
        if (forceField != null) {
            forceField.dispose();
        }
    }
}
