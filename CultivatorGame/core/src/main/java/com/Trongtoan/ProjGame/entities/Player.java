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

    private float speed = 400f;
    private boolean onGround = false;

    private final List<Rectangle> groundRects;

    public Player(List<Rectangle> groundRects) {
        texture = new Texture(Gdx.files.internal("player.png"));
        sprite = new Sprite(texture);
        sprite.setSize(64, 64);
        position = new Vector2(100  , 300); // bắt đầu ở giữa trời để rơi xuống
        velocity = new Vector2(0, 0);
        bounds = new Rectangle(position.x, position.y, sprite.getWidth(), sprite.getHeight());
        this.groundRects = groundRects;
    }

    public void update(float deltaTime) {
        boolean movingLeft = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean movingRight = Gdx.input.isKeyPressed(Input.Keys.D);

        float moveX = 0;
        if (movingLeft) moveX = -speed;
        if (movingRight) moveX = speed;

        // Lật hướng mặt nhân vật
        if (movingLeft && sprite.isFlipX()) sprite.flip(true, false);
        if (movingRight && !sprite.isFlipX()) sprite.flip(true, false);

        // Nhảy
        if (Gdx.input.isKeyJustPressed(Input.Keys.W) && onGround) {
            velocity.y = 550f;
            onGround = false;
        }

        // Trọng lực
        float gravity = 900f;
        velocity.y -= gravity * deltaTime;

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
                newPos.x = position.x; // không di chuyển theo X
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

    public void setSize(float width, float height) {
        sprite.setSize(width, height);
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
