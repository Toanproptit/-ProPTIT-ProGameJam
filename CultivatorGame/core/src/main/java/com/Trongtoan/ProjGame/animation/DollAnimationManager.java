package com.Trongtoan.ProjGame.animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class DollAnimationManager {

    public enum DollState {
        IDLE, TAKE_HIT
    }

    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> hitAnimation;

    private DollState currentState = DollState.IDLE;
    private float stateTime = 0f;

    private Texture idleTexture;
    private Texture hitTexture;

    private float scale = 3.0f; // Scale lên cho to hơn nếu cần (64x64 → 128x128)

    public DollAnimationManager(String type) {
        idleTexture = new Texture("Monster/" + type + "/" + type + "_Idle.png");
        hitTexture  = new Texture("Monster/" + type + "/" + type + "_Take_Hit.png");

        idleAnimation = createAnimation(idleTexture);
        hitAnimation  = createAnimation(hitTexture);
        hitAnimation.setPlayMode(Animation.PlayMode.NORMAL); // chỉ chơi 1 lần khi bị đánh
    }

    private Animation<TextureRegion> createAnimation(Texture texture) {
        TextureRegion[][] tmp = TextureRegion.split(texture, 64, 64);
        TextureRegion[] frames = new TextureRegion[tmp[0].length];
        for (int i = 0; i < tmp[0].length; ++i) {
            frames[i] = tmp[0][i];
        }
        return new Animation<>(0.1f, frames);
    }

    public void setState(DollState state) {
        if (currentState != state) {
            currentState = state;
            stateTime = 0f;
        }
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;
    }

    public void draw(SpriteBatch batch, float x, float y, boolean flipX) {
        TextureRegion frame = getCurrentFrame();
        if (frame.isFlipX() != flipX) {
            frame.flip(true, false);
        }

        float width = frame.getRegionWidth() * scale;
        float height = frame.getRegionHeight() * scale;
        batch.draw(frame, x, y, width, height);
    }

    public TextureRegion getCurrentFrame() {
        return currentState == DollState.TAKE_HIT
            ? hitAnimation.getKeyFrame(stateTime, false)
            : idleAnimation.getKeyFrame(stateTime, true);
    }

    public float getScaledWidth() {
        return getCurrentFrame().getRegionWidth() * scale;
    }

    public float getScaledHeight() {
        return getCurrentFrame().getRegionHeight() * scale;
    }

    public void dispose() {
        idleTexture.dispose();
        hitTexture.dispose();
    }
}
