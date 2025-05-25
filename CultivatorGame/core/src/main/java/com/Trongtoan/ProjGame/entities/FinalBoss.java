package com.Trongtoan.ProjGame.entities;

import com.Trongtoan.ProjGame.animation.BossAnimationManager;
import com.Trongtoan.ProjGame.animation.BossAnimationManager.BossState;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

public class FinalBoss extends Boss {

    private boolean enteredPhase2 = false;
    private float castCooldown = 6f;
    private float castTimer = 0f;
    private float attackDuration = 0.8f;
    private float attackStateTimer = 0f;

    public FinalBoss(Vector2 position) {
        super("final_boss", 5000f, 5f, position, 200f, 500f,10000);
        getAnimationManager().setScale(BossState.IDLE, 1.5f);
        getAnimationManager().setScale(BossState.WALK, 1.5f);
        getAnimationManager().setScale(BossState.CLEAVE, 1.5f);
        getAnimationManager().setScale(BossState.ATTACK2, 1.5f);
        getAnimationManager().setScale(BossState.FLY, 1.5f);
        getAnimationManager().setScale(BossState.TAKE_HIT, 1.5f);
        getAnimationManager().setScale(BossState.DIE, 1.5f);
    }

    @Override
    public void update(float deltaTime, Player player) {
        if (isBeingHit()) {
            hitTimer += deltaTime;
            if (hitTimer >= hitDuration) {
                setBeingHit(false);
            }
        }

        if (!isAlive()) {
            setState(BossState.DIE);
            getAnimationManager().update(deltaTime);
            return;
        }

        float dx = player.getPosition().x - getPosition().x;
        float dy = player.getPosition().y - getPosition().y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        setFacingLeft(dx < 0);

        boolean isAttacking = getCurrentState() == BossState.CLEAVE || getCurrentState() == BossState.ATTACK2;

        if (isAttacking) {
            attackStateTimer += deltaTime;
            attackTimer += deltaTime;

            if (attackTimer >= attackCooldown) {
                player.takeDamage(getAttack());
                attackTimer = 0f;
            }

            if (attackStateTimer >= attackDuration) {
                setState(BossState.IDLE);
                attackStateTimer = 0f;
            }
        }
        else if (distance > 300f || dy > 120f) {
            setState(BossState.FLY);
            float flySpeed = 100f;
            getPosition().x += Math.signum(dx) * flySpeed * deltaTime;
            getPosition().y += Math.signum(dy) * flySpeed * deltaTime;
        }
        else if (Math.abs(dx) < 100f && Math.abs(dy) < 60f) {
            if (Math.random() < 0.5) {
                setState(BossState.CLEAVE);
            } else {
                setState(BossState.ATTACK2);
            }
            attackStateTimer = 0f;
        }
        else {
            setState(BossState.WALK);
            float walkSpeed = 50f;
            getPosition().x += Math.signum(dx) * walkSpeed * deltaTime;

            float groundY = 350f;
            if (getPosition().y > groundY) {
                getPosition().y -= 100f * deltaTime;
            }
        }

        getAnimationManager().update(deltaTime);
    }

    @Override
    public void draw(SpriteBatch batch, Player player) {
        float drawX = getPosition().x - getAnimationManager().getScaledWidth() / 2f;
        float drawY = getPosition().y;
        getAnimationManager().draw(batch, drawX, drawY, isFacingLeft(), isBeingHit());
    }

    @Override
    public void drawInfo(SpriteBatch batch, BitmapFont font) {
        if (!isAlive()) return;

        float barWidth = 180;
        float barHeight = 14;

        float drawX = getPosition().x - getAnimationManager().getScaledWidth() / 2f;
        float drawY = getPosition().y;

        float barX = drawX + getAnimationManager().getScaledWidth() / 2f - barWidth / 2f;
        float barY = drawY + getAnimationManager().getScaledHeight();

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
        font.draw(batch, getType(), barX, barY + 30);
    }

    @Override
    public void playHitAnimation() {
        setBeingHit(true);
        hitTimer = 0f;
    }
}
