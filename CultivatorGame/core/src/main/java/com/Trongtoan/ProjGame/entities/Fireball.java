package com.Trongtoan.ProjGame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Fireball {
    private Vector2 position;
    private Vector2 velocity;
    private Texture texture;
    private TextureRegion textureRegion;
    private boolean active = true;
    private float speed = 600f;
    private float damage;
    private float scale = 2.5f;

    public Fireball(Vector2 startPos, Vector2 direction, float damage) {
        this.position = new Vector2(startPos);
        this.velocity = new Vector2(direction).nor().scl(speed);
        this.texture = new Texture("Monster/Demonfly/fireball.png");
        this.textureRegion = new TextureRegion(texture);
        this.damage = damage;
    }

    public void update(float delta) {
        if (!active) return;
        position.mulAdd(velocity, delta);
    }

    public void draw(SpriteBatch batch, Player player) {
        if (!active) return;

        boolean flip = player.getPosition().x > position.x;

        float width = textureRegion.getRegionWidth() * scale;
        float height = textureRegion.getRegionHeight() * scale;

        if (flip) {
            batch.draw(textureRegion,
                position.x + width, position.y,
                -width, height);
        } else {
            batch.draw(textureRegion,
                position.x, position.y,
                width, height);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y,
            texture.getWidth() * scale,
            texture.getHeight() * scale);
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }

    public float getDamage() {
        return damage;
    }

    public void dispose() {
        texture.dispose();
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
