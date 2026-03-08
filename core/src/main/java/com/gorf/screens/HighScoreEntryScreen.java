package com.gorf.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.gorf.Constants;
import com.gorf.GorfGame;
import com.gorf.graphics.StarBackground;
import com.gorf.graphics.StrobingText;

public class HighScoreEntryScreen extends ScreenAdapter {
    private final GorfGame game;
    private final FitViewport viewport;
    private final StarBackground stars;
    private final StrobingText strobe;
    private final GlyphLayout layout;
    private final BitmapFont bigFont;
    private final int score;

    private final char[] initials = {'A', 'A', 'A'};
    private int cursorPos = 0;
    private float blinkTimer;
    private float globalTime;

    // Repeat delay for held keys
    private float repeatTimer;
    private static final float REPEAT_DELAY = 0.15f;

    public HighScoreEntryScreen(GorfGame game, int score) {
        this.game = game;
        this.score = score;
        viewport = new FitViewport(Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);
        stars = new StarBackground();
        strobe = new StrobingText();
        layout = new GlyphLayout();
        bigFont = new BitmapFont();
        bigFont.getData().setScale(3f);
    }

    @Override
    public void render(float delta) {
        globalTime += delta;
        blinkTimer += delta;
        stars.update(delta);
        game.inputManager.update();

        handleInput(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        game.batch.setProjectionMatrix(viewport.getCamera().combined);
        game.batch.begin();

        stars.render(game.batch);

        float cx = Constants.VIRTUAL_WIDTH / 2f;

        // Title
        strobe.drawCentered(game.batch, game.font, "NEW HIGH SCORE!",
            cx, Constants.VIRTUAL_HEIGHT - 120, globalTime, 0);

        // Score
        game.font.setColor(Color.WHITE);
        String scoreText = String.format("%,d", score);
        layout.setText(game.font, scoreText);
        game.font.draw(game.batch, scoreText, cx - layout.width / 2f, Constants.VIRTUAL_HEIGHT - 180);

        // Initials
        String initialsStr = new String(initials);
        layout.setText(bigFont, initialsStr);
        float initialsX = cx - layout.width / 2f;
        float initialsY = Constants.VIRTUAL_HEIGHT / 2f + 20;

        for (int i = 0; i < 3; i++) {
            String ch = String.valueOf(initials[i]);
            layout.setText(bigFont, ch);
            float charX = initialsX + i * (layout.width + 20);

            if (i == cursorPos && ((int)(blinkTimer * 3)) % 2 == 0) {
                bigFont.setColor(Color.YELLOW);
            } else {
                bigFont.setColor(Color.WHITE);
            }
            bigFont.draw(game.batch, ch, charX, initialsY);
        }

        // Instructions
        game.font.setColor(0.6f, 0.6f, 0.6f, 1f);
        String hint = "UP/DOWN TO CHANGE   LEFT/RIGHT TO MOVE   START TO CONFIRM";
        layout.setText(game.font, hint);
        game.font.draw(game.batch, hint, cx - layout.width / 2f, 150);
        game.font.setColor(Color.WHITE);

        game.batch.end();
    }

    private void handleInput(float delta) {
        repeatTimer -= delta;
        boolean canRepeat = repeatTimer <= 0;

        // Up/Down changes current letter
        if (game.inputManager.isUpPressed() && canRepeat) {
            initials[cursorPos]++;
            if (initials[cursorPos] > 'Z') initials[cursorPos] = 'A';
            repeatTimer = REPEAT_DELAY;
        }
        if (game.inputManager.isDownPressed() && canRepeat) {
            initials[cursorPos]--;
            if (initials[cursorPos] < 'A') initials[cursorPos] = 'Z';
            repeatTimer = REPEAT_DELAY;
        }

        // Left/Right moves cursor
        if (game.inputManager.isRightPressed() && canRepeat) {
            cursorPos = Math.min(cursorPos + 1, 2);
            repeatTimer = REPEAT_DELAY;
        }
        if (game.inputManager.isLeftPressed() && canRepeat) {
            cursorPos = Math.max(cursorPos - 1, 0);
            repeatTimer = REPEAT_DELAY;
        }

        // Confirm
        if (game.inputManager.isStartJustPressed()) {
            game.highScores.insert(new String(initials), score);
            game.setScreen(new TitleScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        stars.dispose();
        bigFont.dispose();
    }
}
