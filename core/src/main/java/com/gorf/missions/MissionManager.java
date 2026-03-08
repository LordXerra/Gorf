package com.gorf.missions;

import com.gorf.Constants;
import com.gorf.graphics.SpriteManager;
import com.gorf.scoring.ScoreManager;
import com.gorf.systems.EntityManager;

public class MissionManager {
    private final Mission[] missions;
    private int currentIndex = 0;

    public MissionManager() {
        // All 5 missions will be added as they are implemented
        // For now, use placeholder null entries - they'll be filled in Phases 3-7
        missions = new Mission[5];
    }

    public void setMission(int index, Mission mission) {
        missions[index] = mission;
    }

    public Mission getCurrentMission() {
        return missions[currentIndex];
    }

    public void initCurrentMission(EntityManager em, SpriteManager sprites, ScoreManager sm) {
        em.clear();
        Mission m = missions[currentIndex];
        if (m != null) {
            m.init(em, sprites, sm.getDifficulty());
        }
    }

    public void advanceToNextMission(ScoreManager sm) {
        Mission current = missions[currentIndex];
        if (current != null) {
            current.dispose();
        }
        sm.onMissionComplete();
        currentIndex = sm.getCurrentMission();
    }

    public boolean isMissionComplete(EntityManager em) {
        Mission m = missions[currentIndex];
        if (m == null) return em.getAliveEnemyCount() == 0;
        return m.isComplete(em);
    }

    public String getCurrentMissionName() {
        Mission m = missions[currentIndex];
        if (m != null) return m.getMissionName();
        return Constants.MISSION_NAMES[currentIndex];
    }

    public int getCurrentMissionNumber() {
        return currentIndex + 1;
    }

    public void reset() {
        currentIndex = 0;
    }
}
