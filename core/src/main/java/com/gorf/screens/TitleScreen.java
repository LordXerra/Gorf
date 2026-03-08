package com.gorf.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.gorf.Constants;
import com.gorf.GorfGame;
import com.gorf.audio.SoundId;
import com.gorf.graphics.LogoRenderer;
import com.gorf.graphics.StarBackground;
import com.gorf.graphics.StrobingText;
import com.gorf.scoring.HighScoreEntry;

public class TitleScreen extends ScreenAdapter {
    private enum AttractPhase { TITLE, HIGH_SCORES, HELP, CREDITS }

    private final GorfGame game;
    private final FitViewport viewport;
    private final StarBackground stars;
    private final LogoRenderer logoRenderer;
    private final StrobingText strobingText;
    private final GlyphLayout layout;
    private final BitmapFont smallFont;

    private AttractPhase currentPhase = AttractPhase.TITLE;
    private float phaseTimer;
    private float globalTime;
    private float blinkTimer;
    private float ambientTimer;
    private long ambientLoopId = -1;

    private static final SoundId[] AMBIENT_SFX = {
        SoundId.AMBIENT_BLIP, SoundId.AMBIENT_WARBLE, SoundId.AMBIENT_ZAP,
        SoundId.AMBIENT_PING, SoundId.AMBIENT_SWEEP
    };

    public TitleScreen(GorfGame game) {
        this.game = game;
        this.viewport = new FitViewport(Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);
        this.stars = new StarBackground();
        this.logoRenderer = new LogoRenderer(game.sprites);
        this.strobingText = new StrobingText();
        this.layout = new GlyphLayout();
        this.smallFont = new BitmapFont();
        this.smallFont.getData().setScale(1.2f);

        // Start continuous ambient drone loop
        ambientLoopId = game.sounds.loop(SoundId.TITLE_AMBIENT, 0.7f);
    }

    @Override
    public void render(float delta) {
        globalTime += delta;
        phaseTimer += delta;
        blinkTimer += delta;

        // Update input and sounds
        game.inputManager.update();
        game.sounds.update(delta);

        // Occasional ambient SFX layered on top of the drone
        ambientTimer -= delta;
        if (ambientTimer <= 0) {
            ambientTimer = MathUtils.random(3.0f, 7.0f);
            SoundId snd = AMBIENT_SFX[MathUtils.random(AMBIENT_SFX.length - 1)];
            game.sounds.play(snd, 0.35f);
        }

        // Escape exits to desktop
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.sounds.stop(SoundId.TITLE_AMBIENT);
            Gdx.app.exit();
            return;
        }

        // Cycle attract phases
        if (phaseTimer >= Constants.ATTRACT_PHASE_DURATION) {
            phaseTimer = 0;
            currentPhase = switch (currentPhase) {
                case TITLE -> AttractPhase.HIGH_SCORES;
                case HIGH_SCORES -> AttractPhase.HELP;
                case HELP -> AttractPhase.CREDITS;
                case CREDITS -> AttractPhase.TITLE;
            };
        }

