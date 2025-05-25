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
    private List<Monster> monsters; // danh sách quái để tự chọn
    private boolean manuallySelected = false;


    public SkillSlotManager(Player player) {
        this.player = player;

        // Khởi tạo kỹ năng cơ bản
        Skill punch = new Skill("Punch", new Texture("Skills/punch.png"), 0.5f, 30f, 1.0f, "Cú đấm cơ bản");
        Skill fireball = new Skill("Fireball",new Texture("Skills/fireball.png"),1.5f,150f,2.5f,"Chưởng nè");
        skillSlots.add(new SkillSlot(punch, 1000, 20));
        skillSlots.add(new SkillSlot(fireball,1050,20));
    }

    public void setMonsters(List<Monster> monsters) {
        this.monsters = monsters;
    }

    public void update(float delta) {
        for (SkillSlot slot : skillSlots) {
            slot.update(delta);
        }

        autoSelectNearestMonster(); // Tự chọn quái gần nhất
        handleSkillHotkeys(delta);  // Phím tắt dùng kỹ năng
    }

    private void autoSelectNearestMonster() {
        if(manuallySelected)return;
        if (monsters == null) return;

        Monster nearest = null;
        float minDistX = Float.MAX_VALUE;

        for (Monster m : monsters) {
            float dx = Math.abs(player.getPosition().x - m.getPosition().x);
            float dy = Math.abs(player.getPosition().y - m.getPosition().y);

            if (m.isAlive() && dx < 200f && dy < 80f && dx < minDistX) {
                nearest = m;
                minDistX = dx;
            }
        }

        for (Monster m : monsters) m.setSelected(false);

        if (nearest != null) {
            nearest.setSelected(true);
            player.setSelectedTarget(nearest);
        } else {
            player.setSelectedTarget(null);
        }
    }


    private void handleSkillHotkeys(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            useSkillAtIndex(0, delta);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)){
            useSkillAtIndex(1,delta);
        }

    }

    public void useSkillAtIndex(int index, float delta) {
        if (index < 0 || index >= skillSlots.size()) return;

        SkillSlot slot = skillSlots.get(index);

        if (!slot.canUse()) {
            System.out.println("Skill đang hồi!");
            return;
        }

        Monster selectedTarget = player.getSelectedTarget(); // ✅ Lấy target từ player

        if (selectedTarget == null) {
            System.out.println("Chưa chọn mục tiêu để tấn công!");
            return;
        }

        System.out.println("use skill " + slot.getSkill().getName());
        player.attackWithSkill(slot.getSkill(), selectedTarget, delta);
        if (slot.getSkill().getName().equalsIgnoreCase("Fireball")) {
            player.setFireballTextures(
                new Texture("Skills/fireball_fly.png"),
                new Texture("Skills/fireball_explode.png")
            );
        }
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

    public void setManualSelection(boolean selected) {
        this.manuallySelected = selected;
    }

    public void handleClick(float x, float y) {
        for (SkillSlot slot : skillSlots) {
            if (slot.getBounds().contains(x, y)) {
                if (!slot.canUse()) {
                    System.out.println("Skill đang hồi");
                    return;
                }

                Monster selectedTarget = player.getSelectedTarget(); // ✅ Lấy từ player

                if (selectedTarget == null) {
                    System.out.println("Chưa chọn mục tiêu để tấn công!");
                    return;
                }

                slot.activate();
                System.out.println("Dùng kỹ năng: " + slot.getSkill().getName());
                player.attackWithSkill(slot.getSkill(), selectedTarget, Gdx.graphics.getDeltaTime());
                if (slot.getSkill().getName().equalsIgnoreCase("Fireball")) {
                    player.setFireballTextures(
                        new Texture("Skills/fireball_fly.png"),
                        new Texture("Skills/fireball_explode.png")
                    );
                }
                break;
            }
        }
    }
}
