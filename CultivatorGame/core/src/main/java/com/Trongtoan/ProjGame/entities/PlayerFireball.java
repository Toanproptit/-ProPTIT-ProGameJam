package com.Trongtoan.ProjGame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.Trongtoan.ProjGame.animation.FireballAnimation;

public class PlayerFireball {
    private Vector2 position;
    private Vector2 velocity;
    private boolean active = true;
    private float speed = 300f;
    private float damage;
    private boolean hasHit = false;
    private Monster target;
    private FireballAnimation animation;
    private Player owner;
    private float scale;

    public PlayerFireball(Player player, Vector2 startPos, Monster target, Vector2 direction, float damage, Texture flyTex, int flyFrames, Texture explodeTex, int explodeFrames, float frameDuration, float scale) {
        this.owner = player;
        this.target = target;
        this.position = new Vector2(startPos);
        this.velocity = new Vector2(direction).nor().scl(speed);
        this.damage = damage;
        this.animation = new FireballAnimation(flyTex, flyFrames, explodeTex, explodeFrames, frameDuration);
        setScale(scale); // use setter to preserve center
    }

    public void update(float delta) {
        if (!active) return;

        if (target != null && target.isAlive()) {
            Vector2 dir = new Vector2(target.getCenter()).sub(position).nor();
            velocity.set(dir.scl(speed));
        }

        position.mulAdd(velocity, delta);
        animation.update(delta);

        if (target != null && getBounds().overlaps(target.getBounds()) && !hasHit) {
            target.takeDamage(damage, owner);
            explode();
            hasHit = true;
        }

        if (hasHit && animation.getState() == FireballAnimation.State.EXPLODE && animation.isAnimationFinished()) {
            deactivate();
        }
    }

    public void draw(SpriteBatch batch) {
        if (!active) return;
        animation.draw(batch, position.x, position.y, velocity.x < 0, scale);
    }

    public Rectangle getBounds() {
        float width = animation.getWidth() * scale;
        float height = animation.getHeight() * scale;

        return new Rectangle(
            position.x,
            position.y,
            width,
            height
        );
    }


    public void explode() {
        animation.reset(FireballAnimation.State.EXPLODE);
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

    public Vector2 getPosition() {
        return position;
    }

    public boolean hasHit() {
        return hasHit;
    }

    public void markHit() {
        hasHit = true;
    }

    public void dispose() {
        animation.dispose();
    }

    public void setScale(float newScale) {
        Vector2 centerBefore = getCenter();
        this.scale = newScale;
        Vector2 centerAfter = getCenter();
        Vector2 delta = centerBefore.sub(centerAfter);
        position.add(delta);
    }

    public Vector2 getCenter() {
        return new Vector2(
            position.x + animation.getWidth() * scale / 2f,
            position.y + animation.getHeight() * scale / 2f
        );
    }
}
