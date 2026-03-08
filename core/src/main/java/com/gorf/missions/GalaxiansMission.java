package com.gorf.missions;

import com.badlogic.gdx.math.MathUtils;
import com.gorf.Constants;
import com.gorf.entities.Entity;
import com.gorf.entities.EnemyBullet;
import com.gorf.entities.enemies.Galaxian;
import com.gorf.entities.enemies.Galaxian.GalaxianColor;
import com.gorf.graphics.SpriteManager;
import com.gorf.systems.EntityManager;

/**
 * Mission 3: Galaxians.
 * Enemies in formation (3 rows x 5 cols).
 * Color hierarchy: blue (bottom), red, flagship/white (top).
 * Enemies dive at player along Bezier curves.
 */
public class GalaxiansMission extends Mission {
    private static final int COLS = 5;
    private static final int ROWS = 3;
    private static final float COL_SPACING = 55f;
    private static final float ROW_SPACING = 40f;

    // Formation movement
    private float formationX;
    private float formationSpeed;
    private int formationDir = 1;

    private float playerX, playerY;
    private SpriteManager sprites;

    @Override
    public void init(EntityManager em, SpriteManager sprites, float difficulty) {
        this.difficulty = difficulty;
        this.sprites = sprites;
        this.complete = false;

        formationX = 0;
        formationSpeed = 35f * difficulty;
        playerX = Constants.VIRTUAL_WIDTH / 2f;
        playerY = 60f;

        float startX = Constants.VIRTUAL_WIDTH / 2f - (COLS - 1) * COL_SPACING / 2f;
        float topY = Constants.VIRTUAL_HEIGHT - 100;

        // Row colors from top to bottom: flagship, red, blue/yellow
        GalaxianColor[] rowColors = {
            GalaxianColor.FLAGSHIP, GalaxianColor.RED, GalaxianColor.BLUE
        };

        for (int row = 0; row < ROWS; row++) {
            GalaxianColor color = rowColors[row];
            // Top row (flagship) has fewer: only 1 in center
            int colStart = 0;
            int colEnd = COLS;
            if (color == GalaxianColor.FLAGSHIP) {
                colStart = 2;
                colEnd = 3;
            }

            for (int col = colStart; col < colEnd; col++) {
                float gx = startX + col * COL_SPACING;
                float gy = topY - row * ROW_SPACING;

                Galaxian g = new Galaxian(sprites, color, gx, gy, difficulty);
                g.setPlayerTarget(playerX, playerY);
                g.setFireCallback((fx, fy, tx, ty) -> {
                    float dx = tx - fx;
                    float dy = ty - fy;
                    float dist = (float) Math.sqrt(dx * dx + dy * dy);
                    if (dist > 0) {
                        float bSpeed = 140f * difficulty;
                        EnemyBullet bullet = new EnemyBullet(sprites, fx, fy,
                            (dx / dist) * bSpeed, (dy / dist) * bSpeed);
                        em.enemyProjectiles.add(bullet);
                    }
                });
                em.enemies.add(g);
            }
        }
    }

    @Override
    public void update(float delta, EntityManager em) {
        // Track player
        if (em.player != null && em.player.alive) {
            playerX = em.player.x;
            playerY = em.player.y;
        }

        // Formation lateral movement
        formationX += formationSpeed * formationDir * delta;

        // Check boundaries
        float leftMost = Constants.VIRTUAL_WIDTH;
        float rightMost = 0;
        boolean anyAlive = false;

        for (Entity e : em.enemies) {
            if (e instanceof Galaxian && e.alive) {
                anyAlive = true;
                if (e.x < leftMost) leftMost = e.x;
                if (e.x > rightMost) rightMost = e.x;
            }
        }

        if (anyAlive) {
            if (rightMost > Constants.VIRTUAL_WIDTH - 40) {
                formationDir = -1;
            } else if (leftMost < 40) {
                formationDir = 1;
            }
        }

        // Update all Galaxians
        for (Entity e : em.enemies) {
            if (!e.alive) continue;
            if (e instanceof Galaxian g) {
                g.setPlayerTarget(playerX, playerY);
                // Apply formation drift to base position
                // (Galaxian's own update handles the movement)
            }
        }
    }

    @Override
    public boolean isComplete(EntityManager em) {
        for (Entity e : em.enemies) {
            if (e.alive && e instanceof Galaxian) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getMissionName() {
        return "GALAXIANS";
    }

    @Override
    public int getMissionNumber() {
        return 3;
    }
}
