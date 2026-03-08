package com.gorf.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import java.util.EnumMap;

public class SoundManager {
    private final EnumMap<SoundId, Sound> sounds = new EnumMap<>(SoundId.class);
    private final EnumMap<SoundId, Float> cooldowns = new EnumMap<>(SoundId.class);
    private float masterVolume = 1.0f;

    private static final float DEFAULT_COOLDOWN = 0.05f;
    private static final int MAX_SOUNDS_PER_FRAME = 8;
    private int frameSoundCount = 0;

    public void load() {
        for (SoundId id : SoundId.values()) {
            String path = "sounds/" + id.filename + ".wav";
            try {
                sounds.put(id, Gdx.audio.newSound(Gdx.files.internal(path)));
            } catch (Exception e) {
                Gdx.app.error("SoundManager", "Failed to load: " + path);
            }
        }
        Gdx.app.log("SoundManager", "Loaded " + sounds.size() + " sounds");
    }

    public void update(float delta) {
        frameSoundCount = 0;
        for (SoundId id : SoundId.values()) {
            Float remaining = cooldowns.get(id);
            if (remaining != null && remaining > 0) {
                cooldowns.put(id, remaining - delta);
            }
        }
    }

    public long play(SoundId id) {
        return play(id, masterVolume, true);
    }

    public long play(SoundId id, float volume) {
        return play(id, volume * masterVolume, true);
    }

    private long play(SoundId id, float finalVolume, boolean useLimits) {
        if (useLimits) {
            if (frameSoundCount >= MAX_SOUNDS_PER_FRAME) return -1;
            Float remaining = cooldowns.get(id);
            if (remaining != null && remaining > 0) return -1;
        }

        Sound s = sounds.get(id);
        if (s != null) {
            if (useLimits) {
                frameSoundCount++;
                cooldowns.put(id, DEFAULT_COOLDOWN);
            }
            return s.play(finalVolume);
        }
        return -1;
    }

    public long playPriority(SoundId id) {
        return play(id, masterVolume, false);
    }

    public long loop(SoundId id, float volume) {
        Sound s = sounds.get(id);
        if (s != null) return s.loop(volume * masterVolume);
        return -1;
    }

    public void stop(SoundId id) {
        Sound s = sounds.get(id);
        if (s != null) s.stop();
    }

    public void stopAll() {
        for (Sound s : sounds.values()) {
            s.stop();
        }
    }

    public void setMasterVolume(float volume) {
        this.masterVolume = Math.max(0, Math.min(1, volume));
    }

    public void dispose() {
        for (Sound s : sounds.values()) {
            s.dispose();
        }
        sounds.clear();
    }
}
