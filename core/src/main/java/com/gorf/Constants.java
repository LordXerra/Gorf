package com.gorf;

public class Constants {
    // Version
    public static final String VERSION = "V0.07";

    // Window
    public static final int WINDOW_WIDTH = 1024;
    public static final int WINDOW_HEIGHT = 768;
    public static final int VIRTUAL_WIDTH = 1024;
    public static final int VIRTUAL_HEIGHT = 768;

    // Play area: player confined to lower third
    public static final int PLAYER_ZONE_TOP = VIRTUAL_HEIGHT / 3;

    // Player
    public static final float PLAYER_SPEED = 300f;
    public static final float BULLET_SPEED = 600f;
    public static final int STARTING_LIVES = 3;

    // Scoring - Mission 1 (Astro Battles)
    public static final int SCORE_INVADER = 50;
    public static final int SCORE_LARGE_UFO = 100;
    public static final int SCORE_SMALL_UFO = 200;
    public static final int SCORE_GORFIAN_ROBOT = 250;

    // Scoring - Mission 2 (Laser Attack)
    public static final int SCORE_DIVE_BOMBER = 100;
    public static final int SCORE_LASER_CANNON = 300;
    public static final int SCORE_MINI_ROBOT = 300;

    // Scoring - Mission 3 (Galaxians)
    public static final int SCORE_GALAXIAN_FORMATION = 50;
    public static final int SCORE_GALAXIAN_YELLOW = 60;
    public static final int SCORE_GALAXIAN_BLUE = 80;
    public static final int SCORE_GALAXIAN_RED = 100;
    public static final int SCORE_GALAXIAN_FLAGSHIP = 300;

    // Scoring - Mission 4 (Space Warp)
    public static final int SCORE_WARP_ENEMY = 100;

    // Scoring - Mission 5 (Flag Ship)
    public static final int SCORE_HULL_HIT = 20;
    public static final int SCORE_ESCORT = 100;
    public static final int SCORE_DEBRIS = 150;
    public static final int SCORE_FLAGSHIP_DESTROY = 1000;

    // Input
    public static final float CONTROLLER_DEADZONE = 0.25f;

    // Title screen
    public static final float ATTRACT_PHASE_DURATION = 6.0f;
    public static final int STAR_COUNT = 200;
    public static final float STROBE_SPEED = 0.08f;

    // Sprite scale (original sprites are small pixel art)
    public static final int SPRITE_SCALE = 3;

    // Rank names
    public static final String[] RANKS = {
        "SPACE CADET", "SPACE CAPTAIN", "SPACE COLONEL",
        "SPACE GENERAL", "SPACE WARRIOR", "SPACE AVENGER"
    };

    // Mission names
    public static final String[] MISSION_NAMES = {
        "ASTRO BATTLES", "LASER ATTACK", "GALAXIANS",
        "SPACE WARP", "FLAG SHIP"
    };
}
