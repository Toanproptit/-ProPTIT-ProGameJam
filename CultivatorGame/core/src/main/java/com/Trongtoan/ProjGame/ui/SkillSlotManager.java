package com.Trongtoan.ProjGame.ui;

import com.Trongtoan.ProjGame.skills.Skill;
import com.Trongtoan.ProjGame.entities.Monster;
import com.Trongtoan.ProjGame.entities.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

public class SkillSlotManager {
    private List<SkillSlot> skillSlots = new ArrayList<>();
    private Player player;
    private Monster selectedTarget;

    public SkillSlotManager(Player player) {
        this.player = player;
        Skill punch = new Skill("Punch", new Texture("Skills/punch.png"), 2f, 10f, 1.0f, "Cú đấm cơ bản");
        skillSlots.add(new SkillSlot(punch, 20, 20));
    }

    public void setSelectedTarget(Monster target) {
        this.selectedTarget = target;
    }

    public void update(float delta) {
        for (SkillSlot slot : skillSlots) {
            slot.update(delta);
        }
        handleSkillHotkeys(delta); // tách phần xử lý phím tắt ra đây
    }

    private void handleSkillHotkeys(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            useSkillAtIndex(0, delta);
        }
    }

    public void useSkillAtIndex(int index, float delta) {
        if (index < 0 || index >= skillSlots.size()) return;

        SkillSlot slot = skillSlots.get(index);

        if (!slot.canUse()) {
            System.out.println("Skill đang hồi!");
            return;
        }

        if (selectedTarget == null) {
            System.out.println("Chưa chọn mục tiêu để tấn công!");
            return;
        }

        System.out.println("use skill " + slot.getSkill().getName());
        player.attackWithSkill(slot.getSkill(), selectedTarget, delta);
        slot.activate();
    }

    public void draw(SpriteBatch batch) {
        for (SkillSlot slot : skillSlots) {
            slot.draw(batch);
        }
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void handleClick(float x, float y) {
        for (SkillSlot slot : skillSlots) {
            if (slot.getBounds().contains(x, y)) {
                if (slot.canUse()) {
                    if (selectedTarget == null) {
                        System.out.println("Chưa chọn mục tiêu để tấn công!");
                        return;
                    }

                    slot.activate();
                    System.out.println("Dùng kỹ năng: " + slot.getSkill().getName());
                    player.attackWithSkill(slot.getSkill(), selectedTarget, Gdx.graphics.getDeltaTime());
                } else {
                    System.out.println("Skill đang hồi");
                }
                break;
            }
        }
    }

}
