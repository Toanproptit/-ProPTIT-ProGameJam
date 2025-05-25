package com.Trongtoan.ProjGame.entities;

import com.Trongtoan.ProjGame.animation.PlayerAnimationManager;
import com.Trongtoan.ProjGame.skills.Skill;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

public class Player {

    private int deathCount = 0;


    private Vector2 position;
    private Vector2 velocity;
    private Rectangle bounds;
    private float speed = 1000f;
    private boolean onGround = false;
    private float gravity = 900f;
    private float flypower = 500f;
    private boolean isflying = false;
    private boolean isJumping = false;
    private float yVelocity = 0;
    private float preJumpHeight = 0;


    private float currentHp;
    private float maxHp;
    private float currentMp;
    private float maxMp;
    private int baseDamage ;
    private int potentialPoints = 0;




    private boolean isDie = false;


    private boolean flipX = false;
    private final PlayerAnimationManager animationManager = new PlayerAnimationManager();
    private final List<Rectangle> groundRects;

    private boolean ridingUfo = false;
    private UFO ufo;

    private float summonTimer =0;
    private boolean isSummoningUfo = false;
    private float summonDelay = 0.1f;

    private float scale = 0.8f;


    private boolean isAttacking= false ;
    private float attackTimer = 0;
    private float attackDuration = 0.8f;
    private float distance;
    private boolean isAutoMoving = false;
    private Monster selectedTarget;

    private Skill currentSkill = null;
    private Skill pendingSkillCast = null;
    private float attackEffectDelay = 0.3f; // thời điểm "ra đòn"
    private boolean skillEffectTriggered = false;

    // 🔥 Danh sách các quả cầu lửa do người chơi tạo
    private List<PlayerFireball> playerFireballs = new ArrayList<>();
    private Texture fireballFlyTex;
    private Texture fireballExplodeTex;


    private int dollsKilled = 0;
    private int boarsKilled = 0;
    private int golemKilled = 0;
    private int demonflyKilled = 0;

    public Player(List<Rectangle> groundRects) {
        animationManager.setScale(scale);

        position = new Vector2(100, 500);
        velocity = new Vector2(0, 0);
        this.groundRects = groundRects;
        this.maxHp = 300;
        this.maxMp = 300;
        this.currentHp = 300;
        this.currentMp = 300;
        this.baseDamage = 10;
        // ✅ Cập nhật bounds theo animation thay vì hardcode
        updateBounds();
    }

    public int getDeathCount() {
        return deathCount;
    }

    public void onKillDemonfly(){
        demonflyKilled++;
    }

    public int getDemonflyKilled(){
        return demonflyKilled;
    }

    public void onKillGolem(){
        golemKilled++;
    }
    public int getGolemsKilled(){
        return golemKilled;
    }
    public void onKillDoll() {
        dollsKilled++;
    }
    public int getDollsKilled() {
        return dollsKilled;
    }

    public void onKillBoar() {
        boarsKilled++;
    }

    public int getBoarsKilled() {
        return boarsKilled;
    }

