package com.gorf.audio;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Procedurally generates all game sound effects as WAV files.
 * Run this as a standalone tool or call generateAll() at first launch.
 */
public class SoundGenerator {
    private static final int SAMPLE_RATE = 44100;
    private static final int BITS_PER_SAMPLE = 16;

    public static void main(String[] args) {
        String outputDir = "assets/sounds";
        generateAll(outputDir);
    }

    public static void generateAll(String outputDir) {
        new File(outputDir).mkdirs();

        // Player sounds
        generate(outputDir, "player-fire", playerFire());
        generate(outputDir, "player-death", playerDeath());

        // Enemy death
        generate(outputDir, "enemy-death", enemyDeath());
        generate(outputDir, "invader-hit", invaderHit());

        // Mission-specific
        generate(outputDir, "ufo-bonus", ufoBonus());
        generate(outputDir, "laser-beam", laserBeam());
        generate(outputDir, "dive-bomb", diveBomb());
        generate(outputDir, "warp-entry", warpEntry());
        generate(outputDir, "warp-enemy", warpEnemy());
        generate(outputDir, "flagship-hit", flagshipHit());
        generate(outputDir, "flagship-destroy", flagshipDestroy());
        generate(outputDir, "shield-hit", shieldHit());
        generate(outputDir, "debris-hit", debrisHit());

        // Game events
        generate(outputDir, "mission-start", missionStart());
        generate(outputDir, "mission-complete", missionComplete());
        generate(outputDir, "game-start", gameStart());
        generate(outputDir, "game-over", gameOver());
        generate(outputDir, "high-score", highScore());
        generate(outputDir, "rank-up", rankUp());
        generate(outputDir, "extra-life", extraLife());
        generate(outputDir, "startup", startup());

        // Enemy fire
        generate(outputDir, "enemy-fire", enemyFire());

        // Ambient sounds for title screen
        generate(outputDir, "ambient-blip", ambientBlip());
        generate(outputDir, "ambient-warble", ambientWarble());
        generate(outputDir, "ambient-zap", ambientZap());
        generate(outputDir, "ambient-ping", ambientPing());
        generate(outputDir, "ambient-sweep", ambientSweep());

        System.out.println("Generated all sound effects in " + outputDir);
    }

    // Rising pitch sine sweep
    private static short[] playerFire() {
        int samples = (int)(SAMPLE_RATE * 0.08);
        short[] data = new short[samples];
        for (int i = 0; i < samples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double progress = (double) i / samples;
            double freq = 800 + 2000 * progress;
            double amp = 0.6 * (1.0 - progress * 0.5);
            data[i] = (short)(amp * Short.MAX_VALUE * Math.sin(2 * Math.PI * freq * t));
        }
        return data;
    }

    // Descending pitch with noise
    private static short[] playerDeath() {
        int samples = (int)(SAMPLE_RATE * 0.6);
        short[] data = new short[samples];
        for (int i = 0; i < samples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double progress = (double) i / samples;
            double freq = 800 * (1.0 - progress * 0.8);
            double envelope = 1.0 - progress;
            double sine = Math.sin(2 * Math.PI * freq * t);
            double noise = (Math.random() * 2 - 1) * progress * 0.4;
            data[i] = (short)(envelope * Short.MAX_VALUE * 0.5 * (sine + noise));
        }
        return data;
    }

    // Short burst with noise
    private static short[] enemyDeath() {
        int samples = (int)(SAMPLE_RATE * 0.12);
        short[] data = new short[samples];
        for (int i = 0; i < samples; i++) {
            double progress = (double) i / samples;
            double envelope = 1.0 - progress;
            double freq = 400 - 200 * progress;
            double sine = Math.sin(2 * Math.PI * freq * (double) i / SAMPLE_RATE);
            double noise = (Math.random() * 2 - 1) * 0.3;
            data[i] = (short)(envelope * Short.MAX_VALUE * 0.5 * (sine * 0.7 + noise * 0.3));
        }
        return data;
    }

