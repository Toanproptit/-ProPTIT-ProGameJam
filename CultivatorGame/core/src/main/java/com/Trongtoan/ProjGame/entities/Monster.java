package com.Trongtoan.ProjGame.entities;

import com.Trongtoan.ProjGame.animation.EnemyAnimationManager;
import com.Trongtoan.ProjGame.animation.GolemAnimationManager;
import com.Trongtoan.ProjGame.animation.EnemyAnimationManager.EnemyState;
import com.Trongtoan.ProjGame.animation.GolemAnimationManager.GolemState;
import com.Trongtoan.ProjGame.animation.IAnimationManager;
import com.Trongtoan.ProjGame.ui.FloatingText;
import com.Trongtoan.ProjGame.ui.PlayerHUD;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.audio.Sound;

import java.util.ArrayList;
import java.util.List;

public class Monster {
    Sound hitSound = Gdx.audio.newSound(Gdx.files.internal("Sound/hit.wav"));

    private String type;
    private float hp, maxHp;
    private int potentialReward;
    protected float respawnTime, respawnTimer;
    private Vector2 position;
    private Rectangle bounds;
    private boolean isAlive = true;
    private Vector2 velocity;
    private float startX, endX;
    private float speed = 25f;

    private boolean selected = false;
    private boolean autoSelected = false;
    private boolean isStatic = false;
    private boolean canShootFireball = false;

    private IAnimationManager animationManager;
    private Enum<?> currentState;
    private float detectRange;
    private float attackCooldown = 1f;
    private float attackTimer = 0;
    private float attack;

    private List<Fireball> fireballs = new ArrayList<>();

    private PlayerHUD playerHUD;


    private List<FloatingText> floatingTexts = new ArrayList<>();
    private BitmapFont font = new BitmapFont();
    private OrthographicCamera camera;


    public Monster(String type, float hp, float respawnTime, Vector2 position, float attack, float detectRange,int potentialReward) {
        this.type = type;
        this.hp = this.maxHp = hp;
        this.respawnTime = respawnTime;
        this.attack = attack;
        this.detectRange = detectRange;
        this.potentialReward= potentialReward;
        this.position = position.cpy();

        setupAnimationManager(type);

        float width = (animationManager != null) ? animationManager.getScaledWidth() : 288;
        float height = (animationManager != null) ? animationManager.getScaledHeight() : 160;
        this.bounds = new Rectangle(position.x, position.y, width, height);

        this.startX = position.x - 25;
        this.endX = position.x + 25;
        this.velocity = new Vector2(speed, 0);

        this.isStatic = type.equalsIgnoreCase("Demonfly");
        this.canShootFireball = type.equalsIgnoreCase("Demonfly");
    }

    public void update(float deltaTime, Player player) {
        Vector2 playerPos = player.getPosition();

        if (!isAlive) {
            if (!currentState.equals(getDieState()))
                setState(getDieState());

            respawnTimer += deltaTime;
            if (respawnTimer >= respawnTime) {
                isAlive = true;
                hp = maxHp;
                respawnTimer = 0;
                position.x = (startX + endX) / 2f;
                setState(getWalkState());
            }

            animationManager.update(deltaTime);
            return;
        }

        float distance = player.getCenter().dst(getCenter());

        if (distance < detectRange) {
            setState(getAttackState());
            attackTimer += deltaTime;
            if (attackTimer >= attackCooldown) {
                if (canShootFireball) {
                    Vector2 dir = new Vector2(player.getPosition()).sub(position);
                    fireballs.add(new Fireball(position.cpy(), dir, attack));
                } else {
                    player.takeDamage(attack);
                }
                attackTimer = 0;
            }
        } else {
            if (isStatic) {
                setState(getWalkState());
            } else {
                setState(getWalkState());
                position.x += velocity.x * deltaTime;
                if (position.x < startX) {
                    position.x = startX;
                    velocity.x = speed;
                } else if (position.x > endX) {
                    position.x = endX;
                    velocity.x = -speed;
                }
            }
        }

        for (Fireball f : fireballs) f.update(deltaTime);
        for (Fireball f : fireballs) {
            if (f.isActive() && f.getBounds().overlaps(player.getBounds())) {
                player.takeDamage(f.getDamage());
                f.deactivate();
            }
        }
        fireballs.removeIf(f -> !f.isActive() || f.getPosition().dst(position) > 800);

        autoSelected = distance < 80f;

        bounds.set(position.x, position.y,
            animationManager.getScaledWidth(), animationManager.getScaledHeight());
        animationManager.update(deltaTime);
    }