    public void update(float deltaTime) {

        if (isDie) {
            animationManager.update(deltaTime, PlayerAnimationManager.State.DIE);
            return;
        }

        if (isAttacking) {
            attackTimer -= deltaTime;

            if (!skillEffectTriggered && attackTimer <= (attackDuration - 0.3f)) {
                skillEffectTriggered = true;

                if (pendingSkillCast != null && pendingSkillCast.getName().equals("Fireball") && selectedTarget != null) {
                    Vector2 start = new Vector2(position.x + bounds.width / 2f, position.y + bounds.height / 2f);
                    Vector2 target = selectedTarget.getCenter();
                    Vector2 direction = target.cpy().sub(start).nor();

                    System.out.println("🔥 Spawn fireball at " + start + " dir=" + direction);
                    System.out.println("🎯 Target: " + selectedTarget.getCenter());

                    PlayerFireball fb = new PlayerFireball(this,start, selectedTarget, direction,baseDamage * pendingSkillCast.getDamageMultiplier(),
                        fireballFlyTex, 4,
                        fireballExplodeTex, 4,
                        0.15f,scale
                    );
                    playerFireballs.add(fb); // 👈 đạn bay ra tại thời điểm rặn
                }
            }

            if (attackTimer <= 0) {
                isAttacking = false;
                pendingSkillCast = null;
                currentSkill = null;
                System.out.println("=> Hết thời gian tấn công");
            }
        }

        boolean pressingLeft = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean pressingRight = Gdx.input.isKeyPressed(Input.Keys.D);
        boolean pressingUp = Gdx.input.isKeyPressed(Input.Keys.W);

        float moveX = 0;

        //  Triệu hồi UFO khi nhấn W
        if (isflying && (pressingLeft ||pressingRight) && (ufo == null || !ufo.isActive()) && !ridingUfo) {
            isSummoningUfo =true;
            summonTimer=0;
        }
        if(isSummoningUfo){
            summonTimer+=deltaTime;
            if(summonTimer>=summonDelay){
                ufo = new UFO(-100, position.y-62);
                ufo.setSize(128*scale*2, 64*scale*2); // ví dụ: UFO thấp hơn nhân vật

                isSummoningUfo=false;
            }
        }

        // Nếu va chạm thì bắt đầu cưỡi UFO
        if (ufo != null && !ridingUfo && ufo.isActive() && bounds.overlaps(ufo.getBounds())) {
            ridingUfo = true;
            isflying = true;
        }

        //  Xử lý di chuyển trái/phải

        if (selectedTarget != null && !isAttacking && isAutoMoving) {
            float targetX = selectedTarget.getPosition().x;
            float targetY = selectedTarget.getPosition().y;
            float playerX = position.x;
            float playerY = position.y;

            distance = Math.abs(targetX - playerX);
            float dy = Math.abs(targetY - playerY);

            if (distance > 50f) {
                isAutoMoving = true;
                flipX = targetX < playerX;
                velocity.x = flipX ? -speed : speed;

                // ✅ Nếu quái ở trên cao → bật chế độ bay
                isflying = dy > 80f;
            } else {
                isAutoMoving = false;
                velocity.x = 0;
            }
        } else {
            isAutoMoving = false;
        }

        if(isAutoMoving){
            moveX =velocity.x;
        }
        else{
            if (pressingLeft) {
                moveX = -speed;
                flipX = true;
            }
            if (pressingRight) {
                moveX = speed;
                flipX = false;
            }
        }


        //  Nhảy
        if (isJumping) {
            position.y += yVelocity * deltaTime;
            if (position.y < preJumpHeight) {
                yVelocity += gravity * deltaTime;
            } else {
                position.y = preJumpHeight;
                yVelocity = 0;
                isJumping = false;
            }
        } else if (pressingUp && onGround) {
            isJumping = true;
            yVelocity = 300f;
            preJumpHeight = position.y + 150f;
        }

        //  Bay
        if ((pressingUp || (!onGround && (pressingLeft || pressingRight))) && !isflying) {
            isflying = true;
        }

        if (isflying) {
            velocity.y = pressingUp ? flypower : 0;
        } else {
            velocity.y -= gravity * deltaTime;
        }

        if (!pressingUp && !pressingRight && !pressingLeft) {
            isflying = false;
        }

        //  Nếu đang cưỡi UFO, gắn UFO theo player
        if (ridingUfo && ufo != null) {
            ufo.setPosition(position.x-ufo.getWidth()/2+ bounds.width*scale/2, (position.y-ufo.getHeight()));
        }


        // Tính vị trí mới
        Vector2 newPos = new Vector2(position);
        newPos.x += moveX * deltaTime;
        newPos.y += velocity.y * deltaTime;

        Rectangle newBoundsX = new Rectangle(newPos.x, position.y, bounds.width, bounds.height);
        Rectangle newBoundsY = new Rectangle(position.x, newPos.y, bounds.width, bounds.height);

        // Va chạm X
        for (Rectangle rect : groundRects) {
            if (newBoundsX.overlaps(rect)) {
                moveX = 0;
                newPos.x = position.x;

                if(isAutoMoving && !isJumping && !isflying){
                    isflying=true;
                }

                break;
            }
        }

        // Va chạm Y
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

        //  Gán lại vị trí và bounds
        position.set(newPos);
        updateBounds();


        //  Nếu chạm đất thì dừng cưỡi
        if (ridingUfo && !isflying && onGround && ufo != null) {
            ridingUfo = false;
            ufo.startExit();
        }


        //  Đổi animation
        PlayerAnimationManager.State animationState;





        if (isDie) {
            animationState = PlayerAnimationManager.State.DIE;
        } else if (isAttacking) {
            if (currentSkill != null && currentSkill.getName().equals("Fireball")) {
                animationState = PlayerAnimationManager.State.CAST;
            } else {
                animationState = PlayerAnimationManager.State.PUNCH;
            }
        } else if (ridingUfo) {
            animationState = PlayerAnimationManager.State.IDLE;
        } else if (!onGround) {
            animationState = (velocity.y > 0) ? PlayerAnimationManager.State.JUMP : PlayerAnimationManager.State.FALL;
        }
        else if (moveX != 0) {
                animationState = PlayerAnimationManager.State.RUN;
        }
        else {
            animationState = PlayerAnimationManager.State.IDLE;
        }

        animationManager.update(deltaTime, animationState);

        for (PlayerFireball fb : playerFireballs) {
            fb.update(deltaTime);

        }

//        System.out.println("🧮 Player fireballs: " + playerFireballs.size());

    }


