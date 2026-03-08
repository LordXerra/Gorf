package com.gorf.scoring;

public class HighScoreEntry implements Comparable<HighScoreEntry> {
    public String initials;
    public int score;

    public HighScoreEntry() {}

    public HighScoreEntry(String initials, int score) {
        this.initials = initials;
        this.score = score;
    }

    @Override
    public int compareTo(HighScoreEntry other) {
        return Integer.compare(other.score, this.score); // descending
    }
}
