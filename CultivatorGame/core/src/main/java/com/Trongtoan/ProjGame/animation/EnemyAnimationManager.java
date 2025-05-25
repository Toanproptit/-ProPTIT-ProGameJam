package com.Trongtoan.ProjGame.animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class EnemyAnimationManager implements IAnimationManager {
    public enum EnemyState {
        IDLE, WALK, ATTACK, DIE;
    }

    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> attackAnimation;
    private Animation<TextureRegion> dieAnimation;

    private EnemyState currentState = EnemyState.WALK;
    private float stateTime = 0f;

    private Texture idleTexture;
    private Texture walkTexture;
    private Texture attackTexture;
    private Texture dieTexture;

    // Scale riêng cho từng trạng thái
    private float idleScale = 1f;
    private float walkScale = 1f;
    private float attackScale = 1.5f;
    private float dieScale = 1f;

    public EnemyAnimationManager(String type) {
        idleTexture = new Texture("Monster/" + type + "/" + type + "_Idle.png");
        walkTexture = new Texture("Monster/" + type + "/" + type + "_Walk.png");
        attackTexture = new Texture("Monster/" + type + "/" + type + "_Attack.png");
        dieTexture = new Texture("Monster/" + type + "/" + type + "_Die.png");

        idleAnimation = createAnimation(idleTexture);
        walkAnimation = createAnimation(walkTexture);
        attackAnimation = createAnimation(attackTexture);
        dieAnimation = createAnimation(dieTexture);
        dieAnimation.setPlayMode(Animation.PlayMode.NORMAL);
    }

    private Animation<TextureRegion> createAnimation(Texture texture) {
        TextureRegion[][] tmp = TextureRegion.split(texture, 64, 64);
        TextureRegion[] frames = new TextureRegion[tmp[0].length];
        for (int i = 0; i < tmp[0].length; ++i) {
            frames[i] = tmp[0][i];
        }
        return new Animation<>(0.1f, frames);
    }

    public void setState(EnemyState state) {
        if (currentState != state) {
            currentState = state;
            stateTime = 0;
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

        float scale = getCurrentScale();
        float width = frame.getRegionWidth() * scale;
        float height = frame.getRegionHeight() * scale;

        batch.draw(frame, x, y, width, height);
    }

    public TextureRegion getCurrentFrame() {
        switch (currentState) {
            case WALK:
                return walkAnimation.getKeyFrame(stateTime, true);
            case ATTACK:
                return attackAnimation.getKeyFrame(stateTime, true);
            case DIE:
                return dieAnimation.getKeyFrame(stateTime, false);
            case IDLE:
            default:
                return idleAnimation.getKeyFrame(stateTime, true);
        }
    }

    private float getCurrentScale() {
        switch (currentState) {
            case WALK: return walkScale;
            case ATTACK: return attackScale;
            case DIE: return dieScale;
            case IDLE:
            default: return idleScale;
        }
    }

    public void setScale(EnemyState state, float scale) {
        switch (state) {
            case IDLE: idleScale = scale; break;
            case WALK: walkScale = scale; break;
            case ATTACK: attackScale = scale; break;
            case DIE: dieScale = scale; break;
        }
    }

    @Override
    public void setScale(float scale) {
        idleScale = scale;
        walkScale = scale;
        attackScale = scale;
        dieScale = scale;
    }


    public float getScaledWidth() {
        return getCurrentFrame().getRegionWidth() * getCurrentScale();
    }

    public float getScaledHeight() {
        return getCurrentFrame().getRegionHeight() * getCurrentScale();
    }

    @Override
    public void setState(Enum<?> state) {
        if (state instanceof EnemyState && currentState != state) {
            currentState = (EnemyState) state;
            stateTime = 0;
        }
    }

    public void dispose() {
        idleTexture.dispose();
        walkTexture.dispose();
        attackTexture.dispose();
        dieTexture.dispose();
    }
}
