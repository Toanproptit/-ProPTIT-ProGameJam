package com.Trongtoan.ProjGame.skills;

import com.badlogic.gdx.graphics.Texture;

public class Skill {
    private String name;
    private Texture icon;
    private float cooldown;
    private float range;
    private float damageMultiplier; // hệ số cộng dồn sát thương
    private String description;
    private float cooldownTimer =0;

    public Skill(String name, Texture icon, float cooldown,float range, float damageMultiplier, String description) {
        this.name = name;
        this.icon = icon;
        this.range = range;
        this.cooldown = cooldown;
        this.damageMultiplier = damageMultiplier;
        this.description = description;
    }

    public void update(float delta){
        if(cooldownTimer>0){
            cooldownTimer-= delta;
        }
    }
    public void startCooldown() {
        cooldownTimer = cooldown;
    }
    public boolean isReady() {
        return cooldownTimer <= 0;
    }
    public float getRange() {
        return range;
    }
    public String getName() { return name; }
    public Texture getIcon() { return icon; }
    public float getCooldown() { return cooldown; }
    public float getDamageMultiplier() { return damageMultiplier; }
    public String getDescription() { return description; }
}
