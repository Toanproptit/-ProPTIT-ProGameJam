    package com.Trongtoan.ProjGame.screen;

    import com.Trongtoan.ProjGame.Main;
    import com.Trongtoan.ProjGame.entities.*;
    import com.Trongtoan.ProjGame.logic.Mission;
    import com.Trongtoan.ProjGame.logic.MissionManager;
    import com.Trongtoan.ProjGame.ui.FloatingText;
    import com.Trongtoan.ProjGame.ui.PlayerHUD;
    import com.Trongtoan.ProjGame.ui.SkillSlotManager;
    import com.badlogic.gdx.Gdx;
    import com.badlogic.gdx.Screen;
    import com.badlogic.gdx.audio.Music;
    import com.badlogic.gdx.graphics.GL20;
    import com.badlogic.gdx.graphics.OrthographicCamera;
    import com.badlogic.gdx.graphics.g2d.BitmapFont;
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

        private SkillSlotManager skillUI;
        private PlayerHUD playerHUD;



        private Monster selected;

        private OrthographicCamera hudCamera;

        private MissionManager missionManager;
        private Rectangle portalRect;

        private Music bgMusic;

        public GameScreen(Main game) {
            this.game = game;
        }

        @Override
        public void show() {
            bgMusic = Gdx.audio.newMusic(Gdx.files.internal("Sound/back.mp3"));
            bgMusic.setLooping(true);
            bgMusic.setVolume(0.1f); // 50% volume
            bgMusic.play();

            batch = new SpriteBatch();
            font = new BitmapFont();

            missionManager = new MissionManager();

            missionManager.setMissionSequence(List.of(
                new Mission("kill_doll_5", "Defeat 5 training dummies"),
                new Mission("reach_stats", "Reach 500 HP, 500 MP, and 50 base damage")
            ));

            hudCamera = new OrthographicCamera();
            hudCamera.setToOrtho(false,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

            camera = new OrthographicCamera();

            map = new TmxMapLoader().load("Map/Home/Home.tmx");
            mapRenderer = new OrthogonalTiledMapRenderer(map);
            camera.setToOrtho(false,2500 ,1300);//3000,1800

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
            playerHUD = new PlayerHUD(player);
            skillUI = new SkillSlotManager(player);
            loadMonster();
            skillUI.setMonsters(monsters);
        }

        @Override
        public void render(float delta) {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            player.update(delta);
            if (player.isDie()) {
                player.respawn();
                player.resetDeathCount(); // bạn sẽ tạo thêm hàm này bên dưới
            }
            if (player.getUfo() != null && player.getUfo().isActive()) {
                player.getUfo().update(delta, camera);
            }
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
            player.drawWithUfo(batch);

            for(Monster monster: monsters){
                monster.update(delta,player);
                monster.draw(batch,player);
                monster.drawInfo(batch,font);
            }
            batch.end();

            batch.setProjectionMatrix(hudCamera.combined);
            batch.begin();
            playerHUD.draw(batch);
            skillUI.draw(batch);


            // nhiệm vụ
            Mission current = missionManager.getCurrentMission();
            if (current != null && !current.isCompleted()) {
                String missionText = "";

                switch (current.getId()) {
                    case "kill_doll_5":
                        missionText = "Training: " + player.getDollsKilled() + "/5 dummies defeated";
                        break;

                    case "reach_stats":
                        missionText = String.format(
                            "Stats: HP %d/500 | MP %d/500 | DMG %d/50",
                            (int) player.getMaxHp(),
                            (int) player.getMaxMp(),
                            player.getBaseDamage()
                        );
                        break;
                    case "kill_boar_30":
                        missionText = "Training: " + player.getBoarsKilled() + "/30 minotaur defeated";
                        break;
                    case "reach_stats_map1":
                        missionText = String.format(
                            "Stats: HP %d/1000 | MP %d/1000 | DMG %d/100",
                            (int) player.getMaxHp(),
                            (int) player.getMaxMp(),
                            player.getBaseDamage()
                        );
                        break;
                    case "reach_stats_map3":
                        missionText = String.format(
                            "Stats: HP %d/2000 | MP %d/2000 | DMG %d/200",
                            (int) player.getMaxHp(),
                            (int) player.getMaxMp(),
                            player.getBaseDamage()
                        );
                        break;
                    case "reach_stats_map4":
                        missionText = String.format(
                            "Stats: HP %d/3000 | MP %d/3000 | DMG %d/300",
                            (int) player.getMaxHp(),
                            (int) player.getMaxMp(),
                            player.getBaseDamage()
                        );
                        break;
                    case "boss_defeat":
                        missionText = "Mission: Defeat the boss!";
                        break;
                    case "kill_golem_20":
                        missionText = "Mission: " + player.getGolemsKilled() + "/20 Golems defeated";
                        break;
                    case  "kill_demonfly_20":
                        missionText =  "Mission: " + player.getDemonflyKilled() + "/20 Demonflys defeated";
                        break;
                    default:
                        missionText = current.getDescription();
                        break;
                }


                font.draw(batch, missionText, 30, Gdx.graphics.getHeight() - 90);
            }

            if (portalRect != null) {
                missionManager.updateMissions(player, monsters, portalRect);
            }

            batch.end();
            skillUI.update(delta);

            if(Gdx.input.justTouched()) {
                Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                hudCamera.unproject(touch); // dùng hudCamera vì UI theo screen
                skillUI.handleClick(touch.x,touch.y); // ✅ chọn skill
                camera.unproject(touch);              // dùng camera chính để chọn quái
                checkClickOnMonster(touch.x, touch.y); // ✅ chọn quái
            }

        }

        private void checkClickOnMonster(float touchX, float touchY){
            Monster selected = null;

            for (Monster monster : monsters) {
                if (monster.getBounds().contains(touchX, touchY)) {
                    monster.setSelected(true);
                    selected = monster;
                } else {
                    monster.setSelected(false);
                }
            }

            player.setSelectedTarget(selected); // ✅ Gán đúng vào Player
//            skillUI.setManualSelection(true);
        }





        private void checkPortalCollision(){
            MapLayer portalLayer = map.getLayers().get("Portal");
            if(portalLayer== null )return;
            for(MapObject object : portalLayer.getObjects()){
                if(object instanceof RectangleMapObject){
                    Rectangle rect= ((RectangleMapObject)object).getRectangle();
                    portalRect = rect;
                    if(player.getBounds().overlaps(rect)){
                        if (!missionManager.hasMissionInProgress()) { // ✅ Đã xong hết nhiệm vụ
                            String nextMap = object.getProperties().get("toMap", String.class);
                            float spawnX = object.getProperties().get("spawnX", Float.class);
                            float spawnY = object.getProperties().get("spawnY", Float.class);

                            changeMap(nextMap, spawnX, spawnY);
                        } else {
                            System.out.println("❌ Chưa hoàn thành nhiệm vụ để vào cổng!");

                        }

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
    //        player = new Player(groundRects);
            player.setPosition(spawnX, spawnY);

            if (mapFile.contains("Map3")) {
                player.resetDeathCount();
                loadMonster();
                missionManager.setMissionSequence(List.of(
                    new Mission("kill_golem_20", "Defeat 20 Golems"),
                    new Mission("reach_stats_map3", "Reach 2000 HP, 2000 MP, and 200 base damage"),
                    new Mission("boss_defeat", "Defeat the final boss")
                ));
                player.setSize(0.8f);
                player.setSpeed(800f);
                camera.setToOrtho(false, 2400, 1400);
            }
            else if(mapFile.contains("Map4")){
                loadMonster();
                player.resetDeathCount();
                missionManager.setMissionSequence(List.of(
                    new Mission("kill_demonfly_20","Defeat 20 Demonflys"),
                    new Mission("reach_stats_map4", "Reach 3000 HP, 3000 MP, and 300 base damage")
                ));
                player.setSize(0.6f);
                player.setSpeed(400f);
                camera.setToOrtho(false, 1600, 1200);
            }else if(mapFile.contains("Map1")) {
                player.resetDeathCount();
                loadMonster();
                missionManager.setMissionSequence(List.of(
                    new Mission("kill_boar_30", "Defeat 30 wild boars"),
                    new Mission("reach_stats_map1", "Reach 1000 HP, 1000 MP, and 100 base damage")
                ));
                player.setSize(0.4f);
                player.setSpeed(400f);
                camera.setToOrtho(false, 1200, 800);
            }
            else if(mapFile.contains("Map2")){
                player.resetDeathCount();
                loadMonster();
                missionManager.setMissionSequence(List.of(
                    new Mission("boss_defeat", "Defeat the boss")
                ));
                player.setSize(0.4f);
                player.setSpeed(400f);
                camera.setToOrtho(false, 1200, 800);
            }
            else{
                player.resetDeathCount();
                loadMonster();
                missionManager.setMissionSequence(List.of(
                    new Mission("boss_defeat", "Defeat the boss")
                ));
                player.setSize(0.4f);
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
            monsters.clear();
            MapLayer monsterLayer = map.getLayers().get("Monster");
            if (monsterLayer != null) {
                for (MapObject obj : monsterLayer.getObjects()) {
                    if (obj instanceof RectangleMapObject) {
                        Rectangle rect = ((RectangleMapObject) obj).getRectangle();
                        try {
                            String type = obj.getProperties().get("type", String.class);

                            float hp = obj.getProperties().get("hp", Float.class);
                            float respawn = obj.getProperties().get("respawnTime", Float.class);

                            Vector2 spawnPos = new Vector2(rect.x, rect.y);
                            if("Boss".equalsIgnoreCase(type)){
                                Boss boss = new Boss(type, hp, respawn, spawnPos, 50f, 300f,100); // tùy theo boss
                                monsters.add(boss);
                            }
                            else if("BossM3".equalsIgnoreCase(type)) {
                                Boss boss = new Boss(type, hp, respawn, spawnPos, 100f, 300f,150); // tùy theo boss
                                monsters.add(boss);
                            }
                            else if("final_Boss".equalsIgnoreCase(type)) {
                                FinalBoss boss = new FinalBoss(spawnPos);// tùy theo boss
                                monsters.add(boss);
                            }

                            else {
                                    if ("minotaur".equalsIgnoreCase(type)) {
                                        Monster boss = new Monster(type, hp, respawn, spawnPos,20f,50,10);
                                        monsters.add(boss);
                                    }
                                    else if("Demonfly".equalsIgnoreCase(type)){
                                        Monster monster = new Monster(type, hp, respawn, spawnPos,60f,300,20);
                                        monsters.add(monster);
                                        if (currentMapFile.contains("Map4")) {
                                            monster.setSize(2.5f);
                                        } else if (currentMapFile.contains("Map3")) {
                                            monster.setSize(2f); // to hơn
                                        }
                                    }
                                    else if("Doll".equalsIgnoreCase(type)) {
                                        Doll doll = new Doll(type,hp,respawn,spawnPos,0,0,5);
                                        monsters.add(doll);
                                    }
                                    else{
                                        Monster boss = new Monster(type, hp, respawn, spawnPos,40f,200,15);
                                        monsters.add(boss);
                                    }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            skillUI.setMonsters(monsters);
            for (Monster m : monsters) {
                m.setPlayerHUD(playerHUD);
                m.setCamera(camera);
            }

        }


    //    public void drawPlayerStats(SpriteBatch batch, BitmapFont font){
    //        float hpPercent = player.getCurrentHp()/ player.getMaxHp(); // Phần trăm hp:))
    //        float mpPercent = player.getCurrentMp()/ player.getMaxMp(); // Phần trăm mp , cái giá của việc ngu tiếng anh@@
    //
    //        float barWidth = 200; // chiều dài thanh
    //        float barHeight = 20; // chiều cao mốĩ thanh
    //
    //        float x = 20;
    //        float y = Gdx.graphics.getHeight() - 50;
    //
    //        batch.end();
    //
    //        ShapeRenderer shapeRenderer = new ShapeRenderer();
    //        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
    //        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled); // filled vẽ hình đặc, line vẽ viền, point vẽ từng chấm :v
    //
    //        // Vẽ Khung đen
    //        shapeRenderer.setColor(Color.BLACK);
    //        shapeRenderer.rect(x-2,y-2,barWidth+4,barHeight*2+10);
    //        // Vẽ thanh HP màu đỏ
    //        shapeRenderer.setColor(Color.RED);
    //        shapeRenderer.rect(x,y,barWidth*hpPercent,barHeight);
    //        // Vẽ thanh MP màu  xanh
    //        shapeRenderer.setColor(Color.BLUE);
    //        shapeRenderer.rect(x,y-barHeight-5,barWidth*mpPercent,barHeight);
    //
    //        shapeRenderer.end();
    //        shapeRenderer.dispose();
    //
    //        batch.begin();
    //        font.draw(batch, "HP: " + (int)player.getCurrentHp() + " / " + (int)player.getMaxHp(), x + 10, y + barHeight - 5);
    //        font.draw(batch, "MP: " + (int)player.getCurrentMp() + " / " + (int)player.getMaxMp(), x + 10, y - 5);
    //    }

        @Override public void resize(int width, int height) {}
        @Override public void pause() {}
        @Override public void resume() {}
        @Override public void hide() {}
        @Override public void dispose() {
            batch.dispose();
            player.dispose();
            map.dispose();
            if (bgMusic != null) bgMusic.dispose();
            mapRenderer.dispose();
        }
    }
