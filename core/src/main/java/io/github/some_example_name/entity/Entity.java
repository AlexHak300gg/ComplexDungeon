package io.github.some_example_name.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
    protected Vector2 position;
    protected Vector2 velocity;
    protected Vector2 size;
    protected boolean alive;

    public Entity(float x, float y, float width, float height) {
        position = new Vector2(x, y);
        velocity = new Vector2();
        size = new Vector2(width, height);
        alive = true;
    }

    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, size.x, size.y);
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public Vector2 getSize() {
        return size;
    }

    public boolean isAlive() {
        return alive;
    }

    public void kill() {
        alive = false;
    }

    public abstract void update(float delta);

    public abstract void draw(SpriteBatch batch);

    public void dispose() {
    }
}
