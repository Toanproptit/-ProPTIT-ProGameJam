package com.Trongtoan.ProjGame.screen;

import com.Trongtoan.ProjGame.Main;
import com.Trongtoan.ProjGame.entities.Monster;
import com.Trongtoan.ProjGame.entities.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {

    private final Main game;
    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera camera;
    private Player player;
    private Monster monster;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private List<Rectangle> groundRects;
    private int mapWidth, mapHeight;
    private String currentMapFile;
    private List<Monster> monsters;
    private OrthographicCamera hudCamera;



    public GameScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();

        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

        camera = new OrthographicCamera();

        map = new TmxMapLoader().load("Map/Home/Home.tmx");
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


        if(Gdx.input.justTouched()){
            Vector3 touch = new Vector3(Gdx.input.getX(),Gdx.input.getY(),0);
            camera.unproject(touch);
            checkClickOnMonster(touch.x,touch.y);
        }

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
        hudCamera.update();


        batch.setProjectionMatrix(camera.combined);
        mapRenderer.setView(camera);
        mapRenderer.render();


        batch.begin();
        player.draw(batch);

        for(Monster monster: monsters){
            monster.update(delta);
            monster.draw(batch);
            monster.drawInfo(batch,font);
        }
        batch.end();

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        drawPlayerStats(batch, font);
        batch.end();
    }

    private void checkClickOnMonster(float touchX,float touchY){
        for(Monster monster: monsters){
            if(monster.getBounds().contains(touchX,touchY)){
                monster.setSelected(true);// chọn quái click vào
            }
            else {
                monster.setSelected(false); // không phải thì đánh dấu chưa được chọn
            }
        }
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
            player.setSize(0.5f);
            player.setSpeed(800f);
            camera.setToOrtho(false, 2400, 1400);
        }
        else if(mapFile.contains("Map4")){
            monsters = new ArrayList<>();
            player.setSize(0.9f);
            player.setSpeed(400f);
            camera.setToOrtho(false, 1600, 1200);
        }else if(mapFile.contains("Map1")) {
            loadMonster();
            player.setSize(0.3f);
            player.setSpeed(400f);
            camera.setToOrtho(false, 1200, 800);
        }
        else{
            monsters = new ArrayList<>();
            player.setSize(0.9f);
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

    public void drawPlayerStats(SpriteBatch batch, BitmapFont font){
        float hpPercent = player.getCurrentHp()/ player.getMaxHp(); // Phần trăm hp:))
        float mpPercent = player.getCurrentMp()/ player.getMaxMp(); // Phần trăm mp , cái giá của việc ngu tiếng anh@@

        float barWidth = 200; // chiều dài thanh
        float barHeight = 20; // chiều cao mốĩ thanh

        float x = 20;
        float y = Gdx.graphics.getHeight() - 50;

        batch.end();

        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled); // filled vẽ hình đặc, line vẽ viền, point vẽ từng chấm :v

        // Vẽ Khung đen
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(x-2,y-2,barWidth+4,barHeight*2+10);
        // Vẽ thanh HP màu đỏ
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(x,y,barWidth*hpPercent,barHeight);
        // Vẽ thanh MP màu  xanh
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(x,y-barHeight-5,barWidth*mpPercent,barHeight);

        shapeRenderer.end();
        shapeRenderer.dispose();

        batch.begin();
        font.draw(batch, "HP: " + (int)player.getCurrentHp() + " / " + (int)player.getMaxHp(), x + 10, y + barHeight - 5);
        font.draw(batch, "MP: " + (int)player.getCurrentMp() + " / " + (int)player.getMaxMp(), x + 10, y - 5);
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
