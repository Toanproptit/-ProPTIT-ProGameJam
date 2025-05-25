package com.Trongtoan.ProjGame.ui;

import com.Trongtoan.ProjGame.entities.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;

public class PlayerHUD {
    private final Player player;
    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer;

    private Stage uiStage;
    private Texture topbar;
    private Button menuButton;
    private Button respawnButton;
    private SidePanel sidePanel;
    private BottomMenuPanel bottomMenu;

    private PotentialPanel potentialPanel;

    private final List<FloatingText> floatingTexts = new ArrayList<>();
    private final BitmapFont floatingFont = new BitmapFont();

    private Label potentialLabel;

    public PlayerHUD(Player player) {
        Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        this.player = player;
        this.font = new BitmapFont();
        this.shapeRenderer = new ShapeRenderer();

        topbar = new Texture("ui/topbar.png");
        uiStage = new Stage(new ScreenViewport());


        potentialPanel = new PotentialPanel(skin,player);
        bottomMenu = new BottomMenuPanel(potentialPanel);
        potentialPanel.setBottomMenu(bottomMenu);
        sidePanel = new SidePanel();

        potentialLabel = new Label("Tiềm năng: " + player.getPotentialPoints(), skin);
        sidePanel.addActor(potentialLabel);

        menuButton = new Button("ui/menu.png");
        respawnButton = new Button("ui/respawn_button.png");
        respawnButton.setPosition(Gdx.graphics.getWidth()/2f-respawnButton.getWidth()/2f,50f);
        respawnButton.setToggleListener(()->{
            player.respawn();
            respawnButton.setVisible(false);
        });
        respawnButton.setVisible(false);

        menuButton.setToggleListener(() -> {
            bottomMenu.toggle();
        });

        uiStage.addActor(potentialPanel);
        uiStage.addActor(bottomMenu);
        uiStage.addActor(respawnButton);
        uiStage.addActor(sidePanel);
        uiStage.addActor(menuButton);

        Gdx.input.setInputProcessor(uiStage);// Cho phép nhận input nếu cần

    }

    public void draw(SpriteBatch batch) {
        potentialLabel.setText("Tiềm năng: " + player.getPotentialPoints());
        float hpPercent = player.getCurrentHp() / player.getMaxHp();
        float mpPercent = player.getCurrentMp() / player.getMaxMp();

        float hpWidth = 142;
        float hpHeight = 18;
        float x = 90;
        float y = Gdx.graphics.getHeight() - 10 - hpHeight;

        float mpWidth = 100;
        float mpHeight = 12;




        batch.end();

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);


        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(x, y, hpWidth * hpPercent, hpHeight);

        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(x, y - mpHeight - 14, mpWidth * mpPercent, mpHeight);

        shapeRenderer.end();
        batch.begin();


        if (player.isDie()) {
            respawnButton.setVisible(true);
        } else {
            respawnButton.setVisible(false);
        }

        float hudX = 0;
        float hudY = Gdx.graphics.getHeight() - topbar.getHeight(); // để canh lên góc trên
        batch.draw(topbar, hudX, hudY);

        uiStage.act(Gdx.graphics.getDeltaTime());
        uiStage.draw();

        for (int i = 0; i < floatingTexts.size(); i++) {
            FloatingText text = floatingTexts.get(i);
            text.update(Gdx.graphics.getDeltaTime());
            text.draw(batch);
            if (text.isFinished()) {
                floatingTexts.remove(i--);
            }
        }

        potentialPanel.update(player);

    }

    public void showPotentialGain(int amount, Vector2 position) {
        System.out.println("[FloatingText] + " + amount + " tại " + position);
        floatingTexts.add(new FloatingText(
            "+ " + amount ,
            position.cpy().add(0, 30),
            floatingFont,
            Color.GREEN
        ));
    }

    public void dispose() {
        font.dispose();
        shapeRenderer.dispose();
        uiStage.dispose();
    }
}
