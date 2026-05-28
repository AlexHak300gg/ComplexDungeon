package io.github.some_example_name.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import java.util.Iterator;

public class EntityManager {
    private final Array<Entity> entities;

    public EntityManager() {
        entities = new Array<>();
    }

    public void add(Entity entity) {
        entities.add(entity);
    }

    public void remove(Entity entity) {
        entities.removeValue(entity, true);
    }

    public void updateAll(float delta) {
        Iterator<Entity> iter = entities.iterator();
        while (iter.hasNext()) {
            Entity e = iter.next();
            if (!e.isAlive()) {
                iter.remove();
                continue;
            }
            e.update(delta);
        }
    }

    public void drawAll(SpriteBatch batch) {
        for (Entity e : entities) {
            if (e.isAlive()) {
                e.draw(batch);
            }
        }
    }

    public void clear() {
        entities.clear();
    }

    public Array<Entity> getEntities() {
        return entities;
    }
}
