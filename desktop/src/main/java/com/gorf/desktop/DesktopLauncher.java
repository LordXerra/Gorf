package com.gorf.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.gorf.Constants;
import com.gorf.GorfGame;

public class DesktopLauncher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("GORF " + Constants.VERSION);
        config.setWindowedMode(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        config.setForegroundFPS(60);
        config.useVsync(true);
        config.setResizable(true);
        new Lwjgl3Application(new GorfGame(), config);
    }
}
