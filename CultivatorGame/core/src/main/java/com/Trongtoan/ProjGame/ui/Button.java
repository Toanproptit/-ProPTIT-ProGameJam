package com.Trongtoan.ProjGame.ui;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Button extends ImageButton {

    public interface ToggleListener {
        void onToggle();
    }

    private ToggleListener listener;

    public Button(String texturePath) {
        super(new ImageButtonStyle());
        Texture texture = new Texture(Gdx.files.internal(texturePath));
        TextureRegion region = new TextureRegion(texture);
        this.getStyle().imageUp = new TextureRegionDrawable(region);
        this.setSize(region.getRegionWidth(), region.getRegionHeight());

        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (listener != null) listener.onToggle();
            }
        });
    }

    public void setToggleListener(ToggleListener listener) {
        this.listener = listener;
    }
}

