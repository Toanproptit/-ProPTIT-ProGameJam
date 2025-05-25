package com.Trongtoan.ProjGame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class BottomMenuPanel extends Table {

    private boolean isVisible = false;
    private final float panelHeight = 120;
    private final float panelWidth = 400;
    private PotentialPanel potentialPanel;

    public BottomMenuPanel(PotentialPanel potentialPanel) {
        this.potentialPanel = potentialPanel;
        this.setSize(panelWidth, panelHeight);
        float x = (Gdx.graphics.getWidth() - panelWidth) / 2f;
        float y = -panelHeight;
        this.setPosition(x, y);

        this.center().pad(10);

        // Tạo nút từ ảnh
        ImageButton skillbutton = createImageButton("ui/skillButton.png");
        ImageButton indexbutton = createImageButton("ui/indexButton.png");
        ImageButton settingnbutton = createImageButton("ui/settingButton.png");
        ImageButton missionbutton = createImageButton("ui/missionButton.png");

        // Thêm vào bảng
        this.add(skillbutton).pad(5);
        this.add(indexbutton).pad(5);
        this.add(settingnbutton).pad(5);
        this.add(missionbutton).pad(5);


        // Ví dụ: sự kiện click
        skillbutton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                potentialPanel.setVisible(true);
                BottomMenuPanel.this.setVisible(false);
            }
        });

        // Bạn có thể thêm các sự kiện cho 2 nút còn lại tương tự
    }

    private ImageButton createImageButton(String path) {
        Texture tex = new Texture(Gdx.files.internal(path));
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(tex));
        return new ImageButton(drawable);
    }



    public void toggle() {
        float x = getX();
        if (isVisible) {
            this.addAction(Actions.moveTo(x, -panelHeight-10, 0.3f));
        } else {
            this.addAction(Actions.moveTo(x, 30f, 0.3f));
        }
        isVisible = !isVisible;
    }
}
