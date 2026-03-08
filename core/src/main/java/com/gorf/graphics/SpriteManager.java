package com.gorf.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.HashMap;
import java.util.Map;

public class SpriteManager {
    private Texture sheet;
    private final Map<String, Animation<TextureRegion>> animations = new HashMap<>();
    private final Map<String, TextureRegion> staticFrames = new HashMap<>();

    public void load() {
        sheet = new Texture(Gdx.files.internal("sprites/gorf-sprites.png"));
        sheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        defineLogoFrames();
        defineInvaderFrames();
        defineGorfianRobotFrames();
        defineSmallEnemyFrames();
        definePlayerFrames();
        defineGalaxianFrames();
        defineWarpEnemyFrames();
        defineFlagshipFrames();
        defineExplosionFrames();
        defineBulletFrames();
        defineFlameFrames();

        Gdx.app.log("SpriteManager", "Loaded " + animations.size() + " animations, "
            + staticFrames.size() + " static frames");
    }

    // Row 0: GORF logo letters (y=23-62, each 24x40)
    private void defineLogoFrames() {
        staticFrames.put("logo_G", new TextureRegion(sheet, 86, 23, 24, 40));
        staticFrames.put("logo_O", new TextureRegion(sheet, 118, 23, 24, 40));
        staticFrames.put("logo_R", new TextureRegion(sheet, 150, 23, 24, 40));
        staticFrames.put("logo_F", new TextureRegion(sheet, 182, 23, 24, 40));
    }

    // Row 1: Space invader sprites (y=79-94, h=16)
    private void defineInvaderFrames() {
        TextureRegion frame1 = new TextureRegion(sheet, 99, 79, 13, 16);
        TextureRegion frame2 = new TextureRegion(sheet, 119, 79, 13, 16);
        staticFrames.put("invader_1", frame1);
        staticFrames.put("invader_2", frame2);

        Animation<TextureRegion> anim = new Animation<>(0.5f, frame1, frame2);
        anim.setPlayMode(Animation.PlayMode.LOOP);
        animations.put("invader_walk", anim);

        // Player bullet (thin vertical line)
        staticFrames.put("player_bullet", new TextureRegion(sheet, 140, 79, 1, 16));

        // Additional invader variants
        staticFrames.put("invader_3", new TextureRegion(sheet, 149, 79, 11, 16));
        staticFrames.put("invader_4", new TextureRegion(sheet, 165, 79, 11, 16));
    }

    // Row 2: Gorfian Robot (y=116-135, h=20)
    private void defineGorfianRobotFrames() {
        TextureRegion f1 = new TextureRegion(sheet, 91, 116, 15, 20);
        TextureRegion f2 = new TextureRegion(sheet, 113, 116, 15, 20);
        TextureRegion f3 = new TextureRegion(sheet, 149, 116, 15, 20);
        TextureRegion f4 = new TextureRegion(sheet, 171, 116, 15, 20);
        staticFrames.put("gorfian_robot_1", f1);
        staticFrames.put("gorfian_robot_2", f2);

        Animation<TextureRegion> anim = new Animation<>(0.3f, f1, f2, f3, f4);
        anim.setPlayMode(Animation.PlayMode.LOOP);
        animations.put("gorfian_robot", anim);
    }

