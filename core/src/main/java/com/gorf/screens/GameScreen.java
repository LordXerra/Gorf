package com.gorf.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.gorf.Constants;
import com.gorf.GorfGame;
import com.gorf.audio.SoundId;
import com.gorf.entities.Entity;
import com.gorf.entities.enemies.GorfianRobot;
import com.gorf.entities.enemies.Invader;
import com.gorf.entities.enemies.UFO;
import com.gorf.entities.enemies.DiveBomber;
import com.gorf.entities.enemies.LaserCannon;
import com.gorf.entities.enemies.MiniRobot;
import com.gorf.missions.AstroBattlesMission;
import com.gorf.missions.LaserAttackMission;
import com.gorf.graphics.HudRenderer;
import com.gorf.graphics.StarBackground;
import com.gorf.graphics.StrobingText;
import com.gorf.missions.MissionManager;
import com.gorf.scoring.ScoreManager;
import com.gorf.systems.CollisionSystem;
import com.gorf.systems.EntityManager;
import com.gorf.systems.ParticleSystem;

public class GameScreen extends ScreenAdapter implements CollisionSystem.CollisionCallback {
    private final GorfGame game;
    private final FitViewport viewport;
    private final StarBackground stars;
    private final ScoreManager scoreManager;
    private final EntityManager entityManager;
    private final CollisionSystem collisionSystem;
    private final MissionManager missionManager;
    private final ParticleSystem particles;
    private final HudRenderer hud;
    private final StrobingText strobe;
    private final GlyphLayout layout;
    private final BitmapFont overlayFont;

    private enum PlayState {
        MISSION_INTRO, PLAYING, PLAYER_DYING, RESPAWNING, MISSION_CLEAR, GAME_OVER
    }

    private PlayState state = PlayState.MISSION_INTRO;
    private float stateTimer = 0;
    private float globalTime = 0;

    public GameScreen(GorfGame game) {
        this.game = game;
        viewport = new FitViewport(Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);
        stars = new StarBackground();
        scoreManager = new ScoreManager();
        entityManager = new EntityManager(game.sprites);
        collisionSystem = new CollisionSystem();
        missionManager = new MissionManager();
        particles = new ParticleSystem();
        hud = new HudRenderer(game.font);
        strobe = new StrobingText();
        layout = new GlyphLayout();
        overlayFont = new BitmapFont();
        overlayFont.getData().setScale(2.0f);

        // Register missions
        missionManager.setMission(0, new AstroBattlesMission());
        missionManager.setMission(1, new LaserAttackMission());
        // Missions 2-4 will be added in Phases 5-7

        game.sounds.playPriority(SoundId.GAME_START);
        startMission();
    }

    private void startMission() {
        state = PlayState.MISSION_INTRO;
        stateTimer = 0;
        entityManager.clear();
        entityManager.player.respawn();
        particles.clear();

        missionManager.initCurrentMission(entityManager, game.sprites, scoreManager);
        game.sounds.playPriority(SoundId.MISSION_START);
    }

    @Override
    public void render(float delta) {
        delta = Math.min(delta, 1 / 30f);
        globalTime += delta;
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        game.batch.setProjectionMatrix(viewport.getCamera().combined);
        game.batch.begin();

        stars.render(game.batch);

        // Mission-specific rendering (force field, warp tunnel, etc.)
        var mission = missionManager.getCurrentMission();
        if (mission != null) {
            mission.render(game.batch);
        }

        entityManager.renderAll(game.batch);
        particles.render(game.batch);
        hud.render(game.batch, scoreManager);
        renderStateOverlay();

        game.batch.end();
    }

    private void update(float delta) {
        game.inputManager.update();
        game.sounds.update(delta);
        stateTimer += delta;
        stars.update(delta);

        // F1 fullscreen toggle
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            toggleFullscreen();
        }

