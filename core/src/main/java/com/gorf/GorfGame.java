package com.gorf;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gorf.audio.SoundManager;
import com.gorf.graphics.SpriteManager;
import com.gorf.input.InputManager;
import com.gorf.scoring.HighScoreTable;
import com.gorf.screens.TitleScreen;

public class GorfGame extends Game {
    public SpriteBatch batch;
    public BitmapFont font;
    public InputManager inputManager;
    public SpriteManager sprites;
    public SoundManager sounds;
    public HighScoreTable highScores;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(1.5f);

        inputManager = new InputManager();
        inputManager.initialize();
        sprites = new SpriteManager();
        sprites.load();
        sounds = new SoundManager();
        sounds.load();
        highScores = new HighScoreTable();

        // Restore fullscreen preference
        Preferences prefs = Gdx.app.getPreferences("Gorf");
        if (prefs.getBoolean("fullscreen", false)) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }

        setScreen(new TitleScreen(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        sprites.dispose();
        sounds.dispose();
        super.dispose();
    }
}