    // Rows 3-4: Small enemies, UFOs, projectiles (y=163-185, h=8 each)
    private void defineSmallEnemyFrames() {
        // Row 3 invader types (3 size pairs = 6 sprites for animation frames)
        staticFrames.put("small_invader_1a", new TextureRegion(sheet, 57, 163, 8, 8));
        staticFrames.put("small_invader_1b", new TextureRegion(sheet, 70, 163, 8, 8));
        staticFrames.put("small_invader_2a", new TextureRegion(sheet, 82, 163, 11, 8));
        staticFrames.put("small_invader_2b", new TextureRegion(sheet, 97, 163, 11, 8));
        staticFrames.put("small_invader_3a", new TextureRegion(sheet, 111, 163, 12, 8));
        staticFrames.put("small_invader_3b", new TextureRegion(sheet, 126, 163, 12, 8));

        // UFO sprites
        staticFrames.put("ufo_large", new TextureRegion(sheet, 142, 163, 16, 8));
        staticFrames.put("ufo_small", new TextureRegion(sheet, 161, 163, 11, 8));

        // Arrow/projectile sprites (thin 3px wide)
        staticFrames.put("enemy_bullet_1", new TextureRegion(sheet, 182, 163, 3, 8));
        staticFrames.put("enemy_bullet_2", new TextureRegion(sheet, 188, 163, 3, 8));
        staticFrames.put("enemy_bullet_3", new TextureRegion(sheet, 194, 163, 3, 8));
        staticFrames.put("enemy_bullet_4", new TextureRegion(sheet, 200, 163, 3, 8));

        // Force field segments
        staticFrames.put("force_field_1", new TextureRegion(sheet, 212, 163, 3, 8));
        staticFrames.put("force_field_2", new TextureRegion(sheet, 219, 163, 3, 8));
        staticFrames.put("force_field_3", new TextureRegion(sheet, 226, 163, 3, 8));
        staticFrames.put("force_field_4", new TextureRegion(sheet, 233, 163, 3, 8));
        staticFrames.put("force_field_end", new TextureRegion(sheet, 245, 163, 5, 8));

        // Row 4 - second frames for small invaders
        staticFrames.put("small_invader_4a", new TextureRegion(sheet, 57, 178, 8, 8));
        staticFrames.put("small_invader_4b", new TextureRegion(sheet, 70, 178, 8, 8));
        staticFrames.put("small_invader_5a", new TextureRegion(sheet, 82, 178, 11, 8));
        staticFrames.put("small_invader_5b", new TextureRegion(sheet, 97, 178, 11, 8));
        staticFrames.put("small_invader_6a", new TextureRegion(sheet, 111, 178, 12, 8));
        staticFrames.put("small_invader_6b", new TextureRegion(sheet, 126, 178, 12, 8));

        // Invader animations
        Animation<TextureRegion> small1 = new Animation<>(0.5f,
            new TextureRegion(sheet, 57, 163, 8, 8), new TextureRegion(sheet, 70, 163, 8, 8));
        small1.setPlayMode(Animation.PlayMode.LOOP);
        animations.put("small_invader_1", small1);

        Animation<TextureRegion> small2 = new Animation<>(0.5f,
            new TextureRegion(sheet, 82, 163, 11, 8), new TextureRegion(sheet, 97, 163, 11, 8));
        small2.setPlayMode(Animation.PlayMode.LOOP);
        animations.put("small_invader_2", small2);

        Animation<TextureRegion> small3 = new Animation<>(0.5f,
            new TextureRegion(sheet, 111, 163, 12, 8), new TextureRegion(sheet, 126, 163, 12, 8));
        small3.setPlayMode(Animation.PlayMode.LOOP);
        animations.put("small_invader_3", small3);

        // Enemy bullet animation
        Animation<TextureRegion> bulletAnim = new Animation<>(0.1f,
            new TextureRegion(sheet, 182, 163, 3, 8),
            new TextureRegion(sheet, 188, 163, 3, 8),
            new TextureRegion(sheet, 194, 163, 3, 8),
            new TextureRegion(sheet, 200, 163, 3, 8));
        bulletAnim.setPlayMode(Animation.PlayMode.LOOP);
        animations.put("enemy_bullet", bulletAnim);
    }

    // Row 5: Player ship and variants (y=206-217, h=12)
    private void definePlayerFrames() {
        staticFrames.put("player_ship", new TextureRegion(sheet, 76, 206, 9, 12));
        staticFrames.put("player_ship_2", new TextureRegion(sheet, 89, 206, 11, 12));

        // Dive bomber sprites (Mission 2)
        staticFrames.put("dive_bomber_1", new TextureRegion(sheet, 103, 206, 10, 12));
        staticFrames.put("dive_bomber_2", new TextureRegion(sheet, 116, 206, 10, 12));
        staticFrames.put("dive_bomber_3", new TextureRegion(sheet, 130, 206, 10, 12));

        // Laser cannon
        staticFrames.put("laser_cannon_1", new TextureRegion(sheet, 143, 206, 8, 12));
        staticFrames.put("laser_cannon_2", new TextureRegion(sheet, 154, 206, 8, 12));

        // Mini robot
        staticFrames.put("mini_robot_1", new TextureRegion(sheet, 165, 206, 10, 12));
        staticFrames.put("mini_robot_2", new TextureRegion(sheet, 179, 206, 10, 12));

        // Escort ships
        staticFrames.put("escort_1", new TextureRegion(sheet, 192, 206, 10, 12));
        staticFrames.put("escort_2", new TextureRegion(sheet, 205, 206, 11, 12));

        // Animations
        Animation<TextureRegion> diveBomber = new Animation<>(0.3f,
            new TextureRegion(sheet, 103, 206, 10, 12),
            new TextureRegion(sheet, 116, 206, 10, 12),
            new TextureRegion(sheet, 130, 206, 10, 12));
        diveBomber.setPlayMode(Animation.PlayMode.LOOP);
        animations.put("dive_bomber", diveBomber);

        Animation<TextureRegion> laserCannon = new Animation<>(0.4f,
            new TextureRegion(sheet, 143, 206, 8, 12),
            new TextureRegion(sheet, 154, 206, 8, 12));
        laserCannon.setPlayMode(Animation.PlayMode.LOOP);
        animations.put("laser_cannon", laserCannon);

        Animation<TextureRegion> miniRobot = new Animation<>(0.3f,
            new TextureRegion(sheet, 165, 206, 10, 12),
            new TextureRegion(sheet, 179, 206, 10, 12));
        miniRobot.setPlayMode(Animation.PlayMode.LOOP);
        animations.put("mini_robot", miniRobot);
    }

