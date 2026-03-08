package com.gorf.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gorf.Constants;
import com.gorf.scoring.ScoreManager;

public class HudRenderer {
    private final BitmapFont font;
    private final GlyphLayout layout;

    public HudRenderer(BitmapFont font) {
        this.font = font;
        this.layout = new GlyphLayout();
    }

    public void render(SpriteBatch batch, ScoreManager sm) {
        float y = Constants.VIRTUAL_HEIGHT - 10;

        // Score (top-left)
        font.setColor(Color.WHITE);
        String scoreText = String.format("SCORE: %,d", sm.getScore());
        font.draw(batch, scoreText, 15, y);

        // Lives (top-center)
        font.setColor(Color.CYAN);
        String livesText = "LIVES: " + sm.getLives();
        layout.setText(font, livesText);
        font.draw(batch, livesText, Constants.VIRTUAL_WIDTH / 2f - layout.width / 2f, y);

        // Rank (top-right)
        font.setColor(Color.YELLOW);
        String rankText = sm.getCurrentRank();
        layout.setText(font, rankText);
        font.draw(batch, rankText, Constants.VIRTUAL_WIDTH - layout.width - 15, y);

        // Mission info (below rank)
        font.setColor(0.7f, 0.7f, 0.7f, 1f);
        String missionText = "MISSION " + sm.getCurrentMissionNumber();
        layout.setText(font, missionText);
        font.draw(batch, missionText, Constants.VIRTUAL_WIDTH - layout.width - 15, y - 25);

        font.setColor(Color.WHITE);
    }
}
