package io.github.some_example_name.entity.enemy;

import com.badlogic.gdx.graphics.Color;
import io.github.some_example_name.entity.EntityManager;
import io.github.some_example_name.level.TileMap;
import io.github.some_example_name.weapon.Bullet;

public class RangedSpitter extends BaseEnemy {
    private static final float SIZE = 24f;
    private static final float CHASE_SPEED = 80f;
    private static final float PREFERRED_DIST = 200f;
    private static final float FIRE_COOLDOWN = 1.5f;

    private float fireTimer;
    private EntityManager entityManager;

    public RangedSpitter(float x, float y, TileMap tileMap) {
        super(x, y, SIZE, SIZE, 60f, 15f, tileMap, new Color(0.3f, 0.8f, 0.1f, 1f));
        fireTimer = FIRE_COOLDOWN;
    }

    public void setEntityManager(EntityManager em) { this.entityManager = em; }
    public EntityManager getEntityManager() { return entityManager; }

    @Override
    public void update(float delta) {
        velocity.y -= 25f * delta;

        if (player != null) {
            float px = position.x + size.x / 2f;
            float py = position.y + size.y / 2f;
            float tx = player.getPosition().x + player.getSize().x / 2f;
            float ty = player.getPosition().y + player.getSize().y / 2f;
            float dx = tx - px;
            float dy = ty - py;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);

            if (dist > PREFERRED_DIST + 30) {
                if (dist > 0) velocity.x = (dx / dist) * CHASE_SPEED * delta;
            } else if (dist < PREFERRED_DIST - 30) {
                if (dist > 0) velocity.x = -(dx / dist) * CHASE_SPEED * delta;
            } else {
                velocity.x *= 0.8f;
            }

            fireTimer -= delta;
            if (fireTimer <= 0 && entityManager != null) {
                float bulletSpeed = 250f;
                float bx = px;
                float by = py;
                float blen = (float) Math.sqrt(dx * dx + dy * dy);
                if (blen > 0) {
                    Bullet b = new Bullet(bx, by,
                        (dx / blen) * bulletSpeed,
                        (dy / blen) * bulletSpeed,
                        damage, tileMap);
                    b.setFromPlayer(false);
                    entityManager.add(b);
                }
                fireTimer = FIRE_COOLDOWN;
            }
        }

        position.x += velocity.x;
        position.y += velocity.y;
        resolveTileCollision();
    }
}