    // Rows 6-9: Galaxian enemies in 4 color variants (yellow, blue, red, white)
    private void defineGalaxianFrames() {
        String[] colors = {"yellow", "blue", "red", "white"};
        int[] rowYs = {236, 249, 265, 281};
        int[] rowHs = {12, 13, 13, 13};

        for (int c = 0; c < 4; c++) {
            String color = colors[c];
            int y = rowYs[c];
            int h = rowHs[c];

            // First few sprites are the main frames
            TextureRegion f1 = new TextureRegion(sheet, 82, y, 11, h);
            TextureRegion f2 = new TextureRegion(sheet, 96, y, 11, h);
            TextureRegion f3 = new TextureRegion(sheet, 110, y, 12, h);
            TextureRegion f4 = new TextureRegion(sheet, 124, y, 12, h);

            staticFrames.put("galaxian_" + color + "_1", f1);
            staticFrames.put("galaxian_" + color + "_2", f2);

            Animation<TextureRegion> anim = new Animation<>(0.3f, f1, f2, f3, f4);
            anim.setPlayMode(Animation.PlayMode.LOOP);
            animations.put("galaxian_" + color, anim);

            // Diving frames
            TextureRegion d1 = new TextureRegion(sheet, 137, y, 11, h);
            TextureRegion d2 = new TextureRegion(sheet, 151, y, 8, h);
            TextureRegion d3 = new TextureRegion(sheet, 163, y, 8, h);
            TextureRegion d4 = new TextureRegion(sheet, 174, y, 11, h);

            Animation<TextureRegion> diveAnim = new Animation<>(0.15f, d1, d2, d3, d4);
            diveAnim.setPlayMode(Animation.PlayMode.LOOP);
            animations.put("galaxian_" + color + "_dive", diveAnim);

            // Flagship-style larger frames at end of row
            staticFrames.put("galaxian_" + color + "_flagship_1", new TextureRegion(sheet, 186, y, 12, h));
            staticFrames.put("galaxian_" + color + "_flagship_2", new TextureRegion(sheet, 200, y, 12, h));
        }
    }

    // Row 10: Space Warp enemies at various scales (y=315-334, h=20)
    private void defineWarpEnemyFrames() {
        // Warp enemies grow from tiny to large as they spiral outward
        int[][] warpSprites = {
            {41, 4}, {48, 7}, {58, 9}, {70, 11},
            {98, 3}, {104, 5}, {113, 7}, {124, 11},
            {146, 3}, {152, 5}, {160, 9}, {172, 12},
            {187, 15}, {212, 3}, {218, 4}, {225, 5},
            {233, 6}, {242, 9}
        };
        for (int i = 0; i < warpSprites.length; i++) {
            staticFrames.put("warp_enemy_" + i,
                new TextureRegion(sheet, warpSprites[i][0], 315, warpSprites[i][1], 20));
        }

        // Create a growth animation (small to large)
        TextureRegion[] growthFrames = new TextureRegion[6];
        growthFrames[0] = new TextureRegion(sheet, 98, 315, 3, 20);
        growthFrames[1] = new TextureRegion(sheet, 104, 315, 5, 20);
        growthFrames[2] = new TextureRegion(sheet, 113, 315, 7, 20);
        growthFrames[3] = new TextureRegion(sheet, 124, 315, 11, 20);
        growthFrames[4] = new TextureRegion(sheet, 172, 315, 12, 20);
        growthFrames[5] = new TextureRegion(sheet, 187, 315, 15, 20);
        Animation<TextureRegion> growAnim = new Animation<>(0.15f, growthFrames);
        growAnim.setPlayMode(Animation.PlayMode.NORMAL);
        animations.put("warp_enemy_grow", growAnim);
    }

