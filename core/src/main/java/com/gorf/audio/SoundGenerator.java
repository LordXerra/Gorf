package com.gorf.audio;

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

        // Title screen continuous ambient drone loop
        generate(outputDir, "title-ambient", titleAmbient());

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

    // Metallic grinding resonance - deep FM synthesis with inharmonic partials
    private static short[] ambientBlip() {
        int samples = (int)(SAMPLE_RATE * 0.8);
        short[] data = new short[samples];
        for (int i = 0; i < samples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double progress = (double) i / samples;
            // Slow attack, long decay
            double envelope = progress < 0.05 ? progress / 0.05
                            : Math.exp(-(progress - 0.05) * 3);
            // Metallic: inharmonic frequency ratios (bell-like)
            double f1 = Math.sin(2 * Math.PI * 180 * t);
            double f2 = Math.sin(2 * Math.PI * 293 * t) * 0.7; // non-integer ratio
            double f3 = Math.sin(2 * Math.PI * 467 * t) * 0.4;
            double f4 = Math.sin(2 * Math.PI * 587 * t) * 0.3;
            // Ring modulation for metallic grinding
            double ring = Math.sin(2 * Math.PI * 37 * t);
            double tone = (f1 + f2 + f3 + f4) * (0.6 + 0.4 * ring);
            data[i] = (short)(envelope * Short.MAX_VALUE * 0.5 * tone);
        }
        return data;
    }

    // Deep space drone with slow FM modulation - eerie and spacey
    private static short[] ambientWarble() {
        int samples = (int)(SAMPLE_RATE * 1.2);
        short[] data = new short[samples];
        for (int i = 0; i < samples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double progress = (double) i / samples;
            // Swell envelope
            double envelope = Math.sin(Math.PI * progress) * 0.9;
            // Deep carrier with slow wobble
            double modulator = Math.sin(2 * Math.PI * 3.5 * t) * 150;
            double carrier = 90 + modulator + 40 * Math.sin(2 * Math.PI * 0.7 * t);
            double tone = Math.sin(2 * Math.PI * carrier * t);
            // Add sub-bass rumble
            double sub = Math.sin(2 * Math.PI * 45 * t) * 0.3;
            // Add high shimmer
            double shimmer = Math.sin(2 * Math.PI * (carrier * 5.03) * t) * 0.15
                           * Math.sin(2 * Math.PI * 2.1 * t);
            data[i] = (short)(envelope * Short.MAX_VALUE * 0.5 * (tone * 0.6 + sub + shimmer));
        }
        return data;
    }

    // Industrial electric arc - harsh crackling with resonant filter sweep
    private static short[] ambientZap() {
        int samples = (int)(SAMPLE_RATE * 0.6);
        short[] data = new short[samples];
        double phase = 0;
        for (int i = 0; i < samples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double progress = (double) i / samples;
            // Sharp attack, medium decay
            double envelope = progress < 0.02 ? progress / 0.02
                            : Math.exp(-(progress - 0.02) * 4) * 0.8;
            // Descending sawtooth with noise injection
            double freq = 800 * (1.0 - progress * 0.6);
            phase += freq / SAMPLE_RATE;
            double sawtooth = 2.0 * (phase - Math.floor(phase + 0.5));
            // Harsh noise bursts
            double noise = (Math.random() * 2 - 1);
            double crackle = noise * Math.abs(sawtooth) * 0.5;
            // Resonant peak that sweeps down
            double resFreq = 2000 * (1.0 - progress * 0.7);
            double resonance = Math.sin(2 * Math.PI * resFreq * t) * 0.3;
            data[i] = (short)(envelope * Short.MAX_VALUE * 0.55
                * (sawtooth * 0.4 + crackle + resonance));
        }
        return data;
    }

    // Futuristic sonar ping with metallic reverb tail
    private static short[] ambientPing() {
        int samples = (int)(SAMPLE_RATE * 1.0);
        short[] data = new short[samples];
        for (int i = 0; i < samples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double progress = (double) i / samples;
            // Sharp attack, long metallic decay
            double envelope = progress < 0.01 ? progress / 0.01
                            : Math.exp(-progress * 2.5);
            // Primary ping tone
            double ping = Math.sin(2 * Math.PI * 1800 * t);
            // Inharmonic metallic overtones (bell partials)
            double metal1 = Math.sin(2 * Math.PI * 2897 * t) * 0.5 * Math.exp(-progress * 4);
            double metal2 = Math.sin(2 * Math.PI * 4350 * t) * 0.3 * Math.exp(-progress * 6);
            double metal3 = Math.sin(2 * Math.PI * 1123 * t) * 0.4 * Math.exp(-progress * 3);
            // Subtle pitch bend down for spacey feel
            double bend = Math.sin(2 * Math.PI * (1800 - 200 * progress) * t) * 0.2 * progress;
            data[i] = (short)(envelope * Short.MAX_VALUE * 0.5
                * (ping * 0.4 + metal1 + metal2 + metal3 + bend));
        }
        return data;
    }

    // Massive frequency sweep with grinding harmonics - spaceship flyby
    private static short[] ambientSweep() {
        int samples = (int)(SAMPLE_RATE * 1.5);
        short[] data = new short[samples];
        double phase1 = 0, phase2 = 0;
        for (int i = 0; i < samples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double progress = (double) i / samples;
            // Swell in and out
            double envelope = Math.sin(Math.PI * progress);
            // Rising sweep from deep bass to mid, then back down
            double freq = 60 + 400 * Math.sin(Math.PI * progress);
            phase1 += freq / SAMPLE_RATE;
            phase2 += (freq * 2.01) / SAMPLE_RATE; // slightly detuned for thickness
            // Sawtooth for grinding texture
            double saw1 = 2.0 * (phase1 - Math.floor(phase1 + 0.5));
            double saw2 = 2.0 * (phase2 - Math.floor(phase2 + 0.5));
            // Filtered noise layer
            double noise = (Math.random() * 2 - 1) * 0.15 * (0.5 + 0.5 * Math.sin(Math.PI * progress));
            // Sub rumble
            double sub = Math.sin(2 * Math.PI * 35 * t) * 0.3 * envelope;
            data[i] = (short)(envelope * Short.MAX_VALUE * 0.5
                * (saw1 * 0.35 + saw2 * 0.25 + noise + sub));
        }
        return data;
    }

    // 12-second metallic space drone loop for title screen
    // Layers: sub-bass pulse, metallic sawtooth grind, FM space choir, high metallic shimmer
    private static short[] titleAmbient() {
        double duration = 12.0;
        int samples = (int)(SAMPLE_RATE * duration);
        short[] data = new short[samples];

        // Phase accumulators for continuous waveforms
        double phase1 = 0, phase2 = 0;

        for (int i = 0; i < samples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double progress = (double) i / samples;

            // Crossfade envelope for seamless looping (fade first/last 0.5s)
            double loopEnv = 1.0;
            double fadeTime = 0.5;
            double fadeFrac = fadeTime / duration;
            if (progress < fadeFrac) loopEnv = progress / fadeFrac;
            else if (progress > 1.0 - fadeFrac) loopEnv = (1.0 - progress) / fadeFrac;

            // === Layer 1: Sub-bass pulse (slow throb) ===
            double subFreq = 40 + 5 * Math.sin(2 * Math.PI * 0.15 * t);
            double sub = Math.sin(2 * Math.PI * subFreq * t) * 0.25;
            // Pulse the sub with a slow LFO
            sub *= 0.7 + 0.3 * Math.sin(2 * Math.PI * 0.25 * t);

            // === Layer 2: Metallic grinding sawtooth (detuned pair) ===
            double grindFreq = 55 + 15 * Math.sin(2 * Math.PI * 0.08 * t);
            phase1 += grindFreq / SAMPLE_RATE;
            phase2 += (grindFreq * 2.003) / SAMPLE_RATE; // detuned octave
            double saw1 = 2.0 * (phase1 - Math.floor(phase1 + 0.5));
            double saw2 = 2.0 * (phase2 - Math.floor(phase2 + 0.5));
            // Ring modulate for metallic quality
            double ring = Math.sin(2 * Math.PI * 13.7 * t);
            double grind = (saw1 * 0.5 + saw2 * 0.3) * (0.5 + 0.5 * ring);
            // Slow volume swell
            grind *= 0.3 * (0.6 + 0.4 * Math.sin(2 * Math.PI * 0.12 * t));

            // === Layer 3: FM space choir (eerie mid-range pad) ===
            double choirMod = Math.sin(2 * Math.PI * 1.3 * t) * 80;
            double choirFreq = 220 + choirMod + 30 * Math.sin(2 * Math.PI * 0.07 * t);
            double choir = Math.sin(2 * Math.PI * choirFreq * t);
            // Add inharmonic partials for space texture
            double partial1 = Math.sin(2 * Math.PI * (choirFreq * 1.498) * t) * 0.4;
            double partial2 = Math.sin(2 * Math.PI * (choirFreq * 2.891) * t) * 0.2;
            choir = (choir + partial1 + partial2) * 0.2;
            // Slow breathing
            choir *= 0.5 + 0.5 * Math.sin(2 * Math.PI * 0.1 * t + 1.0);

            // === Layer 4: High metallic shimmer (bell-like resonance) ===
            double shimFreq = 1800 + 400 * Math.sin(2 * Math.PI * 0.05 * t);
            double shimmer = Math.sin(2 * Math.PI * shimFreq * t) * 0.08;
            shimmer += Math.sin(2 * Math.PI * (shimFreq * 1.414) * t) * 0.05; // sqrt(2) ratio = bell
            shimmer += Math.sin(2 * Math.PI * (shimFreq * 0.707) * t) * 0.04;
            // Slow pulsing
            shimmer *= 0.5 + 0.5 * Math.sin(2 * Math.PI * 0.18 * t + 2.0);

            // === Layer 5: Noise hiss (filtered, slow) ===
            double noise = (Math.random() * 2 - 1) * 0.04;
            noise *= 0.4 + 0.6 * Math.sin(2 * Math.PI * 0.22 * t + 0.5);

            // === Mix ===
            double mix = sub + grind + choir + shimmer + noise;

            // Soft clip to prevent harshness
            mix = Math.tanh(mix * 1.2);

            data[i] = (short)(loopEnv * Short.MAX_VALUE * 0.65 * mix);
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
