package com.Trongtoan.ProjGame.animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class FireballAnimation {
    public enum State { FLY, EXPLODE }

    private Animation<TextureRegion> flyAnimation;
    private Animation<TextureRegion> explodeAnimation;
    private boolean hasExplode = false;

    private State currentState = State.FLY;
    private float stateTime = 0f;
    private float scale = 2.5f;

    private int frameWidth;
    private int frameHeight;

    public FireballAnimation(Texture flyTex, int flyFrames, float frameDuration) {
        this.flyAnimation = createAnimation(flyTex, flyFrames, frameDuration);
        this.frameWidth = flyTex.getWidth() / flyFrames;
        this.frameHeight = flyTex.getHeight();
    }

    public FireballAnimation(Texture flyTex, int flyFrames, Texture explodeTex, int explodeFrames, float frameDuration) {
        this.flyAnimation = createAnimation(flyTex, flyFrames, frameDuration);
        this.explodeAnimation = createAnimation(explodeTex, explodeFrames, frameDuration);
        this.hasExplode = true;
        this.frameWidth = flyTex.getWidth() / flyFrames;
        this.frameHeight = flyTex.getHeight();
    }

    private Animation<TextureRegion> createAnimation(Texture texture, int frameCount, float duration) {
        TextureRegion[][] tmp = TextureRegion.split(texture, texture.getWidth() / frameCount, texture.getHeight());
        TextureRegion[] frames = tmp[0];
        return new Animation<>(duration, frames);
    }

    public void update(float delta) {
        stateTime += delta;
    }

    public void reset(State newState) {
        if (newState == State.EXPLODE && !hasExplode) return;
        currentState = newState;
        stateTime = 0;
    }

    public boolean isAnimationFinished() {
        if (currentState == State.EXPLODE && hasExplode) {
            return explodeAnimation.isAnimationFinished(stateTime);
        }
        return false;
    }

    public void draw(SpriteBatch batch, float x, float y, boolean flipX, float scale) {
        TextureRegion frame = getCurrentFrame();
        if (frame == null) return;

        if (frame.isFlipX() != flipX) frame.flip(true, false);
        batch.draw(frame, x, y, frame.getRegionWidth() * this.scale, frame.getRegionHeight() * this.scale);
    }

    private TextureRegion getCurrentFrame() {
        return currentState == State.EXPLODE && hasExplode
            ? explodeAnimation.getKeyFrame(stateTime, false)
            : flyAnimation.getKeyFrame(stateTime, true);
    }

    public float getWidth() {
        return frameWidth * scale;
    }

    public float getHeight() {
        return frameHeight * scale;
    }

    public State getState() {
        return currentState;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void dispose() {
        // if managing texture ownership, dispose here
    }
}