    // Click + short tone
    private static short[] invaderHit() {
        int samples = (int)(SAMPLE_RATE * 0.06);
        short[] data = new short[samples];
        for (int i = 0; i < samples; i++) {
            double progress = (double) i / samples;
            double envelope = 1.0 - progress;
            double freq = 600;
            data[i] = (short)(envelope * Short.MAX_VALUE * 0.5
                * Math.sin(2 * Math.PI * freq * (double) i / SAMPLE_RATE));
        }
        return data;
    }

    // Warbling FM tone
    private static short[] ufoBonus() {
        int samples = (int)(SAMPLE_RATE * 0.3);
        short[] data = new short[samples];
        for (int i = 0; i < samples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double progress = (double) i / samples;
            double modFreq = 8;
            double modAmp = 200;
            double carrier = 600 + modAmp * Math.sin(2 * Math.PI * modFreq * t);
            double envelope = 0.6 * (1.0 - progress * 0.5);
            data[i] = (short)(envelope * Short.MAX_VALUE * Math.sin(2 * Math.PI * carrier * t));
        }
        return data;
    }

    // Sustained low buzz (sawtooth)
    private static short[] laserBeam() {
        int samples = (int)(SAMPLE_RATE * 0.3);
        short[] data = new short[samples];
        for (int i = 0; i < samples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double progress = (double) i / samples;
            double freq = 120;
            double sawtooth = 2.0 * (t * freq - Math.floor(t * freq + 0.5));
            double envelope = progress < 0.05 ? progress / 0.05 : (progress > 0.9 ? (1.0 - progress) / 0.1 : 1.0);
            data[i] = (short)(envelope * Short.MAX_VALUE * 0.4 * sawtooth);
        }
        return data;
    }

    // Descending swoosh
    private static short[] diveBomb() {
        int samples = (int)(SAMPLE_RATE * 0.2);
        short[] data = new short[samples];
        for (int i = 0; i < samples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double progress = (double) i / samples;
            double freq = 1200 * (1.0 - progress * 0.7);
            double envelope = 1.0 - progress;
            data[i] = (short)(envelope * Short.MAX_VALUE * 0.5
                * Math.sin(2 * Math.PI * freq * t));
        }
        return data;
    }

    // Rising whoosh with filtered noise
    private static short[] warpEntry() {
        int samples = (int)(SAMPLE_RATE * 0.5);
        short[] data = new short[samples];
        for (int i = 0; i < samples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double progress = (double) i / samples;
            double freq = 200 + 1000 * progress;
            double sine = Math.sin(2 * Math.PI * freq * t);
            double noise = (Math.random() * 2 - 1) * 0.2 * progress;
            double envelope = progress < 0.1 ? progress / 0.1 : 1.0;
            data[i] = (short)(envelope * Short.MAX_VALUE * 0.4 * (sine * 0.7 + noise * 0.3));
        }
        return data;
    }

    // Short alien warble
    private static short[] warpEnemy() {
        int samples = (int)(SAMPLE_RATE * 0.15);
        short[] data = new short[samples];
        for (int i = 0; i < samples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double progress = (double) i / samples;
            double freq = 500 + 300 * Math.sin(2 * Math.PI * 15 * t);
            double envelope = 1.0 - progress;
            data[i] = (short)(envelope * Short.MAX_VALUE * 0.4
                * Math.sin(2 * Math.PI * freq * t));
        }
        return data;
    }

    // Metallic clang
    private static short[] flagshipHit() {
        int samples = (int)(SAMPLE_RATE * 0.15);
        short[] data = new short[samples];
        for (int i = 0; i < samples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double progress = (double) i / samples;
            double envelope = Math.exp(-progress * 8);
            double tone = Math.sin(2 * Math.PI * 1200 * t) * 0.5
                        + Math.sin(2 * Math.PI * 2400 * t) * 0.3
                        + Math.sin(2 * Math.PI * 3600 * t) * 0.2;
            data[i] = (short)(envelope * Short.MAX_VALUE * 0.5 * tone);
        }
        return data;
    }

