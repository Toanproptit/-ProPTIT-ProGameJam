package com.Trongtoan.ProjGame.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class FloatingText {
    private String text;
    private Vector2 position;
    private Color color;
    private float alpha = 1f;
    private float lifetime = 1.5f; // giây
    private float elapsed = 0f;
    private BitmapFont font;

    public boolean isFinished() {
        return elapsed >= lifetime;
    }

    public FloatingText(String text, Vector2 position, BitmapFont font, Color color) {
        this.text = text;
        this.position = position.cpy();
        this.font = font;
        this.color = color;
    }

    public void update(float delta) {
        elapsed += delta;
        position.y += 20 * delta; // bay lên
        alpha = 1f - (elapsed / lifetime); // mờ dần
    }

    public void draw(SpriteBatch batch) {
        font.setColor(color.r, color.g, color.b, alpha);
        font.draw(batch, text, position.x, position.y);
        font.setColor(Color.WHITE); // reset màu font
    }
}
