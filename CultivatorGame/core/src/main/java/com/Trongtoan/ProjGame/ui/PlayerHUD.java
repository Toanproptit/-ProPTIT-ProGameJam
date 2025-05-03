package com.Trongtoan.ProjGame.ui;

import com.Trongtoan.ProjGame.entities.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class PlayerHUD {
    private final Player player;
    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer;

    public PlayerHUD(Player player) {
        this.player = player;
        this.font = new BitmapFont();
        this.shapeRenderer = new ShapeRenderer();
    }

    public void draw(SpriteBatch batch) {
        float hpPercent = player.getCurrentHp() / player.getMaxHp();
        float mpPercent = player.getCurrentMp() / player.getMaxMp();

        float barWidth = 200;
        float barHeight = 20;
        float x = 20;
        float y = Gdx.graphics.getHeight() - 50;

        batch.end();

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(x - 2, y - 2, barWidth + 4, barHeight * 2 + 10);

        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(x, y, barWidth * hpPercent, barHeight);

        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(x, y - barHeight - 5, barWidth * mpPercent, barHeight);

        shapeRenderer.end();
        batch.begin();

        font.draw(batch, "HP: " + (int) player.getCurrentHp() + " / " + (int) player.getMaxHp(), x + 10, y + barHeight - 5);
        font.draw(batch, "MP: " + (int) player.getCurrentMp() + " / " + (int) player.getMaxMp(), x + 10, y - 5);
    }

    public void dispose() {
        font.dispose();
        shapeRenderer.dispose();
    }
}
