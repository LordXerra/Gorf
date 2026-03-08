package com.gorf.missions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gorf.graphics.SpriteManager;
import com.gorf.systems.EntityManager;

public abstract class Mission {
    protected boolean complete;
    protected float difficulty = 1.0f;

    public abstract void init(EntityManager em, SpriteManager sprites, float difficulty);
    public abstract void update(float delta, EntityManager em);
    public abstract boolean isComplete(EntityManager em);
    public abstract String getMissionName();
    public abstract int getMissionNumber(); // 1-5

    /** Optional: mission-specific rendering (force field, warp tunnel, laser beams). */
    public void render(SpriteBatch batch) {}

    /** Optional: cleanup when mission ends. */
    public void dispose() {}
}
