
package com.Trongtoan.ProjGame.ui;

import com.Trongtoan.ProjGame.skills.Skill;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class SkillSlot {
    private Skill skill;
    private Rectangle bounds;
    private boolean isCoolingDown = false;
    private float timer = 0;

    public SkillSlot(Skill skill, float x, float y) {
        this.skill = skill;
        this.bounds = new Rectangle(x, y, 40, 40);
    }

    public void update(float delta) {
        skill.update(delta); // cập nhật cooldown
    }

    public void draw(SpriteBatch batch) {
        batch.draw(skill.getIcon(), bounds.x, bounds.y, bounds.width, bounds.height);
        if (!skill.isReady()) {
            Color original = batch.getColor().cpy();
            batch.setColor(0, 0, 0, 0.5f);
            batch.draw(skill.getIcon(), bounds.x, bounds.y, bounds.width, bounds.height);
            batch.setColor(original);
        }

        batch.end();
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BROWN);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();
        shapeRenderer.dispose();
        batch.begin();
    }

    public boolean canUse() {
        return skill.isReady();
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void activate() {
        skill.startCooldown();
    }
    public Skill getSkill() {
        return skill;
    }

}
