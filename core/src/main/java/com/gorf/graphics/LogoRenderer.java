package com.gorf.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.gorf.Constants;

public class LogoRenderer {
    private static final Color[] STROBE_COLORS = {
        new Color(1f, 0.2f, 0.2f, 1f),
        new Color(1f, 0.6f, 0.1f, 1f),
        new Color(1f, 1f, 0.2f, 1f),
        new Color(0.2f, 1f, 0.2f, 1f),
        new Color(0.2f, 0.8f, 1f, 1f),
        new Color(0.4f, 0.4f, 1f, 1f),
        new Color(0.8f, 0.2f, 1f, 1f),
        new Color(1f, 0.4f, 0.8f, 1f),
    };

    private final SpriteManager sprites;
    private final float scale;

    public LogoRenderer(SpriteManager sprites) {
        this.sprites = sprites;
        this.scale = 4f; // 24px letters * 4 = 96px each
    }

    public void render(SpriteBatch batch, float centerX, float centerY, float time) {
        TextureRegion g = sprites.getFrame("logo_G");
        TextureRegion o = sprites.getFrame("logo_O");
        TextureRegion r = sprites.getFrame("logo_R");
        TextureRegion f = sprites.getFrame("logo_F");

        if (g == null || o == null || r == null || f == null) return;

        float letterW = g.getRegionWidth() * scale;
        float letterH = g.getRegionHeight() * scale;
        float gap = 8f * scale;
        float totalW = letterW * 4 + gap * 3;
        float startX = centerX - totalW / 2f;

        // Subtle pulse animation
        float pulse = 1.0f + 0.03f * MathUtils.sin(time * 2f);
        float pScale = scale * pulse;
        float pLetterW = g.getRegionWidth() * pScale;
        float pLetterH = g.getRegionHeight() * pScale;
        float pTotalW = pLetterW * 4 + gap * 3;
        float pStartX = centerX - pTotalW / 2f;

        TextureRegion[] letters = {g, o, r, f};

        // Shadow pass
        for (int i = 0; i < 4; i++) {
            float lx = pStartX + i * (pLetterW + gap);
            batch.setColor(0.1f, 0.0f, 0.2f, 0.6f);
            batch.draw(letters[i], lx + 4, centerY - pLetterH / 2f - 4, pLetterW, pLetterH);
        }

        // Glow pass
        for (int i = 0; i < 4; i++) {
            float lx = pStartX + i * (pLetterW + gap);
            float ly = centerY - pLetterH / 2f;

            float charPhase = time * 6f + i * 1.5f;
            int colorIdx = ((int) charPhase) % STROBE_COLORS.length;
            Color c = STROBE_COLORS[colorIdx];

            batch.setColor(c.r, c.g, c.b, 0.3f);
            float glowOffset = 3f;
            batch.draw(letters[i], lx - glowOffset, ly, pLetterW, pLetterH);
            batch.draw(letters[i], lx + glowOffset, ly, pLetterW, pLetterH);
            batch.draw(letters[i], lx, ly - glowOffset, pLetterW, pLetterH);
            batch.draw(letters[i], lx, ly + glowOffset, pLetterW, pLetterH);
        }

        // Main pass with per-character strobing
        for (int i = 0; i < 4; i++) {
            float lx = pStartX + i * (pLetterW + gap);
            float ly = centerY - pLetterH / 2f;

            float charPhase = time * 6f + i * 1.5f;
            int colorIdx = ((int) charPhase) % STROBE_COLORS.length;
            Color c1 = STROBE_COLORS[colorIdx];
            Color c2 = STROBE_COLORS[(colorIdx + 1) % STROBE_COLORS.length];
            float t = charPhase - (float) Math.floor(charPhase);

            batch.setColor(
                MathUtils.lerp(c1.r, c2.r, t),
                MathUtils.lerp(c1.g, c2.g, t),
                MathUtils.lerp(c1.b, c2.b, t),
                1f
            );
            batch.draw(letters[i], lx, ly, pLetterW, pLetterH);
        }

        batch.setColor(Color.WHITE);
    }
}
