package com.gorf.systems;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.gorf.entities.Entity;
import com.gorf.entities.EnemyBullet;
import com.gorf.entities.Player;
import com.gorf.entities.PlayerBullet;
import com.gorf.graphics.SpriteManager;

public class EntityManager {
    public final Player player;
    public final Array<Entity> enemies = new Array<>();
    public final Array<PlayerBullet> playerBullets = new Array<>();
    public final Array<Entity> enemyProjectiles = new Array<>();

    public EntityManager(SpriteManager sprites) {
        player = new Player(sprites);
        player.setBulletCallback(bullet -> playerBullets.add(bullet));
    }

    public void updateAll(float delta) {
        // Update player bullets
        for (int i = playerBullets.size - 1; i >= 0; i--) {
            PlayerBullet b = playerBullets.get(i);
            b.update(delta);
            if (!b.alive) playerBullets.removeIndex(i);
        }

        // Update enemies
        for (int i = enemies.size - 1; i >= 0; i--) {
            Entity e = enemies.get(i);
            e.update(delta);
            if (!e.alive && e.isOffScreen()) enemies.removeIndex(i);
        }

        // Update enemy projectiles
        for (int i = enemyProjectiles.size - 1; i >= 0; i--) {
            Entity p = enemyProjectiles.get(i);
            p.update(delta);
            if (!p.alive) enemyProjectiles.removeIndex(i);
        }
    }

    public void renderAll(SpriteBatch batch) {
        // Enemies
        for (Entity e : enemies) {
            e.render(batch);
        }

        // Enemy projectiles
        for (Entity p : enemyProjectiles) {
            p.render(batch);
        }

        // Player bullets
        for (PlayerBullet b : playerBullets) {
            b.render(batch);
        }

        // Player
        player.render(batch);
    }

    public void clear() {
        enemies.clear();
        playerBullets.clear();
        enemyProjectiles.clear();
    }

    public int getAliveEnemyCount() {
        int count = 0;
        for (Entity e : enemies) {
            if (e.alive) count++;
        }
        return count;
    }
}
