package io.github.some_example_name.weapon;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.entity.PlayerSlime;
import io.github.some_example_name.level.TileMap;

public class AutoTurret extends Weapon {
    private static final float RADIUS = 400f;
    private static final float BULLET_SPEED = 400f;

    private Texture turretTex;
    private Sprite turretSprite;
    private TileMap tileMap;

    public AutoTurret(PlayerSlime owner, EntityManager em, TileMap tileMap) {
        super("\u0410\u0432\u0442\u043E\u0442\u0443\u0440\u0435\u043B\u044C", 0.3f, 10f, owner, em);
        this.tileMap = tileMap;

        Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.3f, 0.3f, 0.3f, 1f));
        pixmap.fill();
        pixmap.setColor(new Color(0.5f, 0.5f, 0.5f, 1f));
        pixmap.drawRectangle(0, 0, 16, 16);
        turretTex = new Texture(pixmap);
        pixmap.dispose();
        turretSprite = new Sprite(turretTex);
    }

    @Override
    protected void fire(Array<Entity> enemies) {
        float px = owner.getPosition().x + owner.getSize().x / 2f;
        float py = owner.getPosition().y + owner.getSize().y / 2f;
        float targetX = px + owner.getFacingDirection() * 100f;
        float targetY = py;

        if (enemies != null) {
            Entity nearest = null;
            float nearestDist = RADIUS * RADIUS;
            for (Entity e : enemies) {
                float dx = e.getPosition().x + e.getSize().x / 2f - px;
                float dy = e.getPosition().y + e.getSize().y / 2f - py;
                float d = dx * dx + dy * dy;
                if (d < nearestDist) {
                    nearestDist = d;
                    nearest = e;
                }
            }
            if (nearest != null) {
                targetX = nearest.getPosition().x + nearest.getSize().x / 2f;
                targetY = nearest.getPosition().y + nearest.getSize().y / 2f;
            }
        }

        float dx = targetX - px;
        float dy = targetY - py;
        float len = (float)Math.sqrt(dx * dx + dy * dy);
        if (len == 0) return;

        float vx = (dx / len) * BULLET_SPEED;
        float vy = (dy / len) * BULLET_SPEED;

        float bulletDamage = damage + owner.getElementalDamage();
        Bullet bullet = new Bullet(px, py, vx, vy, bulletDamage, tileMap);

        entityManager.add(bullet);
    }

    @Override
    public void draw(SpriteBatch batch) {
        float px = owner.getPosition().x + owner.getSize().x / 2f - 8f;
        float py = owner.getPosition().y + owner.getSize().y - 4f;
        turretSprite.setPosition(px, py);
        turretSprite.draw(batch);
    }

    @Override
    public void dispose() {
        turretTex.dispose();
    }
}