        // Escape quits to title
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new TitleScreen(game));
            return;
        }

        switch (state) {
            case MISSION_INTRO:
                if (stateTimer >= 2.5f) {
                    state = PlayState.PLAYING;
                    stateTimer = 0;
                }
                break;

            case PLAYING:
                updatePlaying(delta);
                break;

            case PLAYER_DYING:
                particles.update(delta);
                // Let enemies keep moving during death animation
                entityManager.updateAll(delta);
                if (stateTimer >= 1.5f) {
                    if (scoreManager.isGameOver()) {
                        state = PlayState.GAME_OVER;
                        stateTimer = 0;
                        game.sounds.playPriority(SoundId.GAME_OVER);
                    } else {
                        state = PlayState.RESPAWNING;
                        stateTimer = 0;
                    }
                }
                break;

            case RESPAWNING:
                entityManager.player.respawn();
                // Clear enemy projectiles for fairness
                for (Entity p : entityManager.enemyProjectiles) {
                    p.alive = false;
                }
                state = PlayState.PLAYING;
                stateTimer = 0;
                break;

            case MISSION_CLEAR:
                particles.update(delta);
                if (stateTimer >= 2.5f) {
                    missionManager.advanceToNextMission(scoreManager);

                    // Check for rank up / extra life
                    if (scoreManager.consumeExtraLifeFlag()) {
                        game.sounds.playPriority(SoundId.EXTRA_LIFE);
                    }

                    game.sounds.playPriority(SoundId.MISSION_COMPLETE);
                    startMission();
                }
                break;

            case GAME_OVER:
                particles.update(delta);
                if (stateTimer >= 3.0f && game.inputManager.isConfirmJustPressed()) {
                    int finalScore = scoreManager.getScore();
                    if (game.highScores.qualifies(finalScore)) {
                        game.sounds.playPriority(SoundId.HIGH_SCORE);
                        game.setScreen(new HighScoreEntryScreen(game, finalScore));
                    } else {
                        game.setScreen(new TitleScreen(game));
                    }
                }
                break;
        }
    }

    private void updatePlaying(float delta) {
        entityManager.player.update(delta, game.inputManager);

        // Update mission logic
        var mission = missionManager.getCurrentMission();
        if (mission != null) {
            mission.update(delta, entityManager);
        }

        entityManager.updateAll(delta);
        particles.update(delta);

        // Collision detection
        boolean playerDied = collisionSystem.checkAll(entityManager, this);
        if (playerDied) return;

        // Check mission complete
        if (missionManager.isMissionComplete(entityManager)
            && entityManager.enemyProjectiles.size == 0) {
            state = PlayState.MISSION_CLEAR;
            stateTimer = 0;
        }
    }

    // === CollisionCallback ===

    @Override
    public void onEnemyKilled(Entity enemy, float x, float y, int score) {
        // Determine score and explosion type based on enemy class
        int points = score;
        if (enemy instanceof Invader inv) {
            points = inv.getScoreValue();
            particles.spawnInvaderExplosion(x, y);
            game.sounds.play(SoundId.INVADER_HIT);
        } else if (enemy instanceof GorfianRobot gr) {
            points = gr.getScoreValue();
            particles.spawnEnemyExplosion(x, y);
            game.sounds.play(SoundId.ENEMY_DEATH);
        } else if (enemy instanceof UFO ufo) {
            points = ufo.getScoreValue();
            particles.spawnUFOExplosion(x, y);
            game.sounds.play(SoundId.UFO_BONUS);
        } else if (enemy instanceof DiveBomber db) {
            points = db.getScoreValue();
            particles.spawnEnemyExplosion(x, y);
            game.sounds.play(SoundId.ENEMY_DEATH);
        } else if (enemy instanceof LaserCannon lc) {
            points = lc.getScoreValue();
            particles.spawnEnemyExplosion(x, y);
            game.sounds.play(SoundId.ENEMY_DEATH);
        } else if (enemy instanceof MiniRobot mr) {
            points = mr.getScoreValue();
            particles.spawnEnemyExplosion(x, y);
            game.sounds.play(SoundId.ENEMY_DEATH);
        } else {
            if (points <= 0) points = Constants.SCORE_INVADER;
            particles.spawnEnemyExplosion(x, y);
            game.sounds.play(SoundId.ENEMY_DEATH);
        }
        scoreManager.addScore(points);
    }

    @Override
    public void onPlayerKilled() {
        particles.spawnPlayerExplosion(entityManager.player.x, entityManager.player.y);
        entityManager.player.alive = false;
        scoreManager.onPlayerDeath();
        state = PlayState.PLAYER_DYING;
        stateTimer = 0;
        game.sounds.playPriority(SoundId.PLAYER_DEATH);
    }

    @Override
    public void onProjectileDestroyed(Entity projectile) {
        particles.spawnSmallExplosion(projectile.x, projectile.y);
    }

    // === Overlay rendering ===

    private void renderStateOverlay() {
        float cx = Constants.VIRTUAL_WIDTH / 2f;
        float cy = Constants.VIRTUAL_HEIGHT / 2f;

        switch (state) {
            case MISSION_INTRO:
                strobe.drawCentered(game.batch, overlayFont,
                    "MISSION " + missionManager.getCurrentMissionNumber(),
                    cx, cy + 40, globalTime, 0);
                strobe.drawCentered(game.batch, game.font,
                    missionManager.getCurrentMissionName(),
                    cx, cy - 10, globalTime, 1f);
                game.font.setColor(Color.GRAY);
                layout.setText(game.font, scoreManager.getCurrentRank());
                game.font.draw(game.batch, scoreManager.getCurrentRank(),
                    cx - layout.width / 2f, cy - 50);
                game.font.setColor(Color.WHITE);
                break;

            case MISSION_CLEAR:
                strobe.drawCentered(game.batch, overlayFont, "MISSION CLEAR!",
                    cx, cy, globalTime, 0);
                break;

            case GAME_OVER:
                strobe.drawCentered(game.batch, overlayFont, "GAME OVER",
                    cx, cy + 30, globalTime, 0);
                game.font.setColor(Color.WHITE);
                String finalScore = String.format("FINAL SCORE: %,d", scoreManager.getScore());
                layout.setText(game.font, finalScore);
                game.font.draw(game.batch, finalScore, cx - layout.width / 2f, cy - 20);
                if (stateTimer >= 3.0f) {
                    game.font.setColor(Color.YELLOW);
                    layout.setText(game.font, "PRESS START");
                    game.font.draw(game.batch, "PRESS START", cx - layout.width / 2f, cy - 60);
                }
                game.font.setColor(Color.WHITE);
                break;

            default:
                break;
        }
    }

    private void toggleFullscreen() {
        if (Gdx.graphics.isFullscreen()) {
            Gdx.graphics.setWindowedMode(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        } else {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }
        Preferences prefs = Gdx.app.getPreferences("Gorf");
        prefs.putBoolean("fullscreen", Gdx.graphics.isFullscreen());
        prefs.flush();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        stars.dispose();
        particles.dispose();
        overlayFont.dispose();
    }
}
