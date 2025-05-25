package com.Trongtoan.ProjGame.animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class BossAnimationManager {
    public enum BossState {
        IDLE, WALK, CLEAVE, TAKE_HIT, DIE,FLY,ATTACK2;
    }

    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> cleaveAnimation;
    private Animation<TextureRegion> takeHitAnimation;
    private Animation<TextureRegion> dieAnimation;

    private Animation<TextureRegion> flyAnimation, attack2Animation;

    private BossState currentState = BossState.IDLE;
    private float stateTime = 0f;

    private Texture flyTexture, attack2Texture;
    private Texture idleTexture;
    private Texture walkTexture;
    private Texture cleaveTexture;
    private Texture takeHitTexture;
    private Texture dieTexture;

    // Scale riêng cho từng trạng thái
    private float idleScale = 2f;
    private float walkScale = 2f;
    private float cleaveScale = 2f;
    private float takeHitScale = 2f;
    private float dieScale = 2f;

    public BossAnimationManager(String type) {
        if (type.equals("final_boss")) {
            flyTexture = new Texture("Monster/" + type + "/" + type + "_Fly.png");
            flyAnimation = createAnimation(flyTexture);

            attack2Texture = new Texture("Monster/" + type + "/" + type + "_Attack2.png");
            attack2Animation = createAnimation(attack2Texture);
        }
        idleTexture = new Texture("Monster/" + type + "/" + type + "_Idle.png");
        walkTexture = new Texture("Monster/" + type + "/" + type + "_Walk.png");
        cleaveTexture = new Texture("Monster/" + type + "/" + type + "_Attack.png");
        takeHitTexture = new Texture("Monster/" + type + "/" + type + "_Take_Hit.png");
        dieTexture = new Texture("Monster/" + type + "/" + type + "_Die.png");

        idleAnimation = createAnimation(idleTexture);
        walkAnimation = createAnimation(walkTexture);
        cleaveAnimation = createAnimation(cleaveTexture);
        takeHitAnimation = createAnimation(takeHitTexture);
        dieAnimation = createAnimation(dieTexture);
        dieAnimation.setPlayMode(Animation.PlayMode.NORMAL);
    }

    private Animation<TextureRegion> createAnimation(Texture texture) {
        TextureRegion[][] tmp = TextureRegion.split(texture, 288, 160);
        TextureRegion[] frames = new TextureRegion[tmp[0].length];
        for (int i = 0; i < tmp[0].length; ++i) {
            frames[i] = tmp[0][i];
        }
        return new Animation<>(0.1f, frames);
    }

    public void setState(BossState state) {
        if (currentState != state) {
            currentState = state;
            stateTime = 0;
        }
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;
    }

    public void draw(SpriteBatch batch, float x, float y, boolean flipX, boolean isBeingHit) {
        TextureRegion frame = getCurrentFrame(isBeingHit);
        if (frame.isFlipX() != flipX) {
            frame.flip(true, false);
        }

        float scale = getCurrentScale();
        float width = frame.getRegionWidth() * scale;
        float height = frame.getRegionHeight() * scale;

        batch.draw(frame, x, y, width, height);
    }

    public TextureRegion getCurrentFrame(boolean isBeingHit) {
        if (isBeingHit && currentState != BossState.DIE) {
            return takeHitAnimation.getKeyFrame(stateTime, true);
        }
        switch (currentState) {
            case WALK:
                return walkAnimation.getKeyFrame(stateTime, true);
            case CLEAVE:
                return cleaveAnimation.getKeyFrame(stateTime, true);
            case TAKE_HIT:
                return takeHitAnimation.getKeyFrame(stateTime, true);
            case DIE:
                return dieAnimation.getKeyFrame(stateTime, false);
            case FLY:
                return flyAnimation != null ? flyAnimation.getKeyFrame(stateTime, true) : idleAnimation.getKeyFrame(stateTime, true);
            case ATTACK2:
                return attack2Animation != null ? attack2Animation.getKeyFrame(stateTime, true) : cleaveAnimation.getKeyFrame(stateTime, true);
            case IDLE:
            default:
                return idleAnimation.getKeyFrame(stateTime, true);
        }
    }

    private float getCurrentScale() {

        switch (currentState) {
            case WALK: return walkScale;
            case CLEAVE: return cleaveScale;
            case TAKE_HIT: return takeHitScale;
            case DIE: return dieScale;
            case IDLE:
            default: return idleScale;
        }
    }

    public void     setScale(BossState state, float scale) {
        switch (state) {
            case IDLE: idleScale = scale; break;
            case WALK: walkScale = scale; break;
            case CLEAVE: cleaveScale = scale; break;
            case TAKE_HIT: takeHitScale = scale; break;
            case DIE: dieScale = scale; break;
        }
    }



    public float getScaledWidth() {
        return getCurrentFrame(false).getRegionWidth() * getCurrentScale();
    }

    public float getScaledHeight() {
        return getCurrentFrame(false).getRegionHeight() * getCurrentScale();
    }

    public void dispose() {
        idleTexture.dispose();
        walkTexture.dispose();
        cleaveTexture.dispose();
        takeHitTexture.dispose();
        dieTexture.dispose();
        if (flyTexture != null) flyTexture.dispose();
        if (attack2Texture != null) attack2Texture.dispose();
    }
}
