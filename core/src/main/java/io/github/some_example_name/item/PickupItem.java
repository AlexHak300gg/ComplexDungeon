package io.github.some_example_name.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.entity.Entity;
import io.github.some_example_name.entity.PlayerSlime;

public abstract class PickupItem extends Entity {
    protected String label;
    protected Texture texture;
    protected Sprite sprite;
    protected boolean magnetized;
    protected Vector2 magnetTarget;

    public PickupItem(float x, float y, Color color, String label) {
        super(x, y, 16, 16);
        this.label = label;
        this.magnetized = false;
        this.magnetTarget = new Vector2();

        Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillCircle(8, 8, 7);
        pixmap.setColor(Color.WHITE);
        pixmap.drawCircle(8, 8, 7);
        texture = new Texture(pixmap);
        pixmap.dispose();
        sprite = new Sprite(texture);
    }

    public String getLabel() { return label; }

    public void setMagnetized(float tx, float ty) {
        magnetized = true;
        magnetTarget.set(tx, ty);
    }

    public void clearMagnetized() {
        magnetized = false;
    }

    @Override
    public void update(float delta) {
        if (magnetized) {
            Vector2 dir = new Vector2(magnetTarget).sub(position.x + 8, position.y + 8);
            float len = dir.len();
            if (len > 4f) {
                dir.nor().scl(300f * delta);
                position.add(dir);
            } else {
                magnetized = false;
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        sprite.setPosition(position.x, position.y);
        sprite.setSize(size.x, size.y);
        sprite.draw(batch);
    }

    public abstract void apply(PlayerSlime player);

    @Override
    public void dispose() {
        texture.dispose();
    }
}
