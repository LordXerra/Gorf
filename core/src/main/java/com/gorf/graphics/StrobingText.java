package com.gorf.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class StrobingText {
    private static final Color[] STROBE_COLORS = {
        new Color(1f, 0.2f, 0.2f, 1f),   // Red
        new Color(1f, 0.6f, 0.1f, 1f),   // Orange
        new Color(1f, 1f, 0.2f, 1f),     // Yellow
        new Color(0.2f, 1f, 0.2f, 1f),   // Green
        new Color(0.2f, 0.8f, 1f, 1f),   // Cyan
        new Color(0.4f, 0.4f, 1f, 1f),   // Blue
        new Color(0.8f, 0.2f, 1f, 1f),   // Purple
        new Color(1f, 0.4f, 0.8f, 1f),   // Pink
    };

    private final GlyphLayout layout = new GlyphLayout();

    public void drawCentered(SpriteBatch batch, BitmapFont font, String text,
                             float centerX, float y, float time, float phaseOffset) {
        // Measure full text width for centering
        font.setColor(Color.WHITE);
        layout.setText(font, text);
        float totalWidth = layout.width;
        float startX = centerX - totalWidth / 2f;

        // Draw each character with its own color
        float xPos = startX;
        for (int i = 0; i < text.length(); i++) {
            String ch = String.valueOf(text.charAt(i));
            float charPhase = time * 8f + i * 0.5f + phaseOffset;
            int colorIdx = ((int) charPhase) % STROBE_COLORS.length;
            if (colorIdx < 0) colorIdx += STROBE_COLORS.length;

            // Lerp between current and next color
            Color c1 = STROBE_COLORS[colorIdx];
            Color c2 = STROBE_COLORS[(colorIdx + 1) % STROBE_COLORS.length];
            float t = charPhase - (float) Math.floor(charPhase);

            font.setColor(
                MathUtils.lerp(c1.r, c2.r, t),
                MathUtils.lerp(c1.g, c2.g, t),
                MathUtils.lerp(c1.b, c2.b, t),
                1f
            );

            layout.setText(font, ch);
            font.draw(batch, ch, xPos, y);
            xPos += layout.width;
        }
        font.setColor(Color.WHITE);
    }

    public void draw(SpriteBatch batch, BitmapFont font, String text,
                     float x, float y, float time, float phaseOffset) {
        float xPos = x;
        for (int i = 0; i < text.length(); i++) {
            String ch = String.valueOf(text.charAt(i));
            float charPhase = time * 8f + i * 0.5f + phaseOffset;
            int colorIdx = ((int) charPhase) % STROBE_COLORS.length;
            if (colorIdx < 0) colorIdx += STROBE_COLORS.length;

            Color c1 = STROBE_COLORS[colorIdx];
            Color c2 = STROBE_COLORS[(colorIdx + 1) % STROBE_COLORS.length];
            float t = charPhase - (float) Math.floor(charPhase);

            font.setColor(
                MathUtils.lerp(c1.r, c2.r, t),
                MathUtils.lerp(c1.g, c2.g, t),
                MathUtils.lerp(c1.b, c2.b, t),
                1f
            );

            layout.setText(font, ch);
            font.draw(batch, ch, xPos, y);
            xPos += layout.width;
        }
        font.setColor(Color.WHITE);
    }
}
