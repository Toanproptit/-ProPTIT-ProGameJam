package com.Trongtoan.ProjGame.animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GolemAnimationManager implements IAnimationManager {

    public enum GolemState {
        IDLE, WALK, ATTACK, DIE;
    }

    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> attackAnimation;
    private Animation<TextureRegion> dieAnimation;

    private GolemState currentState = GolemState.WALK;
    private float stateTime = 0f;

    private Texture idleTexture;
    private Texture walkTexture;
    private Texture attackTexture;
    private Texture dieTexture;

    private float scale = 1f;

    public GolemAnimationManager(String type) {
        idleTexture = new Texture("Monster/" + "Golem" + "/" + type + "_Idle.png");
        walkTexture = new Texture("Monster/" + "Golem" + "/" + type + "_Walk.png");
        attackTexture = new Texture("Monster/" + "Golem" + "/" + type + "_Attack.png");
        dieTexture = new Texture("Monster/" + "Golem" + "/" + type + "_Die.png");

        idleAnimation = createAnimation(idleTexture);
        walkAnimation = createAnimation(walkTexture);
        attackAnimation = createAnimation(attackTexture);
        dieAnimation = createAnimation(dieTexture);
        dieAnimation.setPlayMode(Animation.PlayMode.NORMAL);
        scale = 4f;
    }

    private Animation<TextureRegion> createAnimation(Texture texture) {
        TextureRegion[][] tmp = TextureRegion.split(texture, 90, 64); // Golem frame size
        TextureRegion[] frames = new TextureRegion[tmp[0].length];
        for (int i = 0; i < tmp[0].length; ++i) {
            frames[i] = tmp[0][i];
        }
        return new Animation<>(0.1f, frames);
    }

    @Override
    public void update(float deltaTime) {
        stateTime += deltaTime;
    }

    @Override
    public void draw(SpriteBatch batch, float x, float y, boolean flipX) {
        TextureRegion frame = getCurrentFrame();
        if (frame.isFlipX() != flipX) {
            frame.flip(true, false);
        }

        float width = frame.getRegionWidth() * scale;
        float height = frame.getRegionHeight() * scale;

        batch.draw(frame, x, y, width, height);
    }

    @Override
    public void setState(Enum<?> state) {
        if (state instanceof GolemState && currentState != state) {
            currentState = (GolemState) state;
            stateTime = 0;
        }
    }

    private TextureRegion getCurrentFrame() {
        switch (currentState) {
            case WALK: return walkAnimation.getKeyFrame(stateTime, true);
            case ATTACK: return attackAnimation.getKeyFrame(stateTime, true);
            case DIE: return dieAnimation.getKeyFrame(stateTime, false);
            case IDLE:
            default: return idleAnimation.getKeyFrame(stateTime, true);
        }
    }

    @Override
    public float getScaledWidth() {
        return getCurrentFrame().getRegionWidth() * scale;
    }

    @Override
    public float getScaledHeight() {
        return getCurrentFrame().getRegionHeight() * scale;
    }

    @Override
    public void setScale(float scale) {
        this.scale = scale;
    }


    public void dispose() {
        idleTexture.dispose();
        walkTexture.dispose();
        attackTexture.dispose();
        dieTexture.dispose();
    }
}