    // Long cascading explosion
    private static short[] flagshipDestroy() {
        int samples = (int)(SAMPLE_RATE * 1.2);
        short[] data = new short[samples];
        for (int i = 0; i < samples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double progress = (double) i / samples;
            double envelope = (1.0 - progress) * (progress < 0.05 ? progress / 0.05 : 1.0);
            double freq = 300 * (1.0 - progress * 0.6);
            double sine = Math.sin(2 * Math.PI * freq * t);
            double noise = (Math.random() * 2 - 1);
            double mix = sine * (1.0 - progress) + noise * progress;
            data[i] = (short)(envelope * Short.MAX_VALUE * 0.5 * mix);
        }
        return data;
    }

    // Short metallic ping
    private static short[] shieldHit() {
        int samples = (int)(SAMPLE_RATE * 0.08);
        short[] data = new short[samples];
        for (int i = 0; i < samples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double progress = (double) i / samples;
            double envelope = Math.exp(-progress * 12);
            data[i] = (short)(envelope * Short.MAX_VALUE * 0.4
                * (Math.sin(2 * Math.PI * 2000 * t) + Math.sin(2 * Math.PI * 3000 * t) * 0.5));
        }
        return data;
    }

    // Crunch sound
    private static short[] debrisHit() {
        int samples = (int)(SAMPLE_RATE * 0.1);
        short[] data = new short[samples];
        for (int i = 0; i < samples; i++) {
            double progress = (double) i / samples;
            double envelope = 1.0 - progress;
            double noise = (Math.random() * 2 - 1);
            data[i] = (short)(envelope * Short.MAX_VALUE * 0.4 * noise);
        }
        return data;
    }

    // Ascending 3-note arpeggio
    private static short[] missionStart() {
        return arpeggio(new double[]{440, 554, 659}, 0.1, 0.5);
    }

    // Triumphant ascending 5-note arpeggio
    private static short[] missionComplete() {
        return arpeggio(new double[]{523, 659, 784, 880, 1047}, 0.1, 0.5);
    }

    // Fanfare
    private static short[] gameStart() {
        return arpeggio(new double[]{440, 554, 659, 880}, 0.12, 0.6);
    }

    // Descending sad arpeggio
    private static short[] gameOver() {
        return arpeggio(new double[]{659, 554, 440, 330, 262}, 0.15, 0.5);
    }

    // Celebration jingle
    private static short[] highScore() {
        return arpeggio(new double[]{523, 659, 784, 1047, 784, 1047, 1319}, 0.1, 0.5);
    }

    // Ascending power-up
    private static short[] rankUp() {
        return arpeggio(new double[]{262, 330, 392, 523, 659}, 0.08, 0.6);
    }

    // Bright ascending chime
    private static short[] extraLife() {
        return arpeggio(new double[]{880, 1047, 1319, 1568}, 0.08, 0.5);
    }

    // Title intro sound
    private static short[] startup() {
        return arpeggio(new double[]{262, 330, 392, 523}, 0.15, 0.5);
    }

    // Short downward pew sound for enemy bullets
    private static short[] enemyFire() {
        int samples = (int)(SAMPLE_RATE * 0.06);
        short[] data = new short[samples];
        for (int i = 0; i < samples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double progress = (double) i / samples;
            double freq = 600 * (1.0 - progress * 0.5);
            double envelope = 1.0 - progress;
            data[i] = (short)(envelope * Short.MAX_VALUE * 0.3
                * Math.sin(2 * Math.PI * freq * t));
        }
        return data;
    }

    // Short electronic blip
    private static short[] ambientBlip() {
        int samples = (int)(SAMPLE_RATE * 0.05);
        short[] data = new short[samples];
        for (int i = 0; i < samples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double progress = (double) i / samples;
            double envelope = Math.exp(-progress * 10);
            data[i] = (short)(envelope * Short.MAX_VALUE * 0.3
                * Math.sin(2 * Math.PI * 1400 * t));
        }
        return data;
    }

    // Short FM warble
    private static short[] ambientWarble() {
        int samples = (int)(SAMPLE_RATE * 0.12);
        short[] data = new short[samples];
        for (int i = 0; i < samples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double progress = (double) i / samples;
            double modFreq = 20;
            double carrier = 800 + 400 * Math.sin(2 * Math.PI * modFreq * t);
            double envelope = Math.exp(-progress * 6);
            data[i] = (short)(envelope * Short.MAX_VALUE * 0.25
                * Math.sin(2 * Math.PI * carrier * t));
        }
        return data;
    }

