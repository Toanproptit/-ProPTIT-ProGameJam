package com.Trongtoan.ProjGame;

import com.Trongtoan.ProjGame.screen.LoginScreen;
import com.badlogic.gdx.Game;

public class Main extends Game {
    @Override
    public void create() {
        // Khởi động với màn hình đăng nhập
        setScreen(new LoginScreen(this));
    }

    @Override
    public void render() {
        super.render(); // Tự gọi render() của màn hình hiện tại
    }

    @Override
    public void dispose() {
        // Hủy màn hình hiện tại nếu cần
        if (getScreen() != null) {
            getScreen().dispose();
        }
    }
}
