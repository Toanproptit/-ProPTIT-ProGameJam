package com.Trongtoan.ProjGame.screen;

import com.Trongtoan.ProjGame.Main;
import com.Trongtoan.ProjGame.entities.Monster;
import com.Trongtoan.ProjGame.entities.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {

    private final Main game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Player player;
    private Monster monster;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private List<Rectangle> groundRects;
    private int mapWidth, mapHeight;
    private String currentMapFile;
    private List<Monster> monsters;


    public GameScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        camera = new OrthographicCamera();

        map = new TmxMapLoader().load("Home/Home.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        camera.setToOrtho(false, 3000, 1800);

        groundRects = new ArrayList<>();
        MapLayer groundLayer = map.getLayers().get("Ground");
        if (groundLayer != null) {
            for (MapObject obj : groundLayer.getObjects()) {
                if (obj instanceof RectangleMapObject) {
                    groundRects.add(((RectangleMapObject) obj).getRectangle());
                }
            }
        }
        player = new Player(groundRects);
        monsters = new ArrayList<>();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        player.update(delta);
        checkPortalCollision();
        Vector2 playerPos = player.getPosition();

        int tileWidth = map.getProperties().get("tilewidth", Integer.class);
        int tileHeight = map.getProperties().get("tileheight", Integer.class);
        int width = map.getProperties().get("width", Integer.class);
        int height = map.getProperties().get("height", Integer.class);
        mapWidth = tileWidth * width;
        mapHeight = tileHeight * height;

        float camX = MathUtils.clamp(playerPos.x, camera.viewportWidth / 2f, mapWidth - camera.viewportWidth / 2f);
        float camY = MathUtils.clamp(playerPos.y, camera.viewportHeight / 2f, mapHeight - camera.viewportHeight / 2f);
        camera.position.set(camX, camY, 0);
        camera.update();

        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.draw(batch);

        for(Monster monster: monsters){
            monster.update(delta);
            monster.draw(batch);
        }
        batch.end();
    }

    private void checkPortalCollision(){
        MapLayer portalLayer = map.getLayers().get("Portal");
        if(portalLayer== null )return;
        for(MapObject object : portalLayer.getObjects()){
            if(object instanceof RectangleMapObject){
                Rectangle rect= ((RectangleMapObject)object).getRectangle();

                if(player.getBounds().overlaps(rect)){
                    String nextMap = object.getProperties().get("toMap", String.class);
                    float spawnX = object.getProperties().get("spawnX", Float.class);
                    float spawnY = object.getProperties().get("spawnY", Float.class);

                    changeMap(nextMap, spawnX, spawnY);
                    break;
                }
            }
        }
    }

    public void changeMap(String mapFile,float spawnX, float spawnY ){
        currentMapFile = mapFile;
        if(map!=null)map.dispose();
        if(mapRenderer!=null)mapRenderer.dispose();

        map = new TmxMapLoader().load(mapFile);
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        int tileWidth = map.getProperties().get("tilewidth", Integer.class);
        int tileHeight = map.getProperties().get("tileheight", Integer.class);
        int width = map.getProperties().get("width", Integer.class);
        int height = map.getProperties().get("height", Integer.class);
         mapWidth = tileWidth * width;
         mapHeight = tileHeight * height;



        groundRects.clear();
        MapLayer groundLayer = map.getLayers().get("Ground");
        if (groundLayer != null) {
            for (MapObject obj : groundLayer.getObjects()) {
                if (obj instanceof RectangleMapObject) {
                    groundRects.add(((RectangleMapObject) obj).getRectangle());
                }
            }
        }
        // Cập nhật lại Player và vị trí spawn
        player = new Player(groundRects);
        player.setPosition(spawnX, spawnY);

        if (mapFile.contains("Map3")) {
            monsters = new ArrayList<>();
            player.setSize(120, 120);
            player.setSpeed(800f);
            camera.setToOrtho(false, 2400, 1400);
        }
        else if(mapFile.contains("Map4")){
            monsters = new ArrayList<>();
            player.setSize(100, 100);
            player.setSpeed(400f);
            camera.setToOrtho(false, 1600, 1200);
        }else if(mapFile.contains("Map1")) {
            loadMonster();
            player.setSize(64, 64);
            player.setSpeed(400f);
            camera.setToOrtho(false, 1200, 800);
        }
        else{
            monsters = new ArrayList<>();
            player.setSize(64, 64);
            player.setSpeed(400f);
            camera.setToOrtho(false, 1200, 800);
        }

        // Cập nhật camera theo vị trí mới
        camera.position.set(spawnX, spawnY, 0);
        camera.update();

        // Nếu dùng batch riêng thì cập nhật lại projection matrix
        batch.setProjectionMatrix(camera.combined);
    }

    private void loadMonster(){
        MapLayer monsterLayer = map.getLayers().get("Monster");
        if (monsterLayer != null) {
            for (MapObject obj : monsterLayer.getObjects()) {
                if (obj instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) obj).getRectangle();
                    try {
                        String type = obj.getProperties().get("type", String.class);
                        String texture = obj.getProperties().get("texture", String.class);
                        float hp = obj.getProperties().get("hp", Float.class);
                        float respawn = obj.getProperties().get("respawnTime", Float.class);

                        Vector2 spawnPos = new Vector2(rect.x, rect.y);
                        monsters.add(new Monster(type, texture, hp, respawn, spawnPos));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        batch.dispose();
        player.dispose();
        map.dispose();
        mapRenderer.dispose();
    }
}
