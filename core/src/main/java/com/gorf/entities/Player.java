package com.gorf.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gorf.Constants;
import com.gorf.graphics.SpriteManager;
import com.gorf.input.InputManager;

public class Player extends Entity {
    private PlayerBullet activeBullet;
    private BulletSpawnCallback bulletCallback;
    private final SpriteManager sprites;

    public interface BulletSpawnCallback {
        void onSpawn(PlayerBullet bullet);
    }

    public Player(SpriteManager sprites) {
        this.sprites = sprites;
        currentFrame = sprites.getFrame("player_ship");
        updateBoundsFromFrame();
        respawn();
    }

    public void respawn() {
        x = Constants.VIRTUAL_WIDTH / 2f;
        y = 60;
        alive = true;
        activeBullet = null;
    }

    public void setBulletCallback(BulletSpawnCallback callback) {
        this.bulletCallback = callback;
    }

    public void update(float delta, InputManager input) {
        if (!alive) return;
        stateTime += delta;

        // 8-directional movement
        float mx = input.getMoveX();
        float my = input.getMoveY();

        // Normalize diagonal movement
        float len = (float) Math.sqrt(mx * mx + my * my);
        if (len > 1f) {
            mx /= len;
            my /= len;
        }

        x += mx * Constants.PLAYER_SPEED * delta;
        y += my * Constants.PLAYER_SPEED * delta;

        // Clamp to lower third of screen
        float halfW = width / 2f;
        float halfH = height / 2f;
        if (x - halfW < 0) x = halfW;
        if (x + halfW > Constants.VIRTUAL_WIDTH) x = Constants.VIRTUAL_WIDTH - halfW;
        if (y - halfH < 0) y = halfH;
        if (y + halfH > Constants.PLAYER_ZONE_TOP) y = Constants.PLAYER_ZONE_TOP - halfH;

        // Quark laser: fire on press, re-fire cancels old bullet
        if (input.isFireJustPressed()) {
            fire();
        }
    }

    @Override
    public void update(float delta) {
        // Use update(delta, input) instead
    }

    private void fire() {
        // Cancel existing bullet
        if (activeBullet != null && activeBullet.alive) {
            activeBullet.alive = false;
        }

        // Create new bullet
        activeBullet = new PlayerBullet(sprites, x, y + height / 2f);
        if (bulletCallback != null) {
            bulletCallback.onSpawn(activeBullet);
        }
    }

    public PlayerBullet getActiveBullet() {
        return activeBullet;
    }

    public void clearBullet() {
        activeBullet = null;
    }
}
