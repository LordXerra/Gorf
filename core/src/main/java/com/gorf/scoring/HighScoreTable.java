package com.gorf.scoring;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

public class HighScoreTable {
    private static final int MAX_ENTRIES = 10;
    private static final String SAVE_FILE = "highscores.json";
    private final Array<HighScoreEntry> entries = new Array<>();

    public HighScoreTable() {
        load();
    }

    private void load() {
        FileHandle file = Gdx.files.local(SAVE_FILE);
        if (file.exists()) {
            try {
                Json json = new Json();
                @SuppressWarnings("unchecked")
                Array<HighScoreEntry> loaded = json.fromJson(Array.class, HighScoreEntry.class, file);
                if (loaded != null) {
                    entries.addAll(loaded);
                    entries.sort();
                }
                Gdx.app.log("HighScoreTable", "Loaded " + entries.size + " scores");
                return;
            } catch (Exception e) {
                Gdx.app.error("HighScoreTable", "Failed to load scores", e);
            }
        }
        createDefaults();
    }

    private void createDefaults() {
        entries.clear();
        String[] names = {"TJB", "AKT", "TML", "AJB", "NST", "LIL", "JAK", "FLO", "DOM", "BEN"};
        int baseScore = 10000;
        for (int i = 0; i < MAX_ENTRIES; i++) {
            entries.add(new HighScoreEntry(names[i], baseScore - i * 1000));
        }
    }

    public boolean qualifies(int score) {
        if (entries.size < MAX_ENTRIES) return true;
        return score > entries.get(entries.size - 1).score;
    }

    public int getRank(int score) {
        for (int i = 0; i < entries.size; i++) {
            if (score > entries.get(i).score) return i + 1;
        }
        return entries.size + 1;
    }

    public void insert(String initials, int score) {
        entries.add(new HighScoreEntry(initials, score));
        entries.sort();
        while (entries.size > MAX_ENTRIES) {
            entries.removeIndex(entries.size - 1);
        }
        save();
    }

    public void save() {
        try {
            Json json = new Json();
            json.setOutputType(JsonWriter.OutputType.json);
            Gdx.files.local(SAVE_FILE).writeString(json.prettyPrint(entries), false);
        } catch (Exception e) {
            Gdx.app.error("HighScoreTable", "Failed to save scores", e);
        }
    }

    public Array<HighScoreEntry> getEntries() { return entries; }
    public int getEntryCount() { return entries.size; }
    public HighScoreEntry getEntry(int index) { return entries.get(index); }
}
