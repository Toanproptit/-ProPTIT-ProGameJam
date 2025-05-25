package com.Trongtoan.ProjGame.animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PlayerAnimationManager {

    public enum State{
        RUN,IDLE,JUMP,FALL,PUNCH,DIE,CAST;
    }

    private Animation<TextureRegion> runAnimation;
    private Animation<TextureRegion> jumpAnimation;
    private Animation<TextureRegion> fallAnimation;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> punchAnimation;
    private Animation<TextureRegion> dieAnimation;
    private Animation<TextureRegion> castAnimation;

    private State currentState = State.IDLE;
    private float stateTime = 0;
    float scale = 0.5f;

    private Texture runTexture;
    private Texture idleTexture;
    private Texture jumpTexture;
    private Texture fallTexture;
    private Texture punchTexture;
    private Texture dieTexture;
    private Texture castTexture;



    public PlayerAnimationManager(){
        runTexture = new Texture("Player/Player_run.png");
        idleTexture = new Texture("Player/Player_idle.png");
        jumpTexture = new Texture("Player/Player_jump.png");
        fallTexture = new Texture("Player/Player_fall.png");
        punchTexture = new Texture("Player/Player_attack.png");
        dieTexture = new Texture("Player/Player_die.png");
        castTexture = new Texture("Player/Player_cast.png");


        runAnimation = createAnimation(runTexture,0.1f);
        idleAnimation = createAnimation(idleTexture,0.1f);
        jumpAnimation = createAnimation(jumpTexture,0.1f);
        fallAnimation = createAnimation(fallTexture,0.1f);
        punchAnimation = createAnimation(punchTexture,0.1f);
        dieAnimation = createAnimation(dieTexture,0.1f);
        castAnimation = createAnimation(castTexture,0.5f);
    }

    private Animation<TextureRegion> createAnimation(Texture texture,float frameDuration){
        TextureRegion[][] tmp = TextureRegion.split(texture,127,201);
        TextureRegion[] runFrames = new TextureRegion[tmp[0].length];
        for (int i=0;i< tmp[0].length;++i){
            runFrames[i]=tmp[0][i];
        }
        return new Animation<>(frameDuration,runFrames);
    }

    public void update (float delaTime,State state){
        if(state != currentState){
            reset();
            currentState =state;
        }
        stateTime+=delaTime;
    }

    public void reset() {
        stateTime = 0;
    }

    public TextureRegion getCurrentFrame() {
        switch (currentState){
            case RUN:return runAnimation.getKeyFrame(stateTime,true);
            case FALL:return fallAnimation.getKeyFrame(stateTime,true);
            case JUMP:return jumpAnimation.getKeyFrame(stateTime,true);
            case PUNCH:return punchAnimation.getKeyFrame(stateTime,true);
            case DIE:return dieAnimation.getKeyFrame(stateTime,true);
            case CAST:return castAnimation.getKeyFrame(stateTime,true);
            case IDLE:
            default:return idleAnimation.getKeyFrame(stateTime,true);
        }
    }

    public void draw(SpriteBatch batch,float x,float y, boolean flipX){
        TextureRegion frame = getCurrentFrame();

        if(frame.isFlipX() != flipX){
            frame.flip(true,false);
        }
        batch.draw(frame,x,y,frame.getRegionWidth()*scale,frame.getRegionHeight()*scale);
    }

    public void setScale(float scale){
        this.scale=scale;
    }

    public float getScaledWidth() {
        return getCurrentFrame().getRegionWidth() * scale;
    }

    public float getScaledHeight() {
        return getCurrentFrame().getRegionHeight() * scale;
    }

    public void dispose(){
        runTexture.dispose();
        jumpTexture.dispose();
        idleTexture.dispose();
        fallTexture.dispose();
        punchTexture.dispose();
        dieTexture.dispose();
        castTexture.dispose();
    }
}
