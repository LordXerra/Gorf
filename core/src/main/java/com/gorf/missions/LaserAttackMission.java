package com.gorf.missions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.gorf.Constants;
import com.gorf.entities.Entity;
import com.gorf.entities.EnemyBullet;
import com.gorf.entities.enemies.DiveBomber;
import com.gorf.entities.enemies.LaserBeam;
import com.gorf.entities.enemies.LaserCannon;
import com.gorf.entities.enemies.MiniRobot;
import com.gorf.graphics.SpriteManager;
import com.gorf.systems.EntityManager;

/**
 * Mission 2: Laser Attack.
 * Two formations of 5 enemies each (left and right groups).
 * Features DiveBombers, LaserCannons, and MiniRobots.
 * Formations shift laterally; laser cannons fire vertical beams.
 */
public class LaserAttackMission extends Mission {
    private static final int ENEMIES_PER_GROUP = 5;
    private static final float GROUP_SPACING_X = 80f;
    private static final float GROUP_SPACING_Y = 50f;

    // Formation movement
    private float formationOffsetX;
    private float formationSpeed;
    private int formationDir = 1;
    private float formationBaseY;
    private float formationShiftTimer;
    private float shiftInterval;

    // Laser beams (rendered as entities but tracked here too)
    private final com.badlogic.gdx.utils.Array<LaserBeam> laserBeams = new com.badlogic.gdx.utils.Array<>();

    private SpriteManager sprites;
    private float playerX, playerY;

    @Override
    public void init(EntityManager em, SpriteManager sprites, float difficulty) {
        this.difficulty = difficulty;
        this.sprites = sprites;
        this.complete = false;

        formationOffsetX = 0;
        formationSpeed = 50f * difficulty;
        formationBaseY = Constants.VIRTUAL_HEIGHT - 100;
        shiftInterval = 3f / difficulty;
        formationShiftTimer = shiftInterval;
        playerX = Constants.VIRTUAL_WIDTH / 2f;
        playerY = 60f;

        // Left group: centered at x=280
        float leftCenterX = Constants.VIRTUAL_WIDTH * 0.28f;
        // Right group: centered at x=740
        float rightCenterX = Constants.VIRTUAL_WIDTH * 0.72f;

        spawnGroup(em, sprites, leftCenterX, difficulty);
        spawnGroup(em, sprites, rightCenterX, difficulty);
    }

    private void spawnGroup(EntityManager em, SpriteManager sprites, float centerX, float difficulty) {
        // Each group: 2 DiveBombers (top row), 1 LaserCannon (middle), 2 MiniRobots (bottom row)
        float topY = formationBaseY;
        float midY = formationBaseY - GROUP_SPACING_Y;
        float botY = formationBaseY - GROUP_SPACING_Y * 2;
        float halfSpace = GROUP_SPACING_X / 2f;

        // Top row: 2 DiveBombers
        DiveBomber db1 = new DiveBomber(sprites, centerX - halfSpace, topY, difficulty);
        db1.setPlayerTarget(playerX, playerY);
        db1.setFireCallback((fx, fy, tx, ty) -> {
            EnemyBullet bullet = new EnemyBullet(sprites, fx, fy,
                (tx - fx) * 0.5f, -200f * difficulty);
            em.enemyProjectiles.add(bullet);
        });
        em.enemies.add(db1);

        DiveBomber db2 = new DiveBomber(sprites, centerX + halfSpace, topY, difficulty);
        db2.setPlayerTarget(playerX, playerY);
        db2.setFireCallback((fx, fy, tx, ty) -> {
            EnemyBullet bullet = new EnemyBullet(sprites, fx, fy,
                (tx - fx) * 0.5f, -200f * difficulty);
            em.enemyProjectiles.add(bullet);
        });
        em.enemies.add(db2);

        // Middle: 1 LaserCannon
        LaserCannon lc = new LaserCannon(sprites, centerX, midY, difficulty);
        lc.setFireCallback((fx, fy) -> {
            LaserBeam beam = new LaserBeam(fx, fy, difficulty);
            laserBeams.add(beam);
            em.enemyProjectiles.add(beam);
        });
        em.enemies.add(lc);

        // Bottom row: 2 MiniRobots
        MiniRobot mr1 = new MiniRobot(sprites, centerX - halfSpace, botY, difficulty);
        mr1.setPlayerTarget(playerX, playerY);
        em.enemies.add(mr1);

        MiniRobot mr2 = new MiniRobot(sprites, centerX + halfSpace, botY, difficulty);
        mr2.setPlayerTarget(playerX, playerY);
        em.enemies.add(mr2);
    }

    @Override
    public void update(float delta, EntityManager em) {
        // Track player position for targeting
        if (em.player != null && em.player.alive) {
            playerX = em.player.x;
            playerY = em.player.y;
        }

        // Update formation offset (lateral drift)
        formationOffsetX += formationSpeed * formationDir * delta;

        // Reverse direction at boundaries
        if (formationOffsetX > 80) {
            formationDir = -1;
        } else if (formationOffsetX < -80) {
            formationDir = 1;
        }

        // Periodic formation shift (change base Y slightly)
        formationShiftTimer -= delta;
        if (formationShiftTimer <= 0) {
            formationShiftTimer = shiftInterval;
            // Small random vertical shift
            formationBaseY += MathUtils.random(-15f, 15f);
            formationBaseY = MathUtils.clamp(formationBaseY,
                Constants.VIRTUAL_HEIGHT - 180,
                Constants.VIRTUAL_HEIGHT - 80);
        }

        // Update all enemy positions and targets
        for (Entity e : em.enemies) {
            if (!e.alive) continue;

            if (e instanceof DiveBomber db) {
                db.setPlayerTarget(playerX, playerY);
            } else if (e instanceof MiniRobot mr) {
                mr.setPlayerTarget(playerX, playerY);
            }
        }

        // Clean up dead laser beams
        for (int i = laserBeams.size - 1; i >= 0; i--) {
            if (!laserBeams.get(i).alive) {
                laserBeams.removeIndex(i);
            }
        }
    }

    @Override
    public boolean isComplete(EntityManager em) {
        // Complete when all DiveBombers, LaserCannons, and MiniRobots are dead
        for (Entity e : em.enemies) {
            if (!e.alive) continue;
            if (e instanceof DiveBomber || e instanceof LaserCannon || e instanceof MiniRobot) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getMissionName() {
        return "LASER ATTACK";
    }

    @Override
    public int getMissionNumber() {
        return 2;
    }

    @Override
    public void render(SpriteBatch batch) {
        // LaserBeams render themselves via entity rendering
    }

    @Override
    public void dispose() {
        laserBeams.clear();
    }
}
