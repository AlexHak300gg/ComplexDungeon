package io.github.some_example_name.entity.enemy;

import com.badlogic.gdx.graphics.Color;
import io.github.some_example_name.level.TileMap;

public class SwarmBug extends BaseEnemy {
    private static final float SPEED = 200f;
    private static final float SIZE = 20f;

    public SwarmBug(float x, float y, TileMap tileMap) {
        super(x, y, SIZE, SIZE, 30f, 10f, tileMap, new Color(0.9f, 0.4f, 0.1f, 1f));
    }

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

            if (dist > 0) {
                velocity.x = (dx / dist) * SPEED * delta;
            }
            if (dy > 20) {
                velocity.y = 200f * delta;
            }
        }

        position.x += velocity.x;
        position.y += velocity.y;
        resolveTileCollision();
    }
}
