package io.github.some_example_name.entity.enemy;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import io.github.some_example_name.level.TileMap;

public class EliteBoss extends BaseEnemy {
    private static final float SIZE = 64f;
    private static final float SPEED = 50f;
    private static final float SMASH_COOLDOWN = 3f;
    private static final float SMASH_RANGE = 80f;
    private static final float SMASH_DAMAGE = 30f;

    private float smashTimer;
    private Texture healthBarBg;
    private Texture healthBarFill;

    public EliteBoss(float x, float y, TileMap tileMap) {
        super(x, y, SIZE, SIZE, 500f, 20f, tileMap, new Color(0.6f, 0.1f, 0.1f, 1f));
        smashTimer = SMASH_COOLDOWN;

        Pixmap bg = new Pixmap(100, 8, Pixmap.Format.RGBA8888);
        bg.setColor(Color.DARK_GRAY);
        bg.fill();
        healthBarBg = new Texture(bg);
        bg.dispose();

        Pixmap fill = new Pixmap(100, 8, Pixmap.Format.RGBA8888);
        fill.setColor(Color.RED);
        fill.fill();
        healthBarFill = new Texture(fill);
        fill.dispose();
    }

    @Override
    public void update(float delta) {
        velocity.y -= 25f * delta;

        if (player != null) {
            float px = position.x + size.x / 2f;
            float tx = player.getPosition().x + player.getSize().x / 2f;
            float dx = tx - px;

            if (Math.abs(dx) > 20) {
                velocity.x = (dx > 0 ? 1 : -1) * SPEED * delta;
            } else {
                velocity.x *= 0.8f;
            }

            smashTimer -= delta;
            if (smashTimer <= 0) {
                smashTimer = SMASH_COOLDOWN;

                float py = position.y + size.y / 2f;
                float ty = player.getPosition().y + player.getSize().y / 2f;
                if (Math.abs(ty - py) < SMASH_RANGE && Math.abs(dx) < SMASH_RANGE) {
                    float knockX = (dx > 0 ? 1 : -1) * 300f;
                    float knockY = 200f;
                    if (player instanceof io.github.some_example_name.entity.PlayerSlime) {
                        player.getVelocity().x += knockX;
                        player.getVelocity().y += knockY;
                    }
                }
            }
        }

        position.x += velocity.x;
        position.y += velocity.y;
        resolveTileCollision();
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);

        float barX = position.x + size.x / 2f - 50f;
        float barY = position.y + size.y + 10f;
        batch.draw(healthBarBg, barX, barY, 100, 8);

        float ratio = health / maxHealth;
        batch.draw(healthBarFill, barX, barY, 100 * ratio, 8);
    }

    @Override
    public void dispose() {
        super.dispose();
        healthBarBg.dispose();
        healthBarFill.dispose();
    }
}
