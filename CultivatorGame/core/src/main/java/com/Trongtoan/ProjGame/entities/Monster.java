package com.Trongtoan.ProjGame.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Monster {
    private String type;
    private float hp;
    private float maxHp;
    private float respawnTime;
    private float respawnTimer;
    private Texture texture;
    private Sprite sprite;
    private Vector2 position;
    private Rectangle bounds;
    private boolean isAlive;
    private Vector2 velocity;
    private float startX,endX;
    private float speed = 25f;
    private boolean selected = false;

    public Monster(String type, String texturePath, float hp, float respawnTime, Vector2 position) {
        this.type = type;
        this.hp = hp;
        this.maxHp = hp;
        this.isAlive = true;
        this.respawnTime = respawnTime;
        this.respawnTimer = 0;
        try {
            this.texture = new Texture(texturePath);
        } catch (Exception e) {
            System.out.println("[ERROR] Texture not found at: " + texturePath);
            this.texture = new Texture("Player/player.png"); // fallback texture
        }
        this.sprite = new Sprite(texture);
        this.sprite.setSize(64, 64);
        this.position = position.cpy();
        this.bounds = new Rectangle(position.x, position.y, sprite.getWidth(), sprite.getHeight());

        this.startX = position.x-25;
        this.endX = position.x+25;
        this.velocity = new Vector2(speed,0);
    }

    public void update(float deltaTime) {
        if (!isAlive) {
            respawnTimer += deltaTime;
            if (respawnTimer >= respawnTime) {
                hp = maxHp;
                isAlive = true;
                respawnTimer = 0;
                position.x = (startX + endX) / 2f;
            }
            return;
        }
        position.x += velocity.x*deltaTime;

        if(position.x<startX){
            position.x = startX;
            velocity.x = speed;
        }
        else if(position.x>endX){
            position.x = endX;
            velocity.x = -speed;
        }
        bounds.set(position.x, position.y, sprite.getWidth(), sprite.getHeight());
    }

    public void drawInfo(SpriteBatch batch, BitmapFont font){
        if(!selected||!isAlive){
            return;
        }
        float barWidth =100;
        float barheight=10;

        float barX = position.x + sprite.getWidth()/2 - barWidth/2;
        float barY = position.y+ sprite.getHeight()+10;

        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(barX-2,barY-2,barWidth+4,barheight+4);

        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(barX,barY,barWidth*(hp/maxHp),barheight);

        shapeRenderer.end();
        shapeRenderer.dispose();
        batch.begin();

        font.draw(batch,type,barX,barY+25);

    }

    public void draw(SpriteBatch batch) {
        if (isAlive) {
            sprite.setPosition(position.x, position.y);
            sprite.draw(batch);
        }
    }

    public void takeDamage(float amount) {
        if (!isAlive) return;
        hp -= amount;
        if (hp <= 0) {
            isAlive = false;
            respawnTimer = 0;
        }
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Vector2 getPosition() {
        return position;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
