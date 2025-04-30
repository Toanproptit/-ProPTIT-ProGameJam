package com.Trongtoan.ProjGame.animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PlayerAnimationManager {

    private Animation<TextureRegion> animationRun;
    private Animation<TextureRegion> animationJumb;
    private Animation<TextureRegion> animationFall;
    private Animation<TextureRegion> animationIdle;

    private Texture texture;
    private float stateTime = 0;
    float scale = 0.5f;
    public PlayerAnimationManager(String path){
        texture = new Texture(path);
        TextureRegion[][] tmp = TextureRegion.split(texture,127,201);
        TextureRegion[] runFrames = new TextureRegion[tmp[0].length];
        for(int i=0;i< tmp[0].length;++i){
            runFrames[i]=tmp[0][i];
        }
        animationRun = new Animation<>(0.1f,runFrames);
    }

    public void update (float delaTime){
        stateTime+=delaTime;
    }

    public void reset() {
        stateTime = 0;
    }

    public TextureRegion getCurrentFrame() {
        return animationRun.getKeyFrame(stateTime, true);
    }

    public void dispose(){
        texture.dispose();
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

}