        // F1 fullscreen toggle
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            toggleFullscreen();
        }

        // Start game
        if (game.inputManager.isStartJustPressed() || game.inputManager.isFireJustPressed()) {
            game.sounds.stop(SoundId.TITLE_AMBIENT);
            game.setScreen(new GameScreen(game));
            return;
        }

        // Update stars
        stars.update(delta);

        // Render
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        game.batch.setProjectionMatrix(viewport.getCamera().combined);
        game.batch.begin();

        stars.render(game.batch);

        switch (currentPhase) {
            case TITLE -> renderTitlePhase();
            case HIGH_SCORES -> renderHighScoresPhase();
            case HELP -> renderHelpPhase();
            case CREDITS -> renderCreditsPhase();
        }

        // Always show version in bottom-right
        smallFont.setColor(0.5f, 0.5f, 0.5f, 1f);
        layout.setText(smallFont, Constants.VERSION);
        smallFont.draw(game.batch, Constants.VERSION,
            Constants.VIRTUAL_WIDTH - layout.width - 10,
            layout.height + 10);
        smallFont.setColor(Color.WHITE);

        game.batch.end();
    }

    private void renderTitlePhase() {
        float cx = Constants.VIRTUAL_WIDTH / 2f;

        // GORF logo
        logoRenderer.render(game.batch, cx, Constants.VIRTUAL_HEIGHT - 180, globalTime);

        // "PRESS START" blinking
        if (((int) (blinkTimer * 2)) % 2 == 0) {
            game.font.setColor(Color.WHITE);
            layout.setText(game.font, "PRESS START");
            game.font.draw(game.batch, "PRESS START",
                cx - layout.width / 2f, 200);
        }

        // Control hint
        smallFont.setColor(0.6f, 0.6f, 0.6f, 1f);
        String controlHint = game.inputManager.hasController()
            ? "CONTROLLER DETECTED - CROSS TO FIRE, OPTIONS TO START"
            : "WASD TO MOVE, SPACE TO FIRE, ENTER TO START";
        layout.setText(smallFont, controlHint);
        smallFont.draw(game.batch, controlHint, cx - layout.width / 2f, 140);

        // Fullscreen hint
        String fsHint = Gdx.graphics.isFullscreen() ? "F1: WINDOWED" : "F1: FULLSCREEN";
        layout.setText(smallFont, fsHint);
        smallFont.draw(game.batch, fsHint, cx - layout.width / 2f, 110);
        smallFont.setColor(Color.WHITE);
    }

    private void renderHighScoresPhase() {
        float cx = Constants.VIRTUAL_WIDTH / 2f;

        // Title
        strobingText.drawCentered(game.batch, game.font, "HIGH SCORES",
            cx, Constants.VIRTUAL_HEIGHT - 80, globalTime, 0);

        // Score entries
        float y = Constants.VIRTUAL_HEIGHT - 140;
        float lineHeight = 40;
        for (int i = 0; i < game.highScores.getEntryCount(); i++) {
            HighScoreEntry entry = game.highScores.getEntry(i);
            String line = String.format("%2d. %s  %,d", i + 1, entry.initials, entry.score);
            strobingText.drawCentered(game.batch, smallFont, line,
                cx, y - i * lineHeight, globalTime, i * 0.3f);
        }
    }

    private void renderHelpPhase() {
        float cx = Constants.VIRTUAL_WIDTH / 2f;
        float y = Constants.VIRTUAL_HEIGHT - 80;
        float lineH = 35;

        strobingText.drawCentered(game.batch, game.font, "HOW TO PLAY",
            cx, y, globalTime, 0);

        y -= 60;
        String[] lines = {
            "DESTROY ALL ENEMIES ACROSS 5 MISSIONS",
            "",
            "MISSION 1: ASTRO BATTLES - SPACE INVADERS",
            "MISSION 2: LASER ATTACK - DODGE THE LASERS",
            "MISSION 3: GALAXIANS - DIVE BOMBERS",
            "MISSION 4: SPACE WARP - SURVIVE THE VORTEX",
            "MISSION 5: FLAG SHIP - DESTROY THE BOSS",
            "",
            "KEYBOARD: WASD MOVE, SPACE FIRE",
            "CONTROLLER: D-PAD/STICK MOVE, CROSS FIRE",
            "",
            "ONLY ONE SHOT AT A TIME!",
            "FIRING AGAIN CANCELS YOUR PREVIOUS SHOT"
        };

        for (int i = 0; i < lines.length; i++) {
            if (!lines[i].isEmpty()) {
                smallFont.setColor(0.7f, 0.7f, 0.9f, 1f);
                layout.setText(smallFont, lines[i]);
                smallFont.draw(game.batch, lines[i], cx - layout.width / 2f, y - i * lineH);
            }
        }
        smallFont.setColor(Color.WHITE);
    }

    private void renderCreditsPhase() {
        float cx = Constants.VIRTUAL_WIDTH / 2f;
        float y = Constants.VIRTUAL_HEIGHT - 80;

        strobingText.drawCentered(game.batch, game.font, "CREDITS",
            cx, y, globalTime, 0);

        y -= 80;
        float lineH = 50;
        String[] credits = {
            "CREATED BY TONY BRICE",
            "TECHNICAL SUPPORT BY AARON THORNE",
            "TESTING BY TRIONA MELHUISH,",
            "AJ BRICE AND NAILESH SHETH"
        };

        for (int i = 0; i < credits.length; i++) {
            strobingText.drawCentered(game.batch, smallFont, credits[i],
                cx, y - i * lineH, globalTime, i * 0.5f);
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
        game.sounds.stop(SoundId.TITLE_AMBIENT);
        stars.dispose();
        smallFont.dispose();
    }
}
