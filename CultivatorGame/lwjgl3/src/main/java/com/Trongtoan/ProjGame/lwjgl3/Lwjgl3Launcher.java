package com.Trongtoan.ProjGame.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.Trongtoan.ProjGame.Main;

public class Lwjgl3Launcher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Cultivator's Dread");
        config.setWindowedMode(1200, 800); // Đổi kích thước nếu muốn
        config.useVsync(true);

        new Lwjgl3Application(new Main(), config);
    }
}