    public void draw(SpriteBatch batch, Player player) {
        for (Fireball f : fireballs) f.draw(batch, player);
        if (isAlive || currentState.equals(getDieState())) {
            boolean flip = false;

            if("Demonfly".equalsIgnoreCase(type)){
                if (currentState.equals(getAttackState()) || currentState.equals(getDieState())) {
                    flip = player.getPosition().x > position.x;
                } else if (currentState.equals(getWalkState())) {
                    flip = velocity.x < 0;
                }
            }
            else{
                if (currentState.equals(getAttackState()) || currentState.equals(getDieState())) {
                    flip = player.getPosition().x < position.x;
                } else if (currentState.equals(getWalkState())) {
                    flip = velocity.x < 0;
                }
            }

            animationManager.draw(batch, position.x, position.y, flip);
        }
    }

    public void drawInfo(SpriteBatch batch, BitmapFont font) {
        if ((!selected && !autoSelected) || !isAlive) return;

        float barWidth = 100;
        float barHeight = 10;
        float barX = position.x + animationManager.getScaledWidth() / 2 - barWidth / 2;
        float barY = position.y + animationManager.getScaledHeight() + 10;

        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(barX - 2, barY - 2, barWidth + 4, barHeight + 4);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(barX, barY, barWidth * (hp / maxHp), barHeight);
        shapeRenderer.end();
        shapeRenderer.dispose();

        batch.begin();
        font.draw(batch, type, barX, barY + 25);
    }


    private void setState(Enum<?> newState) {
        if (!newState.equals(currentState)) {
            currentState = newState;
            animationManager.setState(newState);
        }
    }

    private Enum<?> getWalkState() {
        return (animationManager instanceof GolemAnimationManager) ? GolemState.WALK : EnemyState.WALK;
    }

    private Enum<?> getAttackState() {
        return (animationManager instanceof GolemAnimationManager) ? GolemState.ATTACK : EnemyState.ATTACK;
    }

    private Enum<?> getDieState() {
        return (animationManager instanceof GolemAnimationManager) ? GolemState.DIE : EnemyState.DIE;
    }

    public void takeDamage(float amount,Player player) {
        if (!isAlive) return;
        hp -= amount;
        if (hitSound != null) hitSound.play();
        if (hp <= 0) {
            isAlive = false;
            player.addPotential(potentialReward);
            respawnTimer = 0;
            if (playerHUD != null && camera != null) {
                Vector3 screenPos = new Vector3(getCenter(), 0);
                camera.project(screenPos);
                playerHUD.showPotentialGain(potentialReward, new Vector2(screenPos.x, screenPos.y));
            }
            if ("Doll".equalsIgnoreCase(this.getType())) {
                player.onKillDoll(); // ✅ chỉ tăng 1 lần khi chết
            }
            else if ("Minotaur".equalsIgnoreCase(this.getType())) {
                player.onKillBoar(); // ✅ chỉ tăng 1 lần khi chết
            }
            else if("Golem".equalsIgnoreCase(this.getType())||"Golem1".equalsIgnoreCase(this.getType())){
                player.onKillGolem();
            }
            else if("Demonfly".equalsIgnoreCase(this.getType())){
                player.onKillDemonfly();
            }
        }

    }

    public void setSize(float scale) {
        Vector2 centerBefore = getCenter();

        if (animationManager != null) {
            if (animationManager instanceof EnemyAnimationManager) {
                ((EnemyAnimationManager) animationManager).setScale(scale);
            } else if (animationManager instanceof GolemAnimationManager) {
                ((GolemAnimationManager) animationManager).setScale(scale);
            }
        }

        updateBounds();

        // Điều chỉnh position để giữ nguyên center
        Vector2 centerAfter = getCenter();
        Vector2 delta = centerBefore.sub(centerAfter);
        position.add(delta);

        updateBounds();
    }
    public void updateBounds() {
        bounds.set(position.x, position.y,
            animationManager.getScaledWidth(),
            animationManager.getScaledHeight());
    }

    public void playHitAnimation() {
        // Mặc định không làm gì; Boss sẽ override
    }

    protected void setupAnimationManager(String type) {
        if (type.equalsIgnoreCase("Golem")||type.equalsIgnoreCase("Golem1")) {
            GolemAnimationManager manager = new GolemAnimationManager(type);
            animationManager = manager;
            currentState = GolemState.WALK;
        } else {
            EnemyAnimationManager manager = new EnemyAnimationManager(type);
            animationManager = manager;
            currentState = EnemyState.WALK;
        }
    }

    public void revive(){
        isAlive=true;
        hp = maxHp;
        respawnTimer = 0;
    }

    public Vector2 getCenter() {
        return new Vector2(
            position.x + animationManager.getScaledWidth() / 2f,
            position.y + animationManager.getScaledHeight() / 2f
        );
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
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

    public float getAttack(){
        return attack;
    }

    public int getPotentialReward() {
        return potentialReward;
    }

    public float getCurrentHp() {
        return hp;
    }
    public float getMaxHp(){
        return maxHp;
    }
    public String getType() {
        return type;
    }
    public float getRespawnTime(){
        return respawnTime;
    }

    public void setPlayerHUD(PlayerHUD playerHUD) {
        this.playerHUD = playerHUD;
    }
    public void setCamera(OrthographicCamera camera) {
        this.camera = camera;
    }
}
