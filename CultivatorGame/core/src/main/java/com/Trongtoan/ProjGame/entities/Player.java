package com.Trongtoan.ProjGame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class Player {
    private Texture texture;
    private Sprite sprite;
    private Vector2 position;
    private Vector2 velocity;
    private Rectangle bounds;
    private float speed = 800f  ;
    private boolean onGround = false;
    private float gravity = 900f;
    private float flypower = 500f;
    private boolean isflying = false;
    private boolean isJumping = false;
    private float yVelocity = 0;
    private float preJumpHeight = 0;


    private final List<Rectangle> groundRects;

    public Player(List<Rectangle> groundRects) {
        texture = new Texture(Gdx.files.internal("Player/player.png"));
        sprite = new Sprite(texture);
        sprite.setSize(128, 128);
        position = new Vector2(100  , 500); // bắt đầu ở giữa trời để rơi xuống
        velocity = new Vector2(0, 0);
        bounds = new Rectangle(position.x, position.y, sprite.getWidth(), sprite.getHeight());
        this.groundRects = groundRects;
    }

    public void update(float deltaTime) {
        boolean pressingLeft = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean pressingRight = Gdx.input.isKeyPressed(Input.Keys.D);
        boolean pressingUp = Gdx.input.isKeyPressed(Input.Keys.W);

        float moveX = 0;

        if (pressingLeft ){
            moveX = -speed;
        }
        if (pressingRight) {
            moveX = speed;
        }

        // Lật hướng mặt nhân vật
        if (pressingLeft && sprite.isFlipX()) sprite.flip(true, false);
        if (pressingRight && !sprite.isFlipX()) sprite.flip(true, false);

        // Jumping (nro-style)
        if (isJumping) {
            position = new Vector2(position.x, position.y + yVelocity*deltaTime);

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
        bounds.setPosition(position);
        sprite.setPosition(position.x, position.y);
    }

//    private float getGroundBelowPlayerY() {
//        float closestY = Float.NEGATIVE_INFINITY;
//
//        for (Rectangle ground : groundRects) {
//            boolean isBelow = ground.y + ground.height <= position.y;
//            boolean isHorizontallyAligned =
//                position.x >= ground.x && position.x <= ground.x + ground.width;
//
//            if (isBelow && isHorizontallyAligned) {
//                if (ground.y + ground.height > closestY) {
//                    closestY = ground.y + ground.height;
//                }
//            }
//        }
//
//        return closestY; // hoặc trả về -1 nếu không có ground bên dưới
//    }

    public void updateBounds() {
        bounds.set(position.x, position.y, sprite.getWidth(), sprite.getHeight());
    }

    public void setSize(float width, float height) {
        sprite.setSize(width, height);
        updateBounds();
    }
    public void setSpeed(float speed){
        this.speed = speed;
    }
    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public Vector2 getPosition() {
        return position; //cam đi theo
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
        bounds.setPosition(position);
        sprite.setPosition(x, y);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void dispose() {
        texture.dispose();
    }
}
