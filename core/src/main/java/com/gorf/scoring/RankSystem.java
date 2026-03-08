package com.gorf.scoring;

import com.gorf.Constants;

public class RankSystem {
    public static String getRank(int completedCycles) {
        int index = Math.min(completedCycles, Constants.RANKS.length - 1);
        return Constants.RANKS[index];
    }

    public static int getRankIndex(int completedCycles) {
        return Math.min(completedCycles, Constants.RANKS.length - 1);
    }
}
