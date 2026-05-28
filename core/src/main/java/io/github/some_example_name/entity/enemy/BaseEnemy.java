package io.github.some_example_name.entity.enemy;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.level.TileMap;
import io.github.some_example_name.level.TileType;

public abstract class BaseEnemy extends Entity {
    protected float maxHealth;
    protected float health;
    protected float damage;
    protected TileMap tileMap;
    protected Entity player;

    protected Texture texture;
    protected Sprite sprite;
    protected Color color;

    public BaseEnemy(float x, float y, float width, float height, float health, float damage, TileMap tileMap, Color color) {
        super(x, y, width, height);
        this.maxHealth = health;
        this.health = health;
        this.damage = damage;
        this.tileMap = tileMap;
        this.color = color;
        createTexture();
    }

    private void createTexture() {
        Pixmap pixmap = new Pixmap((int)size.x, (int)size.y, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        pixmap.setColor(Color.WHITE);
        pixmap.drawRectangle(0, 0, (int)size.x - 1, (int)size.y - 1);
        texture = new Texture(pixmap);
        pixmap.dispose();
        sprite = new Sprite(texture);
    }

    public void setPlayer(Entity p) { this.player = p; }

    public void applyDamage(float amount) {
        health -= amount;
        if (health <= 0) { health = 0; kill(); }
    }

    public void applyKnockback(float kx, float ky) {
        velocity.x += kx;
        velocity.y += ky;
    }

    public float getHealth() { return health; }
    public float getMaxHealth() { return maxHealth; }
    public float getDamage() { return damage; }

    protected void resolveTileCollision() {
        Rectangle bounds = getBounds();
        int left = (int)(bounds.x / TileMap.TILE_SIZE);
        int right = (int)((bounds.x + bounds.width - 1) / TileMap.TILE_SIZE);
        int bottom = (int)(bounds.y / TileMap.TILE_SIZE);
        int top = (int)((bounds.y + bounds.height - 1) / TileMap.TILE_SIZE);

        for (int tx = left; tx <= right; tx++) {
            for (int ty = bottom; ty <= top; ty++) {
                if (tileMap.isSolid(tx, ty)) {
                    if (tileMap.getTile(tx, ty) == TileType.PLATFORM) continue;
                    float overlapX = Math.min(bounds.x + bounds.width, (tx + 1) * TileMap.TILE_SIZE)
                        - Math.max(bounds.x, tx * TileMap.TILE_SIZE);
                    float overlapY = Math.min(bounds.y + bounds.height, (ty + 1) * TileMap.TILE_SIZE)
                        - Math.max(bounds.y, ty * TileMap.TILE_SIZE);

                    if (overlapX < overlapY) {
                        if (velocity.x > 0) position.x = tx * TileMap.TILE_SIZE - bounds.width;
                        else position.x = (tx + 1) * TileMap.TILE_SIZE;
                        velocity.x = 0;
                    } else {
                        if (velocity.y > 0) position.y = ty * TileMap.TILE_SIZE - bounds.height;
                        else position.y = (ty + 1) * TileMap.TILE_SIZE;
                        velocity.y = 0;
                    }
                }
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        sprite.setPosition(position.x, position.y);
        sprite.setSize(size.x, size.y);
        sprite.draw(batch);
    }

    public void dispose() {
        if (texture != null) texture.dispose();
    }
}
