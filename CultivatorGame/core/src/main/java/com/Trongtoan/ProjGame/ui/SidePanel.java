package com.Trongtoan.ProjGame.ui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class SidePanel extends Table {

    private boolean isVisible = false;
    private final float panelWidth = 300;

    public SidePanel() {
        Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json")); // ✅ dùng skin mặc định hoặc custom
//        Texture bgTexture = new Texture(Gdx.files.internal("ui/panel_bg.png")); // 📦 ảnh nền bảng
//
//        this.setBackground(new TextureRegionDrawable(bgTexture));
        this.setSize(panelWidth, Gdx.graphics.getHeight());
        this.setPosition(-panelWidth, 0); // Bắt đầu ẩn ngoài trái

        this.top().left().pad(20);

        // Nội dung mẫu
//        this.add(new Label("📌 Nhiệm vụ", skin)).left().row();
//        this.add(new Label("• Hoàn thành 3 trận", skin)).left().row();
//        this.add(new Label("• Nhận 50 xu", skin)).left().padBottom(20).row();

    }

    public void toggle() {
        if (isVisible) {
            this.addAction(Actions.moveTo(-panelWidth, 0, 0.3f));
        } else {
            this.addAction(Actions.moveTo(0, 0, 0.3f));
        }
        isVisible = !isVisible;
    }
}