    // Row 12: Flagship (y=356-374, h=19)
    private void defineFlagshipFrames() {
        staticFrames.put("flagship_1", new TextureRegion(sheet, 10, 356, 64, 19));
        staticFrames.put("flagship_2", new TextureRegion(sheet, 84, 356, 64, 19));

        Animation<TextureRegion> flagAnim = new Animation<>(0.5f,
            new TextureRegion(sheet, 10, 356, 64, 19),
            new TextureRegion(sheet, 84, 356, 64, 19));
        flagAnim.setPlayMode(Animation.PlayMode.LOOP);
        animations.put("flagship", flagAnim);

        // Shield segments (growing sizes from the right side of the flagship row)
        staticFrames.put("shield_1", new TextureRegion(sheet, 164, 356, 1, 19));
        staticFrames.put("shield_2", new TextureRegion(sheet, 169, 356, 3, 19));
        staticFrames.put("shield_3", new TextureRegion(sheet, 175, 356, 3, 19));
        staticFrames.put("shield_4", new TextureRegion(sheet, 181, 356, 5, 19));
        staticFrames.put("shield_5", new TextureRegion(sheet, 189, 356, 5, 19));
        staticFrames.put("shield_6", new TextureRegion(sheet, 198, 356, 7, 19));
        staticFrames.put("shield_7", new TextureRegion(sheet, 208, 356, 7, 19));
        staticFrames.put("shield_8", new TextureRegion(sheet, 218, 356, 9, 19));
        staticFrames.put("shield_9", new TextureRegion(sheet, 230, 356, 9, 19));
        staticFrames.put("shield_10", new TextureRegion(sheet, 242, 356, 11, 19));
        staticFrames.put("shield_11", new TextureRegion(sheet, 256, 356, 11, 19));

        // Reactor core
        staticFrames.put("reactor_core", new TextureRegion(sheet, 271, 356, 5, 19));
    }

    // Row 13: Explosions (y=395-416, h=22)
    private void defineExplosionFrames() {
        TextureRegion[] frames = {
            new TextureRegion(sheet, 34, 395, 12, 22),
            new TextureRegion(sheet, 50, 395, 12, 22),
            new TextureRegion(sheet, 68, 395, 17, 22),
            new TextureRegion(sheet, 91, 395, 17, 22),
            new TextureRegion(sheet, 112, 395, 23, 22),
            new TextureRegion(sheet, 140, 395, 23, 22),
            new TextureRegion(sheet, 167, 395, 23, 22),
            new TextureRegion(sheet, 194, 395, 23, 22),
        };
        for (int i = 0; i < frames.length; i++) {
            staticFrames.put("explosion_" + i, frames[i]);
        }

        Animation<TextureRegion> anim = new Animation<>(0.06f, frames);
        anim.setPlayMode(Animation.PlayMode.NORMAL);
        animations.put("explosion", anim);

        // Fire/thrust effects (small sprites at end of row)
        staticFrames.put("thrust_1", new TextureRegion(sheet, 220, 395, 4, 22));
        staticFrames.put("thrust_2", new TextureRegion(sheet, 225, 395, 13, 22));
        staticFrames.put("thrust_3", new TextureRegion(sheet, 239, 395, 4, 22));
        staticFrames.put("thrust_4", new TextureRegion(sheet, 246, 395, 4, 22));
        staticFrames.put("thrust_5", new TextureRegion(sheet, 251, 395, 13, 22));
        staticFrames.put("thrust_6", new TextureRegion(sheet, 265, 395, 4, 22));
    }

    // Row 14: Small bullet/debris sprites (y=433-440, h=8)
    private void defineBulletFrames() {
        staticFrames.put("debris_1", new TextureRegion(sheet, 45, 433, 8, 8));
        staticFrames.put("debris_2", new TextureRegion(sheet, 57, 433, 11, 8));
        staticFrames.put("debris_3", new TextureRegion(sheet, 72, 433, 9, 8));
        staticFrames.put("debris_4", new TextureRegion(sheet, 87, 433, 11, 8));

        // Laser beam segments
        staticFrames.put("laser_beam_1", new TextureRegion(sheet, 82, 433, 2, 8));
        staticFrames.put("laser_beam_2", new TextureRegion(sheet, 101, 433, 3, 8));
    }

    // Row 15: Flame sprites (y=457-466, h=10)
    private void defineFlameFrames() {
        TextureRegion[] flames = {
            new TextureRegion(sheet, 136, 457, 6, 10),
            new TextureRegion(sheet, 144, 457, 6, 10),
            new TextureRegion(sheet, 152, 457, 6, 10),
            new TextureRegion(sheet, 160, 457, 6, 10),
        };
        Animation<TextureRegion> anim = new Animation<>(0.1f, flames);
        anim.setPlayMode(Animation.PlayMode.LOOP);
        animations.put("flame", anim);
        staticFrames.put("flame_1", flames[0]);
    }

    public Animation<TextureRegion> getAnimation(String name) {
        return animations.get(name);
    }

    public TextureRegion getFrame(String name) {
        return staticFrames.get(name);
    }

    public void dispose() {
        if (sheet != null) sheet.dispose();
    }
}
