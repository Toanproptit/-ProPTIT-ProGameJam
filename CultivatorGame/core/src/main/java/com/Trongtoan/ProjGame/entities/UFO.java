package com.Trongtoan.ProjGame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class UFO {
    private Texture ufoTexture;
    private Vector2 position;
    private float speed = 1500f;
    private Rectangle bounds;
    private boolean active = true;
    private boolean exitMode = false;
    public UFO(float startX,float startY){
        position = new Vector2(startX,startY);
        bounds = new Rectangle(startX,startY,128,64);
        ufoTexture = new Texture("UFO/UFO.png");
        exitMode = false;
        active = true;
    }

    public void update(float deltaTime, OrthographicCamera camera) {
        if (!active) return;

        if (exitMode) {
            position.x += speed * deltaTime;
            if (position.x > camera.position.x + camera.viewportWidth / 2f + bounds.width) {
                active = false;
            }
        } else {
            position.x += speed * deltaTime;
            if (position.x > camera.position.x + camera.viewportWidth / 2f + bounds.width) {
                active = false;
            }
        }

        bounds.setPosition(position);
    }


    public void startExit() {
        exitMode = true;
    }

    public void draw(SpriteBatch batch){
        if(active){
            batch.draw(ufoTexture,position.x,position.y, bounds.width, bounds.height);
        }
    }
    public Rectangle getBounds() {
        return bounds;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void deactivate() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
        bounds.setPosition(x, y);
    }

    public void dispose(){
        ufoTexture.dispose();
    }

    public float getWidth(){
        return bounds.width;
    }

    public float getHeight(){
        return bounds.height;
    }

    public void setSize(float width, float height) {
        bounds.setSize(width, height);
    }

}
