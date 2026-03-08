package com.gorf.systems;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.gorf.entities.Entity;
import com.gorf.entities.PlayerBullet;
import com.gorf.entities.enemies.ShieldSegment;

public class CollisionSystem {
    private final Rectangle r1 = new Rectangle();
    private final Rectangle r2 = new Rectangle();

    public interface CollisionCallback {
        void onEnemyKilled(Entity enemy, float x, float y, int score);
        void onPlayerKilled();
        void onProjectileDestroyed(Entity projectile);
    }

    /**
     * Check all collisions. Returns true if the player died this frame.
     */
    public boolean checkAll(EntityManager em, CollisionCallback callback) {
        boolean playerDied = false;

        // Player bullets vs enemies
        for (int bi = em.playerBullets.size - 1; bi >= 0; bi--) {
            PlayerBullet bullet = em.playerBullets.get(bi);
            if (!bullet.alive) continue;
            bullet.getBounds(r1);

            for (int ei = em.enemies.size - 1; ei >= 0; ei--) {
                Entity enemy = em.enemies.get(ei);
                if (!enemy.alive) continue;
                enemy.getBounds(r2);

                if (r1.overlaps(r2)) {
                    bullet.alive = false;
                    // ShieldSegments handle their own death via onDestroyed() (multi-hit)
                    if (!(enemy instanceof ShieldSegment)) {
                        enemy.alive = false;
                    }
                    callback.onEnemyKilled(enemy, enemy.x, enemy.y, 0);
                    break;
                }
            }
        }

        // Player bullets vs enemy projectiles (quark laser can destroy enemy shots)
        for (int bi = em.playerBullets.size - 1; bi >= 0; bi--) {
            PlayerBullet bullet = em.playerBullets.get(bi);
            if (!bullet.alive) continue;
            bullet.getBounds(r1);

            for (int pi = em.enemyProjectiles.size - 1; pi >= 0; pi--) {
                Entity proj = em.enemyProjectiles.get(pi);
                if (!proj.alive) continue;
                proj.getBounds(r2);

                if (r1.overlaps(r2)) {
                    bullet.alive = false;
                    proj.alive = false;
                    callback.onProjectileDestroyed(proj);
                    break;
                }
            }
        }

        // Enemy projectiles vs player
        if (em.player.alive) {
            em.player.getBounds(r1);
            for (Entity proj : em.enemyProjectiles) {
                if (!proj.alive) continue;
                proj.getBounds(r2);
                if (r1.overlaps(r2)) {
                    proj.alive = false;
                    callback.onPlayerKilled();
                    playerDied = true;
                    break;
                }
            }
        }

        // Enemies colliding with player (dive-bombing)
        if (em.player.alive && !playerDied) {
            em.player.getBounds(r1);
            for (Entity enemy : em.enemies) {
                if (!enemy.alive) continue;
                enemy.getBounds(r2);
                if (r1.overlaps(r2)) {
                    enemy.alive = false;
                    callback.onPlayerKilled();
                    playerDied = true;
                    break;
                }
            }
        }

        return playerDied;
    }
}
