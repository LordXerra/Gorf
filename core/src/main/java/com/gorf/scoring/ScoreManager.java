package com.gorf.scoring;

import com.gorf.Constants;

public class ScoreManager {
    private int score;
    private int lives;
    private int currentMission;  // 0-4
    private int cycleCount;      // how many times we've completed all 5 missions
    private boolean extraLifeAwarded;
    private boolean extraLifeFlag;

    public ScoreManager() {
        score = 0;
        lives = Constants.STARTING_LIVES;
        currentMission = 0;
        cycleCount = 0;
        extraLifeAwarded = false;
        extraLifeFlag = false;
    }

    public void addScore(int points) {
        score += points;
    }

    public void onPlayerDeath() {
        lives--;
    }

    public void onMissionComplete() {
        currentMission++;
        if (currentMission >= 5) {
            currentMission = 0;
            cycleCount++;

            // Award extra life after first complete cycle
            if (!extraLifeAwarded) {
                extraLifeAwarded = true;
                lives++;
                extraLifeFlag = true;
            }
        }
    }

    public boolean consumeExtraLifeFlag() {
        if (extraLifeFlag) {
            extraLifeFlag = false;
            return true;
        }
        return false;
    }

    public boolean isGameOver() {
        return lives <= 0;
    }

    public int getScore() { return score; }
    public int getLives() { return lives; }
    public int getCurrentMission() { return currentMission; }
    public int getCycleCount() { return cycleCount; }

    public String getCurrentRank() {
        return RankSystem.getRank(cycleCount);
    }

    public String getCurrentMissionName() {
        return Constants.MISSION_NAMES[currentMission];
    }

    public int getCurrentMissionNumber() {
        return currentMission + 1;
    }

    /** Get difficulty multiplier based on cycle count (1.0 at start, increasing). */
    public float getDifficulty() {
        return 1.0f + cycleCount * 0.2f;
    }
}