    // Quick electric zap
    private static short[] ambientZap() {
        int samples = (int)(SAMPLE_RATE * 0.08);
        short[] data = new short[samples];
        for (int i = 0; i < samples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double progress = (double) i / samples;
            double freq = 2000 * (1.0 - progress * 0.8);
            double envelope = Math.exp(-progress * 8);
            double noise = (Math.random() * 2 - 1) * 0.15;
            data[i] = (short)(envelope * Short.MAX_VALUE * 0.25
                * (Math.sin(2 * Math.PI * freq * t) * 0.8 + noise));
        }
        return data;
    }

    // High-pitched ping
    private static short[] ambientPing() {
        int samples = (int)(SAMPLE_RATE * 0.15);
        short[] data = new short[samples];
        for (int i = 0; i < samples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double progress = (double) i / samples;
            double envelope = Math.exp(-progress * 5);
            data[i] = (short)(envelope * Short.MAX_VALUE * 0.2
                * (Math.sin(2 * Math.PI * 2200 * t) * 0.7
                 + Math.sin(2 * Math.PI * 3300 * t) * 0.3));
        }
        return data;
    }

    // Rising frequency sweep
    private static short[] ambientSweep() {
        int samples = (int)(SAMPLE_RATE * 0.2);
        short[] data = new short[samples];
        for (int i = 0; i < samples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double progress = (double) i / samples;
            double freq = 400 + 1200 * progress;
            double envelope = progress < 0.1 ? progress / 0.1
                            : (progress > 0.7 ? (1.0 - progress) / 0.3 : 1.0);
            data[i] = (short)(envelope * Short.MAX_VALUE * 0.2
                * Math.sin(2 * Math.PI * freq * t));
        }
        return data;
    }

    private static short[] arpeggio(double[] notes, double noteLen, double amp) {
        int noteSamples = (int)(SAMPLE_RATE * noteLen);
        int totalSamples = noteSamples * notes.length;
        short[] data = new short[totalSamples];

        for (int n = 0; n < notes.length; n++) {
            for (int i = 0; i < noteSamples; i++) {
                double t = (double) i / SAMPLE_RATE;
                double noteProgress = (double) i / noteSamples;
                double envelope = noteProgress < 0.05 ? noteProgress / 0.05
                                : (noteProgress > 0.7 ? (1.0 - noteProgress) / 0.3 : 1.0);
                int idx = n * noteSamples + i;
                data[idx] = (short)(envelope * amp * Short.MAX_VALUE
                    * Math.sin(2 * Math.PI * notes[n] * t));
            }
        }
        return data;
    }

    private static void generate(String dir, String name, short[] samples) {
        try {
            byte[] wavData = toWav(samples);
            File file = new File(dir, name + ".wav");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(wavData);
            }
            System.out.println("  Generated: " + name + ".wav (" + samples.length + " samples)");
        } catch (IOException e) {
            System.err.println("  Failed to generate: " + name + " - " + e.getMessage());
        }
    }

    private static byte[] toWav(short[] samples) {
        int dataSize = samples.length * 2;
        ByteBuffer buf = ByteBuffer.allocate(44 + dataSize);
        buf.order(ByteOrder.LITTLE_ENDIAN);

        // RIFF header
        buf.put("RIFF".getBytes());
        buf.putInt(36 + dataSize);
        buf.put("WAVE".getBytes());

        // fmt chunk
        buf.put("fmt ".getBytes());
        buf.putInt(16);           // chunk size
        buf.putShort((short) 1);  // PCM
        buf.putShort((short) 1);  // mono
        buf.putInt(SAMPLE_RATE);
        buf.putInt(SAMPLE_RATE * 2); // byte rate
        buf.putShort((short) 2);     // block align
        buf.putShort((short) BITS_PER_SAMPLE);

        // data chunk
        buf.put("data".getBytes());
        buf.putInt(dataSize);
        for (short s : samples) {
            buf.putShort(s);
        }

        return buf.array();
    }
}
