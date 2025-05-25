package com.Trongtoan.ProjGame.entities;

import com.Trongtoan.ProjGame.animation.DollAnimationManager;
import com.Trongtoan.ProjGame.animation.DollAnimationManager.DollState;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Doll extends Monster {

    private DollAnimationManager animationManager;
    private DollState currentState = DollState.IDLE;
    private boolean facingLeft = false;

    private boolean isBeingHit = false;
    private float hitTimer = 0f;
    private final float hitDuration = 0.3f;

    public Doll(String type, float hp, float respawnTime, Vector2 position, float attack, float detectRange,int potentialReward) {
        super(type, hp, respawnTime, position, attack, detectRange,potentialReward);
        this.animationManager = new DollAnimationManager(type);
        float width = animationManager.getScaledWidth();
        float height = animationManager.getScaledHeight();
        setBounds(new Rectangle(position.x, position.y, width, height));
    }


    @Override
    public void update(float deltaTime, Player player) {
        animationManager.update(deltaTime);

        if (!isAlive()) {
            respawnTimer += deltaTime;
            if (respawnTimer >= getRespawnTime()) {
                revive(); // Hồi sinh
                setState(DollState.IDLE);
            }
            return; // Không xử lý gì thêm nếu chưa hồi sinh
        }

        if (isBeingHit) {
            hitTimer += deltaTime;
            if (hitTimer >= hitDuration) {
                isBeingHit = false;
                setState(DollState.IDLE);
            }
        }
    }


    @Override
    public void playHitAnimation() {
        isBeingHit = true;
        hitTimer = 0f;
        setState(DollState.TAKE_HIT);
    }

    private void setState(DollState newState) {
        if (currentState != newState) {
            currentState = newState;
            animationManager.setState(newState);
        }
    }

    @Override
    public Vector2 getCenter() {
        return new Vector2(
            getPosition().x + animationManager.getScaledWidth() / 2f,
            getPosition().y + animationManager.getScaledHeight() / 2f
        );
    }

    @Override
    public void draw(SpriteBatch batch, Player player) {
        float drawX = getPosition().x - animationManager.getScaledWidth() / 2f;
        float drawY = getPosition().y;
        animationManager.draw(batch, drawX, drawY, facingLeft);
    }

    @Override
    public void drawInfo(SpriteBatch batch, BitmapFont font) {
        if (!isAlive()) return;

        float barWidth = 100;
        float barHeight = 10;

        float drawX = getPosition().x - animationManager.getScaledWidth() / 2f;
        float drawY = getPosition().y;

        float barX = drawX + animationManager.getScaledWidth() / 2f - barWidth / 2f;
        float barY = drawY + animationManager.getScaledHeight() + 5;

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
        font.getData().setScale(1.5f);
        font.draw(batch, getType(), barX, barY + 25);
        font.getData().setScale(1f);
    }

    @Override
    protected void setupAnimationManager(String type) {
        // Không dùng trong Doll vì đã có animationManager riêng
    }


    public void dispose() {
        animationManager.dispose();
    }
}
