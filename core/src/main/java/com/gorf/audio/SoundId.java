package com.gorf.audio;

public enum SoundId {
    PLAYER_FIRE("player-fire"),
    PLAYER_DEATH("player-death"),
    ENEMY_DEATH("enemy-death"),
    INVADER_HIT("invader-hit"),
    UFO_BONUS("ufo-bonus"),
    LASER_BEAM("laser-beam"),
    DIVE_BOMB("dive-bomb"),
    WARP_ENTRY("warp-entry"),
    WARP_ENEMY("warp-enemy"),
    FLAGSHIP_HIT("flagship-hit"),
    FLAGSHIP_DESTROY("flagship-destroy"),
    SHIELD_HIT("shield-hit"),
    DEBRIS_HIT("debris-hit"),
    MISSION_START("mission-start"),
    MISSION_COMPLETE("mission-complete"),
    GAME_START("game-start"),
    GAME_OVER("game-over"),
    HIGH_SCORE("high-score"),
    RANK_UP("rank-up"),
    EXTRA_LIFE("extra-life"),
    STARTUP("startup"),
    ENEMY_FIRE("enemy-fire"),
    AMBIENT_BLIP("ambient-blip"),
    AMBIENT_WARBLE("ambient-warble"),
    AMBIENT_ZAP("ambient-zap"),
    AMBIENT_PING("ambient-ping"),
    AMBIENT_SWEEP("ambient-sweep"),
    TITLE_AMBIENT("title-ambient");

    public final String filename;

    SoundId(String filename) {
        this.filename = filename;
    }
}
