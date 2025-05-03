package com.Trongtoan.ProjGame.entities;

import com.Trongtoan.ProjGame.animation.PlayerAnimationManager;
import com.Trongtoan.ProjGame.skills.Skill;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class Player {
    private Vector2 position;
    private Vector2 velocity;
    private Rectangle bounds;
    private float speed = 800f;
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
    private int baseDamage = 50;



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
    private Monster selectedTarget;



    public Player(List<Rectangle> groundRects) {
        animationManager.setScale(scale);

        position = new Vector2(100, 500); // bắt đầu ở giữa trời để rơi xuống
        velocity = new Vector2(0, 0);
        bounds = new Rectangle(position.x, position.y, 127 * scale, 201 * scale); // cập nhật theo scale
        this.groundRects = groundRects;
        this.currentHp = 1000;
        this.maxHp = 1000;
        this.currentMp = 1000;
        this.maxMp = 1000;
    }

    public void update(float deltaTime) {

        if (isAttacking) {
            System.out.println("isAttacking = true");
            attackTimer -= deltaTime;
            if (attackTimer <= 0) {
                isAttacking = false;
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
        if (pressingLeft) {
            moveX = -speed;
            flipX = true;
        }
        if (pressingRight) {
            moveX = speed;
            flipX = false;
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
        bounds.set(position.x, position.y, animationManager.getScaledWidth(), animationManager.getScaledHeight());



        //  Nếu chạm đất thì dừng cưỡi
        if (ridingUfo && !isflying && onGround && ufo != null) {
            ridingUfo = false;
            ufo.startExit();
        }


        //  Đổi animation
        PlayerAnimationManager.State animationState;

        if (isAttacking) {
            animationState = PlayerAnimationManager.State.PUNCH;
        } else if (ridingUfo) {
            animationState = PlayerAnimationManager.State.IDLE;
        } else if (!onGround) {
            animationState = (velocity.y > 0) ? PlayerAnimationManager.State.JUMP : PlayerAnimationManager.State.FALL;
        } else if (pressingLeft || pressingRight) {
            animationState = PlayerAnimationManager.State.RUN;
        } else {
            animationState = PlayerAnimationManager.State.IDLE;
        }

        animationManager.update(deltaTime, animationState);


    }


    public void attackWithSkill(Skill currentSkill,Monster target,float delta){
        if (target == null)System.out.println("Target hien tai:"  + "null");
        if(!currentSkill.isReady())System.out.println("Skill ready? " + currentSkill.isReady());
        if (target == null || !currentSkill.isReady()) return;
//        float distance = getPosition().dst(target.getPosition());
//        if(distance>currentSkill.getRange()){
        this.isAttacking = true;
        this.attackTimer = this.attackDuration;
        target.takeDamage(baseDamage * currentSkill.getDamageMultiplier());
        currentSkill.startCooldown();
        // TODO: di chuyển lại gần
    }

    public void updateBounds() {
        bounds.set(position.x, position.y, animationManager.getScaledWidth(), animationManager.getScaledHeight());
    }

    public void setSize(float scale) {
        this.scale = scale;
        animationManager.setScale(scale);
        updateBounds();
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void draw(SpriteBatch batch) {
        animationManager.draw(batch, position.x, position.y, flipX);
    }

    public void drawWithUfo(SpriteBatch batch) {
        // Vẽ UFO trước (nằm dưới player)
        if (ufo != null && ufo.isActive()) {
            ufo.draw(batch);
        }
        // Vẽ player
        animationManager.draw(batch, position.x, position.y, flipX);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
        bounds.setPosition(position);
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



    public void setSelectedTarget(Monster monster) {
        this.selectedTarget = monster;
    }

    public Monster getSelectedTarget() {
        return selectedTarget;
    }

    public void punch(Monster monster, float damageMutiplier){
        int damage = (int)(baseDamage*damageMutiplier);
        monster.takeDamage(damage);
    }

    public UFO getUfo() {
        return ufo;
    }

}