    public void attackWithSkill(Skill skill, Monster target, float delta) {
        if (target == null || !skill.isReady()) return;

        float targetX = target.getPosition().x;
        float targetY = target.getPosition().y;
        float playerX = position.x;
        float playerY = position.y;

        float dx = Math.abs(targetX - playerX);
        float dy = Math.abs(targetY - playerY);

        float minRange = skill.getRange() - 25f; // cho phép khoảng dao động ±25
        float maxRange = skill.getRange() + 25f;

        // Nếu quá xa → tự động tiến gần
        if (dx > maxRange || dy > 80f) {
            System.out.println("Quá xa — tiến lại gần mục tiêu");
            isAutoMoving = true;
            selectedTarget = target;
            return;
        }

        // Nếu quá gần → lùi lại cho đúng tầm
        if (dx < minRange) {
            float idealX = targetX + (playerX < targetX ? -skill.getRange() : skill.getRange());
            setPosition(idealX, position.y);
            updateBounds();
            System.out.println("Quá gần — lùi ra tầm dùng chiêu");
            return;
        }

        // ✅ Trong tầm — thực hiện tấn công
        this.currentSkill = skill;
        isAttacking = true;
        attackTimer = attackDuration;
        attackEffectDelay = 0.3f;
        skillEffectTriggered = false;
        pendingSkillCast = skill;

        if (skill.getName().equalsIgnoreCase("Punch")) {
            target.takeDamage(baseDamage * skill.getDamageMultiplier(), this);
            target.playHitAnimation();
        }

        skill.startCooldown();
    }





    public void updateBounds() {
        bounds = new Rectangle(position.x, position.y,
            animationManager.getScaledWidth(),
            animationManager.getScaledHeight());
    }

    public void setSize(float newScale) {
        Vector2 centerBefore = getCenter();
        this.scale = newScale;
        animationManager.setScale(scale);
        updateBounds();

        // ✅ Giữ nguyên vị trí trung tâm
        Vector2 centerAfter = getCenter();
        Vector2 delta = centerBefore.sub(centerAfter);
        position.add(delta);
        updateBounds();
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void draw(SpriteBatch batch) {
        animationManager.draw(batch, position.x, position.y, flipX);
        for (PlayerFireball fb : playerFireballs) fb.draw(batch);
    }

    public void drawWithUfo(SpriteBatch batch) {
        // Vẽ UFO trước (nằm dưới player)
        if (ufo != null && ufo.isActive()) {
            ufo.draw(batch);
        }
        // Vẽ player
        animationManager.draw(batch, position.x, position.y, flipX);
        for (PlayerFireball fb : playerFireballs) fb.draw(batch);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
        velocity.setZero();
        isJumping = false;
        isflying = false;
        isAutoMoving = false;
        selectedTarget = null;
        updateBounds();
    }

    public void takeDamage(float amount){
        if(isDie)return;
        currentHp-=amount;
        if(currentHp<0){
            currentHp=0;
            isDie = true;
            onDie();
        }
    }

    public void resetDeathCount() {
        deathCount = 0;
    }

    public void onDie(){
        velocity.setZero();
        deathCount++;
        System.out.println("Player died!");

    }

    public void increaseMaxHp() {
        if (potentialPoints > 0) {
            maxHp += 10;
            potentialPoints--;
        }
    }
    public void increaseMaxMp() {
        if (potentialPoints > 0) {
            maxMp += 10;
            potentialPoints--;
        }
    }
    public void increaseBaseDamage() {
        if (potentialPoints > 0) {
            baseDamage += 2;
            potentialPoints--;
        }
    }



    public Vector2 getCenter() {
        return new Vector2(position.x + animationManager.getScaledWidth() / 2f,
            position.y + animationManager.getScaledHeight() / 2f);
    }


    public Rectangle getBounds() {
        return bounds;
    }

    public void dispose() {
        animationManager.dispose();
    }

    public float getCurrentMp() {
        return currentMp;
    }

    public float getMaxHp() {
        return maxHp;
    }

    public float getMaxMp() {
        return maxMp;
    }

    public float getCurrentHp() {
        return currentHp;
    }

    public int getBaseDamage(){
        return baseDamage;
    }

    public void setUfo(UFO ufo){
        this.ufo = ufo;
    }

    public void setCurrentMp(float currentMp) {
        this.currentMp = currentMp;
    }

    public void setMaxHp(float maxHp) {
        this.maxHp = maxHp;
    }

    public void setCurrentHp(float currentHp) {
        this.currentHp = currentHp;
    }

    public void setMaxMp(float maxMp) {
        this.maxMp = maxMp;
    }

    public void setBaseDamage(int baseDamage){
        this.baseDamage = baseDamage;
    }

    public void addPotential(int amount){
        potentialPoints+=amount;
    }

    public int getPotentialPoints(){
        return potentialPoints;
    }

    public boolean isDie(){
        return isDie;
    }


    public void setSelectedTarget(Monster monster) {
        this.selectedTarget = monster;
    }

    public Monster getSelectedTarget() {
        return selectedTarget;
    }

    public void punch(Monster monster, float damageMutiplier){
        int damage = (int)(baseDamage*damageMutiplier);
        monster.takeDamage(damage,this);
        monster.playHitAnimation();
    }

    public void respawn() {
        isDie = false;
        setPosition(position.x, position.y); // hoặc dùng spawnPoint riêng
        currentHp = maxHp;
        currentMp = maxMp;
        System.out.println("Player respawned!");
    }

    public UFO getUfo() {
        return ufo;
    }

    public void setFireballTextures(Texture flyTex, Texture explodeTex) {
        this.fireballFlyTex = flyTex;
        this.fireballExplodeTex = explodeTex;
    }

}
