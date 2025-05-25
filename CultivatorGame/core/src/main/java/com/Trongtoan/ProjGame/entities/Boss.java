package com.Trongtoan.ProjGame.entities;

import com.Trongtoan.ProjGame.animation.BossAnimationManager;
import com.Trongtoan.ProjGame.animation.BossAnimationManager.BossState;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Boss extends Monster {

    private BossAnimationManager bossAnimationManager;
    private BossState currentState = BossState.IDLE;
    private boolean facingLeft = false;
    protected float attackCooldown = 1.0f; // giây
    protected float attackTimer = 0f;

    private boolean isBeingHit = false;
    protected float hitTimer = 0f;
    protected float hitDuration = 0.3f;



    public Boss(String type, float hp, float respawnTime, Vector2 position, float attack, float detectRange, int potentialReward) {
        super(type, hp, respawnTime, position, attack, detectRange,potentialReward);
        this.bossAnimationManager = new BossAnimationManager(type);
        if (bossAnimationManager != null) {
            float width = bossAnimationManager.getScaledWidth();
            float height = bossAnimationManager.getScaledHeight();
            this.setBounds(new Rectangle(position.x, position.y, width, height));
        } else {
            System.err.println("Error: bossAnimationManager is not initialized.");
        }
    }

    @Override
    public void update(float deltaTime, Player player) {
        float dx = player.getPosition().x - getPosition().x;
        float dy = player.getPosition().y - getPosition().y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);


        if (isBeingHit) {
            hitTimer += deltaTime;
            if (hitTimer >= hitDuration) {
                isBeingHit = false;
            }
        }

        if (!isAlive()) {
            setState(BossState.DIE);
        } else {
            if (distance > 500f) {
                setState(BossState.IDLE);
                facingLeft = dx > 0;
                // KHÔNG cập nhật facingLeft khi đứng yên
            } else {
                if (Math.abs(dx) < 200f && Math.abs(dy) < 80f) {
                    setState(BossState.CLEAVE);
                    facingLeft = dx > 0; // Chỉ flip khi tấn công
                    attackTimer += deltaTime;
                    if (attackTimer >= attackCooldown) {
                        player.takeDamage(getAttack());
                        attackTimer = 0f;
                    }
                } else {
                    setState(BossState.WALK);
//                    facingLeft = dx < 0; // Chỉ flip khi di chuyển
                    float speed = 50f;
                    getPosition().x += Math.signum(dx) * speed * deltaTime;
                }
            }
        }
        bossAnimationManager.update(deltaTime);
    }


    @Override
    protected void setupAnimationManager(String type) {
        // Không dùng trong Boss vì dùng BossAnimationManager riêng
    }

    @Override
    public Vector2 getCenter() {
        return new Vector2(
            getPosition().x + bossAnimationManager.getScaledWidth() / 2f,
            getPosition().y + bossAnimationManager.getScaledHeight() / 2f
        );
    }

    @Override
    public void draw(SpriteBatch batch, Player player) {
        float drawX = getPosition().x - bossAnimationManager.getScaledWidth() / 2f;
        float drawY = getPosition().y;
        bossAnimationManager.draw(batch, drawX, drawY, facingLeft,isBeingHit);
    }

    @Override
    public void playHitAnimation() {
        isBeingHit = true;
        hitTimer = 0f;
    }


    @Override
    public void drawInfo(SpriteBatch batch, BitmapFont font) {
        if (!isAlive()) return;

        float barWidth = 150;
        float barHeight = 12;

        float drawX = getPosition().x - bossAnimationManager.getScaledWidth() / 2f;
        float drawY = getPosition().y;

        float barX = drawX + bossAnimationManager.getScaledWidth() / 2f - barWidth / 2f;
        float barY = drawY + bossAnimationManager.getScaledHeight() ;

        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(barX - 2, barY - 2, barWidth + 4, barHeight + 4);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(barX, barY, barWidth * (getCurrentHp() / getMaxHp()), barHeight);
        shapeRenderer.end();
        shapeRenderer.dispose();

        batch.begin();
        font.draw(batch, getType(), barX, barY + 25);
    }



    public void setState(BossState newState) {
        if (currentState != newState) {
            currentState = newState;
            bossAnimationManager.setState(newState);
        }
    }



    public BossState getCurrentState() {
        return currentState;
    }

    public BossAnimationManager getAnimationManager() {
        return bossAnimationManager;
    }

    public boolean isFacingLeft() {
        return facingLeft;
    }

    public void setFacingLeft(boolean facingLeft) {
        this.facingLeft = facingLeft;
    }

    public boolean isBeingHit() {
        return isBeingHit;
    }

    public void setBeingHit(boolean value) {
        isBeingHit = value;
    }

    public void dispose() {
        bossAnimationManager.dispose();
    }
}
