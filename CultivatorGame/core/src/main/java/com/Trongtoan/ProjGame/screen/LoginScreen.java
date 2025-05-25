package com.Trongtoan.ProjGame.screen;

import com.Trongtoan.ProjGame.Main;
import com.Trongtoan.ProjGame.screen.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class LoginScreen implements Screen {
    private Main game;
    private Stage stage;
    private Skin skin;
    private TextField usernameField;
    private TextField passwordField;
    private TextButton loginButton;

    public LoginScreen(Main game) {
        this.game = game; // kết nối tới Game
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("Ui/uiskin.json")); // skin mặc định của LibGDX

        loginButton = new TextButton("Login", skin);
        Image background = new Image(new Texture(Gdx.files.internal("Ui/login_bg.png")));
        background.setFillParent(true);
        stage.addActor(background);
        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                game.setScreen(new GameScreen(game));
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center();



        table.add(loginButton).colspan(2).padTop(10);

        stage.addActor(table);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }



    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}

    @Override public void resume() {}

    @Override public void hide() {}

    @Override public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
