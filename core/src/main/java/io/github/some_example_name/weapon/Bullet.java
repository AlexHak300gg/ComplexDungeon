package io.github.some_example_name.weapon;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.level.TileMap;

public class Bullet extends Entity {
    private static final float BULLET_SIZE = 8f;
    private static final float LIFESPAN = 3f;

    private float damage;
    private float lifespan;
    private Texture texture;
    private Sprite sprite;
    private TileMap tileMap;
    private float elementDamage;
    private boolean fromPlayer;

    public Bullet(float x, float y, float vx, float vy, float damage, TileMap tileMap) {
        super(x, y, BULLET_SIZE, BULLET_SIZE);
        velocity.set(vx, vy);
        this.damage = damage;
        this.tileMap = tileMap;
        this.lifespan = LIFESPAN;
        this.elementDamage = 0;
        this.fromPlayer = true;

        Color c = fromPlayer ? Color.YELLOW : new Color(0.2f, 0.9f, 0.2f, 1f);
        Pixmap pixmap = new Pixmap((int)BULLET_SIZE, (int)BULLET_SIZE, Pixmap.Format.RGBA8888);
        pixmap.setColor(c);
        pixmap.fillCircle((int)BULLET_SIZE / 2, (int)BULLET_SIZE / 2, (int)BULLET_SIZE / 2 - 1);
        texture = new Texture(pixmap);
        pixmap.dispose();
        sprite = new Sprite(texture);
    }

    public void setFromPlayer(boolean v) {
        this.fromPlayer = v;
        Color c = v ? Color.YELLOW : new Color(0.2f, 0.9f, 0.2f, 1f);
        Pixmap pixmap = new Pixmap((int)BULLET_SIZE, (int)BULLET_SIZE, Pixmap.Format.RGBA8888);
        pixmap.setColor(c);
        pixmap.fillCircle((int)BULLET_SIZE / 2, (int)BULLET_SIZE / 2, (int)BULLET_SIZE / 2 - 1);
        texture.dispose();
        texture = new Texture(pixmap);
        pixmap.dispose();
        sprite.setTexture(texture);
    }

    public void setElementDamage(float val) { this.elementDamage = val; }
    public float getDamage() { return damage + elementDamage; }
    public boolean isFromPlayer() { return fromPlayer; }

    @Override
    public void update(float delta) {
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;

        lifespan -= delta;
        if (lifespan <= 0) { kill(); return; }

        int tx = (int)(position.x / TileMap.TILE_SIZE);
        int ty = (int)(position.y / TileMap.TILE_SIZE);
        if (tileMap.isSolid(tx, ty)) {
            kill();
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        sprite.setPosition(position.x, position.y);
        sprite.setSize(size.x, size.y);
        sprite.draw(batch);
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
