package com.Trongtoan.ProjGame.entities;

import com.Trongtoan.ProjGame.animation.PlayerAnimationManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class Player {
    private Vector2 position;
    private Vector2 velocity;
    private Rectangle bounds;
    private float speed = 800f;
    private boolean onGround = false;
    private float gravity = 900f;
    private float flypower = 500f;
    private boolean isflying = false;
    private boolean isJumping = false;
    private float yVelocity = 0;
    private float preJumpHeight = 0;

    private float currentHp;
    private float maxHp;
    private float currentMp;
    private float maxMp;

    private boolean flipX = false;
    private final PlayerAnimationManager animationRun = new PlayerAnimationManager("Player/Player_run.png");
    private final List<Rectangle> groundRects;

    private float scale = 0.9f;

    public Player(List<Rectangle> groundRects) {
        animationRun.setScale(scale);

        position = new Vector2(100, 500); // bắt đầu ở giữa trời để rơi xuống
        velocity = new Vector2(0, 0);
        bounds = new Rectangle(position.x, position.y, 127 * scale, 201 * scale); // cập nhật theo scale
        this.groundRects = groundRects;
        this.currentHp = 1000;
        this.maxHp = 1000;
        this.currentMp = 1000;
        this.maxMp = 1000;
    }

    public void update(float deltaTime) {
        boolean pressingLeft = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean pressingRight = Gdx.input.isKeyPressed(Input.Keys.D);
        boolean pressingUp = Gdx.input.isKeyPressed(Input.Keys.W);

        float moveX = 0;
        boolean isMoving = pressingLeft || pressingRight;

        if (isMoving && !isflying) {
            animationRun.update(deltaTime);
        } else {
            animationRun.reset();
        }

        if (pressingLeft) {
            moveX = -speed;
            flipX = true;
        }
        if (pressingRight) {
            moveX = speed;
            flipX = false;
        }

        // Jumping (nro-style)
        if (isJumping) {
            position = new Vector2(position.x, position.y + yVelocity * deltaTime);

            if (position.y < preJumpHeight) {
                yVelocity += gravity * deltaTime;
            } else if (position.y >= preJumpHeight) {
                position = new Vector2(position.x, preJumpHeight);
                yVelocity = 0;
                isJumping = false;
            }
        } else if (pressingUp && onGround) {
            isJumping = true;
            yVelocity = 300f;
            preJumpHeight = position.y + 150f; // khoảng nhảy lên
        }

        // Bay (trong không khí)
        if ((pressingUp || (!onGround && (pressingLeft || pressingRight))) && !isflying) {
            isflying = true;
        }
        if (isflying) {
            if (pressingUp) {
                velocity.y = flypower; // bay lên
            } else {
                velocity.y = 0; // giữ độ cao (bay ngang)
            }
        } else {
            velocity.y -= gravity * deltaTime; // rơi
        }

        if (!pressingUp && !pressingRight && !pressingLeft) {
            isflying = false;
        }

        // Tính vị trí mới theo thời gian
        Vector2 newPos = new Vector2(position);
        newPos.x += moveX * deltaTime;
        newPos.y += velocity.y * deltaTime;

        Rectangle newBoundsX = new Rectangle(newPos.x, position.y, bounds.width, bounds.height);
        Rectangle newBoundsY = new Rectangle(position.x, newPos.y, bounds.width, bounds.height);

        // Kiểm tra va chạm trục X
        for (Rectangle rect : groundRects) {
            if (newBoundsX.overlaps(rect)) {
                moveX = 0;
                newPos.x = position.x;
                break;
            }
        }

        // Kiểm tra va chạm trục Y
        onGround = false;
        for (Rectangle rect : groundRects) {
            if (newBoundsY.overlaps(rect)) {
                if (velocity.y < 0 && position.y >= rect.y + rect.height) {
                    newPos.y = rect.y + rect.height;
                    velocity.y = 0;
                    onGround = true;
                } else if (velocity.y > 0 && position.y + bounds.height <= rect.y) {
                    newPos.y = position.y;
                    velocity.y = 0;
                }
                break;
            }
        }

        position.set(newPos);
        bounds.set(position.x, position.y, animationRun.getScaledWidth(), animationRun.getScaledHeight()); // cập nhật lại bounds mỗi frame
    }

    public void updateBounds() {
        bounds.set(position.x, position.y, animationRun.getScaledWidth(), animationRun.getScaledHeight());
    }

    public void setSize(float scale) {
        this.scale = scale;
        animationRun.setScale(scale);
        updateBounds();
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void draw(SpriteBatch batch) {
        animationRun.draw(batch, position.x, position.y, flipX);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
        bounds.setPosition(position);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void dispose() {
        animationRun.dispose();
    }

    public float getCurrentMp() {
        return currentMp;
    }

    public float getMaxHp() {
        return maxHp;
    }

    public float getMaxMp() {
        return maxMp;
    }

    public float getCurrentHp() {
        return currentHp;
    }

    public void setCurrentMp(float currentMp) {
        this.currentMp = currentMp;
    }

    public void setMaxHp(float maxHp) {
        this.maxHp = maxHp;
    }

    public void setCurrentHp(float currentHp) {
        this.currentHp = currentHp;
    }

    public void setMaxMp(float maxMp) {
        this.maxMp = maxMp;
    }
}
