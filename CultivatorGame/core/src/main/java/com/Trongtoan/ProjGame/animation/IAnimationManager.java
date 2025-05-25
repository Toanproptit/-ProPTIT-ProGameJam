package com.Trongtoan.ProjGame.animation;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface IAnimationManager {
    void update(float deltaTime);
    void draw(SpriteBatch batch, float x, float y, boolean flip);
    float getScaledWidth();
    float getScaledHeight();
    void setScale(float scale);
    void setState(Enum<?> state);
    void dispose();
}
